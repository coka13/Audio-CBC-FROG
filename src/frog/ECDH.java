package frog;

import javax.crypto.KeyAgreement;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ECDH {
    /* EC Diffie–Hellman key exchange */
    public static KeyPair generate( String pathPublicKey, String pathPrivateKey) throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        System.out.println("generating:");
        System.out.println("Java generate an EC keypair");
        String ecdhCurvenameString = "secp256r1";
        // standard curvennames
        // secp256r1 [NIST P-256, X9.62 prime256v1]
        // secp384r1 [NIST P-384]
        // secp521r1 [NIST P-521]
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecParameterSpec = new ECGenParameterSpec(ecdhCurvenameString);
        keyPairGenerator.initialize(ecParameterSpec);
        KeyPair ecdhKeyPair = keyPairGenerator.genKeyPair();
        PrivateKey privateKey = ecdhKeyPair.getPrivate();
        PublicKey publicKey = ecdhKeyPair.getPublic();
        byte[] PubEncoded = publicKey.getEncoded();
        byte[] PvtEncoded = privateKey.getEncoded();

        // Public & private keys
        System.out.println(" public: " + (PubEncoded));
        System.out.println(" private: " + (PvtEncoded));



        write2File(pathPublicKey,(PubEncoded).toString());
        write2File(pathPrivateKey,(PvtEncoded).toString());

        return ecdhKeyPair;



    }

    public static void write2File(String path, String element) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(new File(path));
        printWriter.println(element);
        printWriter.close();
    }

    public static KeyAgreement keyAgreement(KeyPair ecdhKeyPair, PublicKey otherPublicKey) throws InvalidKeyException, NoSuchAlgorithmException {
        KeyAgreement ka = KeyAgreement.getInstance("ECDH");
        ka.init(ecdhKeyPair.getPrivate());
        ka.doPhase(otherPublicKey, true);
        return ka;
    }
    public static void shareSecret(KeyAgreement ka, PublicKey otherPublicKey,PublicKey ourPublicKey,String pathSecretKey) throws NoSuchAlgorithmException, FileNotFoundException {
        // Read shared secret
        byte[] sharedSecret = ka.generateSecret();

        // Derive a key from the shared secret and both public keys
        MessageDigest hash = MessageDigest.getInstance("SHA-256");
        hash.update(sharedSecret);
        // Simple deterministic ordering
        List<ByteBuffer> keys = Arrays.asList(ByteBuffer.wrap(ourPublicKey.getEncoded()), ByteBuffer.wrap(otherPublicKey.getEncoded()));
        Collections.sort(keys);
        hash.update(keys.get(0));
        hash.update(keys.get(1));

        byte[] derivedKey = hash.digest();
        System.out.println("Secret: "+sharedSecret);
        write2File(pathSecretKey,(derivedKey).toString());
    }
}

