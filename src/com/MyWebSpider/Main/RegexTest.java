package com.MyWebSpider.Main;

public class RegexTest {
	public static void main(String args[]){
		String ma = "D:\\firefox\\firefox.exe";
		String pattern = "^([C-Hc-h])[:\\\\](.*)(\\.exe)$";
		System.out.println(ma.trim().matches(pattern));
		
	}
}
