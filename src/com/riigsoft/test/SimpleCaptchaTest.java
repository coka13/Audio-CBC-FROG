package com.riigsoft.test;

import com.riigsoft.captcha.SimpleCaptcha;

import cn.apiclub.captcha.Captcha;
import java.awt.Desktop;
import java.io.File;  

public class SimpleCaptchaTest {

	public static void main(String[] args) {
		Captcha c=SimpleCaptcha.createCaptcha(200, 80);
		SimpleCaptcha.createImage(c);
		System.out.println(c.getAnswer());
		System.out.println("Simple Captcha Created !!!");
		openFile();

	}
	public static  void openFile()   
	{  
	try  
	{  
	//constructor of file class having file as argument  
	File file = new File("captcha.png");   
	if(!Desktop.isDesktopSupported())//check if Desktop is supported by Platform or not  
	{  
	System.out.println("not supported");  
	return;  
	}  
	Desktop desktop = Desktop.getDesktop();  
	if(file.exists())         //checks file exists or not  
	desktop.open(file);              //opens the specified file  
	}  
	catch(Exception e)  
	{  
	e.printStackTrace();  
	}  
	}  
	 
}
