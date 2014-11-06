package com.MyWebSpider.XSSScan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ReflectedXSSScan {

	private String url;
	private String payloadFileName = "payload.txt";
	private String logFileName = "xssLog.txt";
	private Vector<String>payload;
	boolean finishFlag = true;
	
	/**
	 *读入payload文件 
	 */
	public void readFile() {
		File	file = new File(payloadFileName);
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		String tempString = null;
		try {
			while ((tempString = reader.readLine()) != null) {
				if(tempString.trim().equals(""))continue;
				payload.add(new String(tempString));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * 将网址写入log
	 * @param logInfo
	 */
	public void writeLog(String logInfo){
		
		File	file = new File(logFileName);
		
		BufferedWriter writer = null;
		try {
			
			writer = new BufferedWriter(new FileWriter(file,true));
			writer.write(logInfo);
			if(writer != null){
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 初始化log文件
	 */
	private void initFile(){
		File file = new File(logFileName);
		
		try {
			if(!file.exists())
				file.createNewFile();
			else{
				file.delete();
				file.createNewFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 将log文件内容转为String
	 * @return logStr
	 */
	public String LogToString(){
		File	file = new File(logFileName);
		String logStr = new String("");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String tempString = null;
		try {
			while ((tempString = reader.readLine()) != null) {
				if(tempString.trim().equals(""))continue;
				logStr += tempString;
				logStr += "\n";
			}
			reader.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("LogToString"+logStr);
		
		return logStr;
	}
	/**
	 * 构造函数
	 */
	public ReflectedXSSScan(){
//		this.url = url;  
		finishFlag = true;
		payload = new Vector<>();
		initFile();
		readFile();
	}
	/**
	 * 检测网址是否存在反射型XSS漏洞
	 * @param url
	 */
	public void checkUrl(String url){
		finishFlag = false;
		
		Document curDoc;
		
		String domain = url.substring(0,url.indexOf("?")+1);
		String para = url.substring(url.indexOf("?")+1,url.length());
		String sarray[] = para.split("&");
		
		System.out.println(payload.size());
		System.out.println(sarray.length);
		for(int i=0;i<payload.size();++i){
			for(int j=0;j<sarray.length;++j){
				String curUrl = domain;
				int k;
				for(k=0;k<j;++k){
					curUrl += sarray[k];
					curUrl += "&";
				}
				curUrl += sarray[k]+payload.get(i);
				++k;
				for(;k<sarray.length;++k){
					curUrl += "&";
					curUrl += sarray[k];
				}
				
				
				try {
					System.out.println("--------------------------------");
					System.out.println(curUrl);
					curDoc = Jsoup.connect(curUrl).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) " +
							"AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31").timeout(3000).get();
					String webContent = curDoc.toString();
					
//					System.out.println(webContent);
					System.out.println(payload.get(i));
					
					if(webContent.toLowerCase().contains("alert(1)") || webContent.toLowerCase().contains("src=@")||
							webContent.toLowerCase().contains("src=http://baidu.com>")
							|| webContent.toLowerCase().contains(payload.get(i))
							){
						System.out.println("find potential XSS point");
//						System.out.println(curUrl);
						writeLog(curUrl+"\n");
					}
					System.out.println("--------------------------------");
				} catch (IOException e) {
//					e.printStackTrace();
					System.out.println("fail to get the page");
				}
			}
		}	
		finishFlag = true;
	}
	/**
	 * 检测是否结束
	 * @return finishFlag
	 */
	public boolean isFinish(){
		return finishFlag;
	}
	//for test
	public static void main(String args[]){
//		String urlTest = "http://127.0.0.1:8080/xss_test/rxss.php?x=a&y=b";
		String urlTest = "http://lab.nuaa.edu.cn/main/redirect.asp?zx=6&zxmc=力学实验教学中心";
		ReflectedXSSScan rxs = new ReflectedXSSScan(); 
		rxs.checkUrl(urlTest);
	}
}
