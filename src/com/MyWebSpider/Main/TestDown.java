package com.MyWebSpider.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class TestDown {

	public static void main(String args[]) throws IOException{
		String urlpath = "http://127.0.0.1:8080/demoForJava/index.html";
		URL name = null;
		try {
			name = new URL(urlpath.trim());
			InputStream input;
			input = name.openStream();
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader buffer = new BufferedReader(isr);
			File	file = new File("testDown.html");
			
			BufferedWriter writer = null;
			
			
			String inputLine;
			String all = null;
			while((inputLine = buffer.readLine() )!= null){
//				System.out.println(inputLine);
				all += inputLine;
			}
			buffer.close();
			
			
			try {
				
				writer = new BufferedWriter(new FileWriter(file,true));
				writer.write(all);
				if(writer != null){
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
