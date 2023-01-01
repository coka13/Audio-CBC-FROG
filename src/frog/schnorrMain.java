package frog;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.ECField;
import java.util.Scanner;
import java.security.*;

public class schnorrMain {
    static String pathSign = "Sign.txt";
    static String pathPublicKey = "PublicKey.txt";
    static String pathPrivateKey = "PrivateKey.txt";
    static String pathFile="C:\\Users\\Daniel\\Desktop\\FROG1\\peeper5sec.wav";
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {

        Scanner s = new Scanner(System.in);
        System.out.println("1 <bits in q> - generate keys");
        System.out.println("2 <file path> - make sign");
        System.out.println("3 <file path> - check sign");

        int type;
        while (true) {
            type = s.nextInt();
            if (type == 1) {
                int blq = s.nextInt();
                signature.generate(blq, pathPublicKey, pathPrivateKey);
            }
            if (type == 2) {
                signature.makeSign(pathFile, pathPublicKey, pathPrivateKey, pathSign);
            }
            if (type == 3) {
                signature.checkSign(pathFile, pathPublicKey, pathSign);
            }
            if (type == 4){
                break;
            }
        }
    }
}