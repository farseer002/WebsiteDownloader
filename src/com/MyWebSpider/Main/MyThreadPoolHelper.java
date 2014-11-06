package com.MyWebSpider.Main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyThreadPoolHelper {
	ExecutorService pool;
	MyWebSpider mws = null;
	String firstFileName;
	//          http://127.0.0.1:8080/jsoupJavaDoc/ 
	//          http://127.0.0.1:8080/demoForJava/index.html 
	//          http://127.0.0.1:8080/xss_test/rxss.php?x=1
	//          http://lab.nuaa.edu.cn/main/redirect.asp?zx=6&zxmc=力学实验教学中心
//	
	public MyThreadPoolHelper() {
		// TODO Auto-generated constructor stub
		pool = Executors.newCachedThreadPool();
		
	}
	public void  addDownLoad(String url,int depth) throws Exception{
		url = url.trim();
		firstFileName = new String((new DownLoad(url)).getFirstFilePath());
//		MyWebSpider mws1 = new MyWebSpider(url, depth); 
//		mws = mws1;
		mws = new MyWebSpider(url, depth);
		Thread t = new Thread(mws);
		pool.execute(t);
		
	}
	public String getDownLoadInfo(){
		return firstFileName;
	}
	public boolean isFinish(){
		if(mws != null)
			return mws.isFinish();
		else
			return true;
	}
	public void  destory(){
		pool.shutdownNow();
		mws = null;
	}
	public void cancel(){
		
		mws = null;
		pool.shutdownNow();
		
		
		
	}
}
