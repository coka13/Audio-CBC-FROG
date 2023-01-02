package frog;

import java.util.*;

public class OTP
{
 static String genOTP(int len)
 {
     System.out.print("Your OTP is : ");

     // Using numeric values
     String numbers = "0123456789";

     // Using random method
     Random rndm_method = new Random();

     char[] otp = new char[len];

     for (int i = 0; i < len; i++)
     {
         // Use of charAt() method : to get character value
         // Use of nextInt() as it is scanning the value as int
         otp[i] =
          numbers.charAt(rndm_method.nextInt(numbers.length()));
     }
     return  String.valueOf(otp);
 }
    /* Check OTP */
 static void checkOTP(String otp) {
	 Scanner sc=new Scanner(System.in);
	 while (true) {
			System.out.println(("Enter your OTP: "));
			String input_password = sc.next();
			if (input_password.equals(otp)) {
				break;
			} else {
				System.out.println("Wrong password! Try again!");
			}
		}
 }

}