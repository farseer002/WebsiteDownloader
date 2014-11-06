package com.MyWebSpider.Main;



public class MyWebSpider implements Runnable {
	DownLoad dl;
	String url;
	int depth;
	boolean finishFlag = true;
	String firstFilePath = "";
	MyWebSpider(String url, int depth) throws Exception {
		this.url = url;
		url = url.trim();
		this.depth = depth;
		if (depth < 0 || depth > 5) {
			System.out.println("depth should be in the range of 1-"+depth+"!");
			return;
		}
		finishFlag = true;
		// = Integer.parseInt(args[1]);
		// String a = "1|2\\";
		// a = a.replaceAll("[|\\\\]", "_");
		// System.out.println(a);

	}


	public String  getFirstFilePath(){
		while(firstFilePath.equals("")){try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}}
//			return "fail to get File Path";
		
		return firstFilePath;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			finishFlag = false;
			dl = new DownLoad(url, depth, 0);
			firstFilePath = dl.getFirstFilePath();
			finishFlag = dl.isFinish();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public boolean isFinish(){
		return finishFlag;
	}
	
}
