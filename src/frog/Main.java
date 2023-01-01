package frog;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.System.Logger;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

import javax.crypto.spec.IvParameterSpec;
import javax.print.attribute.standard.Media;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
//import sun.audio.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.spi.AudioFileWriter;

import com.riigsoft.captcha.SimpleCaptcha;

import cn.apiclub.captcha.Captcha;

import java.io.File;

public class Main {
	static String pathSign = "Sign.txt";
	static String pathPublicKey = "PublicKey.txt";
	static String pathPrivateKey = "PrivateKey.txt";
	static String pathFile = "peeper5sec.wav";
	static String otp;
	static AudioFormat format;
	static byte[] original_IV;
	static String Captcha;

	public static void main(String[] args)
			throws InvalidKeyException, UnsupportedAudioFileException, IOException, NoSuchAlgorithmException {

		/* Generate OTP */
		otp = OTP.genOTP(4);
		System.out.println(otp + "\n");

		/* Generating IV */
		int ivSize = 16;
		byte[] iv = new byte[ivSize];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);
		original_IV = iv;

		/* Create output text file of console */
		FileOutputStream file = new FileOutputStream("output.txt");
		TeePrintStream tee = new TeePrintStream(file, System.out);
		System.setOut(tee);

		/* Create output files */
		File encryptedFile = new File("encrypted.txt");
		File decryptedFile = new File("decrypted.txt");
		Scanner sc = new Scanner(System.in);
		File decryptedSoundFile = new File("dec.wav");
		File encryptedSoundFile = new File("enc.wav");
		File soundFile = new File("peeper5sec.wav");
		AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);

		/* Insert DH key */
		System.out.println("Enter <bits in q> - generate keys");
		int numOfbits = sc.nextInt();
		signature.generate(numOfbits, pathPublicKey, pathPrivateKey); /* Generates user key using DH */
		String k = readFile(pathPrivateKey, StandardCharsets.UTF_8); /* Reading secret key from file */
		sc.nextLine();
		System.out.println("Press any key to start...");
		sc.nextLine();
		byte[] key = k.getBytes();
		Object internalKey = frog_Algorithm.makeKey(key);

		/* Reading sound file */
		byte[][] fileInByte = split(ais.readAllBytes(), frog_Algorithm.BLOCK_SIZE);
		format = ais.getFormat();
		if (fileInByte == null)
			fileInByte[0] = ais.readAllBytes();

		// Encryption */
		byte[] enc = encryptedSoundFile(encryptedFile, encryptedSoundFile, fileInByte, iv, internalKey);

		int type = 0;
		boolean loop_flag = true;
		boolean is_decrypted = false;
		boolean is_signed = false;
		while (loop_flag) {
			System.out.println("\n");
			System.out.println("0 - Decrypt file");
			System.out.println("1 - Make signature");
			System.out.println("2 - Check signature");
			System.out.println("3 - View public key");
			System.out.println("4 - View private key");
			System.out.println("5 - View signature");
			System.out.println("6 - Open encrypted file");
			System.out.println("7 - Open decrypted file");
			System.out.println("8 - EXIT");
			type = sc.nextInt();

			switch (type) {
			case 0:
				if(is_signed==true) {
				/* Generate captcha and open the image file */
				Captcha c = SimpleCaptcha.createCaptcha(200, 80);
				SimpleCaptcha.createImage(c);
				Captcha = (c.getAnswer());
				openFile("captcha.png");
				/* Check the captcha */
				while (true) {
					System.out.println("Please enter the captcha:");
					if (sc.next().equals(Captcha)) {
						System.out.println("Correct captcha!");
						break;
					} else {
						System.out.println("Wrong captcha!");
					}
				}
				/* Enter your OTP before decryption */
				OTP.checkOTP(otp);

				/* Decryption */
				
				if (signature.checkSign(pathFile, pathPublicKey, pathSign)) {
					decryptedSoundFile(decryptedFile, decryptedSoundFile, fileInByte, enc, internalKey);
					is_decrypted = true;
					/* Check sound */
					System.out.println("Play decrypted file? 1 - Yes\nElse: press any key to continue...");
					type = sc.nextInt();
					if (type == 1) {
						playSound(decryptedSoundFile);
					}
				}
			}
				else {
					System.out.println("Please sign the file!");
				}

				break;
			/* Schnorr signature */
			case 1:
				signature.makeSign(pathFile, pathPublicKey, pathPrivateKey, pathSign);
				openFile(pathSign);
				is_signed=true;
				break;
			case 2:
				if(is_signed==true) {
				signature.checkSign(pathFile, pathPublicKey, pathSign);
				}
				else {
					System.out.println("The file does not contain a signature!");
				}
				break;
			/* Open public key txt file */
			case 3:
				openFile(pathPublicKey);
				break;
			/* Open private key txt file */
			case 4:
				openFile(pathPrivateKey);
				break;
			/* Open signature txt file */
			case 5:
				if(is_signed==true) {
				
				openFile(pathSign);
				}
				else {
					System.out.println("There is no signature!");
				}
				break;
			/* Open encrypted wav file */
			case 6:
				openFile("enc.wav");
				break;
			/* Open decrypted wav file */
			case 7:
				if (is_decrypted == true)
					openFile("dec.wav");
				else
					System.out.println("There is no decrypted file");
				break;
			/* Open console output txt file and exit */
			case 8:
				System.out.println("Goodbye!");
				openFile("output.txt");
				loop_flag = false;
				break;
			}
		}
		sc.close();
		ais.close();

	}

	/* CBC Encryption */
	public static byte[] encryptedSoundFile(File encryptedFile, File encryptedSoundFile, byte[][] fileInByte, byte[] iv,
			Object internalKey) throws IOException {
		byte enc[] = null;
		FileOutputStream fosEnc = new FileOutputStream(encryptedFile);
		DataOutputStream dosEnc = new DataOutputStream(fosEnc);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		for (int i = 0; i < fileInByte.length; i++) {
			/* CBC */
			for (int j = 0; j < frog_Algorithm.BLOCK_SIZE; j++)
				iv[j] = (byte) (iv[j] ^ fileInByte[i][j]);

			iv = frog_Algorithm.blockEncrypt(iv, 0, internalKey);
			outputStream.write(iv);
		}
		enc = outputStream.toByteArray();

		fosEnc.write(enc);
		InputStream b_in2 = new ByteArrayInputStream(enc);

		/* Writing encrypted file to wav file */
		try {
			dosEnc.write(enc);
			AudioInputStream stream = new AudioInputStream(b_in2, format, enc.length);
			AudioSystem.write(stream, Type.WAVE, encryptedSoundFile);
		} finally {
		}
		;

		fosEnc.close();
		dosEnc.close();
		return enc;
	}

	/* CBC Decryption */
	public static void decryptedSoundFile(File decryptedFile, File decryptedSoundFile, byte[][] fileInByte, byte[] enc,
			Object internalKey) throws IOException {

		byte[][] decFileInByte = split(enc, frog_Algorithm.BLOCK_SIZE);
		FileOutputStream fosDecSound = new FileOutputStream(decryptedSoundFile.getAbsolutePath());
		ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
		FileOutputStream fosDecTxt = new FileOutputStream(decryptedFile);

		byte[] iv = original_IV;
		byte[][] p_text_res = decFileInByte;
		for (int i = 0; i < p_text_res.length; i++) {
			byte[] res_cypher = frog_Algorithm.blockDecrypt(decFileInByte[i], 0, internalKey);
			/* CBC */
			for (int j = 0; j < res_cypher.length; j++) {
				if (i == 0)
					res_cypher[j] = (byte) (res_cypher[j] ^ iv[j]);
				else {
					res_cypher[j] = (byte) (res_cypher[j] ^ decFileInByte[i - 1][j]);
				}

			}
			outputStream2.write(res_cypher);
		}

		byte[] dec = outputStream2.toByteArray();
		fosDecTxt.write(dec);
		fosDecTxt.close();

		InputStream b_in = new ByteArrayInputStream(dec);
		try {
			DataOutputStream dos = new DataOutputStream(fosDecSound);
			AudioInputStream stream = new AudioInputStream(b_in, format, dec.length);
			dos.write(dec);
			AudioSystem.write(stream, Type.WAVE, decryptedSoundFile);
			dos.close();
		} finally {
		}
		;

		fosDecSound.close();
	}

	/* Open file */
	public static void openFile(String fileName) {
		try {
			/* Constructor of file class having file as argument */
			File file = new File(fileName);
			if (!Desktop.isDesktopSupported()) /* Check if Desktop is supported by Platform or not */
			{
				System.out.println("Not supported");
				return;
			}
			Desktop desktop = Desktop.getDesktop();
			if (file.exists()) /* Checks file exists or not */
				desktop.open(file); /* Opens the specified file */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Read file contents into string */
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	/* Split byte array to chunks */
	public static byte[][] split(byte[] source, int chunksize) {

		if (source.length < frog_Algorithm.BLOCK_SIZE)
			return null;
		byte[][] ret = new byte[(int) Math.ceil(source.length / (double) chunksize)][chunksize];

		int start = 0;

		for (int i = 0; i < ret.length; i++) {
			ret[i] = Arrays.copyOfRange(source, start, start + chunksize);
			start += chunksize;
		}
		return ret;
	}

	/* Play sound file */
	public static void playSound(File decryptedSoundFile) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(decryptedSoundFile));
			clip.start();
			Thread.sleep(5000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}