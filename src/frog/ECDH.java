package frog;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Scanner;

public class ECDH {
    /* EC Diffie–Hellman key exchange */
    public static void generate( String pathPublicKey, String pathPrivateKey) throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        System.out.println("generating:");
        System.out.println("Java generate an EC keypair");
        String ecdhCurvenameString = "secp256r1";
        // standard curvennames
        // secp256r1 [NIST P-256, X9.62 prime256v1]
        // secp384r1 [NIST P-384]
        // secp521r1 [NIST P-521]
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "SunEC");
        ECGenParameterSpec ecParameterSpec = new ECGenParameterSpec(ecdhCurvenameString);
        keyPairGenerator.initialize(ecParameterSpec);
        KeyPair ecdhKeyPair = keyPairGenerator.genKeyPair();
        PrivateKey privateKey = ecdhKeyPair.getPrivate();
        PublicKey publicKey = ecdhKeyPair.getPublic();
        byte[] PubEncoded = publicKey.getEncoded();
        byte[] PvtEncoded = privateKey.getEncoded();


        System.out.println(" public: " + new BigInteger(PubEncoded));
        System.out.println(" private: " + new BigInteger(PvtEncoded));

        write2File(pathPublicKey,new BigInteger(PubEncoded));
        write2File(pathPrivateKey,new BigInteger(PvtEncoded));
    }

    public static void write2File(String path, BigInteger element) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(new File(path));
        printWriter.println(element);
        printWriter.close();
    }
}

