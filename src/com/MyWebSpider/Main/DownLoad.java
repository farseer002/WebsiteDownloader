package com.MyWebSpider.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.css.sac.InputSource;
//import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;



import com.steadystate.css.dom.CSSImportRuleImpl;

import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.parser.CSSOMParser;


public class DownLoad  {
//	private Document doc;
	private String url;
	private int depth;
	private LinkQueue lque;
	private Map<String, Integer> mp;
	private String WebDirName;
	private int threadNum;
	private boolean finishFlag;
	private static final Logger Log = Logger.getLogger(DownLoad.class.getName());
	/**
	 * 测试用
	 */
	public void test() {
		// url = "http://dict.hjenglish.com/";
		// url = "http://bulo.hujiang.com/";
		url = "http://127.0.0.1:8080/demoForJava/index.html";
		WebDirName = new String("Page/");
		depth = 3;
	}
	/**
	 * 构造函数,不启动下载
	 * @param urlPath url路径
	 */
	DownLoad(String urlPath){
		url = urlPath;
		
	}
	/***
	 * 核心下载函数的初始化工作，
	 * @param urlPath url路径
	 * @param MyDepth 深度
	 * @param threadNum 第几号线程,现已不使用
	 * @throws Exception 
	 */
	DownLoad(String urlPath, int MyDepth,int threadNum) throws Exception {
		this.threadNum= threadNum; 
		url = urlPath;
		depth = MyDepth;
		lque = new LinkQueue();
		mp = new HashMap<String, Integer>();
		WebDirName = stdString(url.substring(0,url.lastIndexOf("/")+1))+"/";
		File file = new File(WebDirName);
		file.mkdirs();
		file = new File(WebDirName+"Images_Download/");
		file.mkdirs();
		file = new File(WebDirName+"Stylesheets_Download/");
		file.mkdirs();
		file = new File(WebDirName+"Scripts_Download/");
		file.mkdirs();
//		test();// for test

		System.out.println("Fetching url:" + url);
		
		finishFlag = false;
		Start();
		
		finishFlag = true;
	}
	/** 
	 * 创建绝对路径(包含多级) 
	 *  
	 * @param header 
	 *            绝对路径的前半部分(已存在) 
	 * @param tail 
	 *            绝对路径的后半部分(第一个和最后一个字符不能是/，格式：123/258/456) 
	 * @return 新创建的绝对路径 
	 */  
	public String makeDir(String header, String tail) {  
		    String[] sub = tail.split("/");  
		    File dir = new File(header);  
		    for (int i = 0; i < sub.length; i++) {  
		        if (!dir.exists()) {  
		            dir.mkdir();  
		        }  
		        File dir2 = new File(dir + File.separator + sub[i]);  
		        if (!dir2.exists()) {  
		            dir2.mkdir();  
		        }  
		        dir = dir2;  
		    }  
		    
		    return dir.toString();  
	}  
	/***
	 * 下载有相对路径的图片,保留相对路径
	 * @param imagesLocalPath 图像本地存储路径
	 * @param imagesUrlPath 图像Url路径
	 */
	public void DownLoadPicByOriginPath(String imagesLocalPath,String imagesUrlPath ){
		if(imagesLocalPath.contains("/") && !imagesLocalPath.substring(0,4).equals("http")){
			String imagesFilePathOrigin = imagesLocalPath.substring(0,imagesLocalPath.lastIndexOf("/"));
			makeDir(WebDirName, imagesFilePathOrigin);
			System.out.println("imagesFilePathOrigin:"+imagesLocalPath);
			System.out.println("imagesPath:"+imagesUrlPath);
			try {
				getImages(imagesUrlPath, WebDirName+imagesLocalPath);
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}
		
	}
	/***
	 * 下载图片的模板函数
	 * @param urlPath url路径
	 * @param strAttr 修改后的属性名
	 * @param links Elements对象
	 */
	public void DownLoadPicModel(String urlPath,String strAttr,Elements links){
		for (Element link : links) {

			String imagesPath = new String(link.attr(strAttr));
			String imagesPathTemp =  new String(link.attr(strAttr));
			System.out.println("srcLinks:" + urlPath);
			
			
			
			imagesPath = ValidateUrl(imagesPath, link);
			String imagesPathFileName = stdString(imagesPath);
			System.out.println("imagesPathFileName:"
					+ imagesPathFileName);
			link.attr("src", imagesPathFileName);
			System.out.println("WebDirName+imagesPathFileName:"+WebDirName
						+ imagesPathFileName);
			try {
				getImages(imagesPath,WebDirName+imagesPathFileName);
				getImages(imagesPath,WebDirName+"Images_Download/" + imagesPathFileName);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			
			//若存在目录结构,则按目录再保存一遍
			DownLoadPicByOriginPath(imagesPathTemp,imagesPath);
		
		}
	}
	/***
	 * <img src="http://xxx.jpg" /> <a href="xxx.jpg"> <frame src= "xxx.jpg" <iframe  src 图片格式为.png .jpeg .jpg  .gif
	 *  如果Url是相对路径,下载两份,一份是当前的,一份保留其文件格式,可能用于其javascript中的调用
	 * 
	 * @param urlPath url路径
	 * @throws Exception
	 */
	public void DownLoadPic(String urlPath, Document urlDoc)
			throws Exception {

		//.png .jpeg .jpg  .gif
		Elements imgSrcLinks = urlDoc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");//?i忽略字母大小写
		System.out.println("download pic in <img src");
		DownLoadPicModel(urlPath, "src", imgSrcLinks);
		
		Elements aHrefLinks = urlDoc.select("a[href~=(?i)\\.(png|jpe?g|gif)]");
		System.out.println("download pic in <a href");
		DownLoadPicModel(urlPath, "href", aHrefLinks);
	
		
		Elements frameSrcLinks = urlDoc.select("frame[src~=(?i)\\.(png|jpe?g|gif)]");
		System.out.println("download pic in <frame src");
		DownLoadPicModel(urlPath,"src", frameSrcLinks);
		
		
		Elements iframeSrcLinks = urlDoc.select("iframe[src~=(?i)\\.(png|jpe?g|gif)]");
		System.out.println("download pic in iframe");
		DownLoadPicModel(urlPath, "src", iframeSrcLinks);
		
	}

	/**
	 * get 图片
	 * 
	 * @param urlPath url路径
	 * @param fileName 需要加上WebDirName才是完整的路径
	 * @throws Exception
	 */

	public void getImages(String urlPath, String fileName) throws Exception {
		URL url = new URL(urlPath);// ：获取的路径
		// :http协议连接对象
		HttpURLConnection conn = (HttpURLConnection) url
				.openConnection();
		conn.setRequestMethod("GET");
		conn.setReadTimeout(10 * 1000);
//		if (conn.getResponseCode() < 10000) {
			InputStream inputStream = conn.getInputStream();
			byte[] data = readStream(inputStream);

			try {
				FileOutputStream outputStream = new FileOutputStream(
						fileName);
				outputStream.write(data);
				outputStream.close();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

//		}

	}

	/**
	 * 读图片流
	 * 
	 * @param inputStream
	 * @return outputStream.toByteArray()
	 * @throws Exception
	 */
	public byte[] readStream(InputStream inputStream) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		outputStream.close();
		inputStream.close();
		return outputStream.toByteArray();
	}

	/**
	 * 将HTML写入文件
	 * 
	 * @param fileName 文件名
	 * @param fileDoc Document对象
	 * @throws Exception
	 */
	public void writeHTMLToFile(String fileName, Document fileDoc)
			throws Exception {
		
		//UTF-8防止乱码
		OutputStreamWriter write = new OutputStreamWriter(
				new FileOutputStream(WebDirName + fileName),
				"UTF-8");
		BufferedWriter writer = new BufferedWriter(write);

		writer.write(fileDoc.toString());
		writer.close();
		
	}

	/**
	 * 将CSS写入文件,同时将其包含的CSS也写入(改成队列加入)
	 * 
	 * @param urlPath url路径
	 * @param fileName 保存的文件名
	 * @throws Exception
	 */
	public void writeCSSToFile(String urlPath, String fileName, Element ele)
			throws Exception {
		System.out.println("cur CSS:" + urlPath);
		// 将css中包含的图片,css文件也下载下来
		String URIPath = urlPath;
		CSSOMParser cssparser = new CSSOMParser();
		CSSStyleSheet css = null;
		try {
			css = cssparser.parseStyleSheet(
					new InputSource(URIPath), null, null);

		} catch (IOException e) {
			e.printStackTrace();
		}
		if (css != null) {
			CSSRuleList cssrules = css.getCssRules();
			for (int i = 0; i < cssrules.getLength(); i++) {
				CSSRule rule = cssrules.item(i);
				if (rule instanceof CSSImportRuleImpl) {// import
									// from
									// com.steadystate.css.dom.CSSImportRuleImpl
					System.out.println("CSSImportRuleImpl:");
					CSSImportRuleImpl cssrule = (CSSImportRuleImpl) rule;
					System.out.println(cssrule.getHref());

					// css中import是同级目录,不能只用baseUri(),暂时单独弄
					String urlPathInclude = urlPath.substring(0,urlPath.lastIndexOf("/") + 1)	+ cssrule.getHref();
					String urlPathFileName = stdString(urlPathInclude);
					writeCSSToFile(urlPathInclude,
							urlPathFileName, ele);
					try {
						cssrule.setHref(urlPathFileName);

					} catch (Exception e) {
						// TODO Auto-generated catch
						// block
						e.printStackTrace();
						continue;
					}
				} else if (rule instanceof CSSStyleRule) {// import
										// from
										// org.w3c.dom.css.CSSStyleRule
					CSSStyleRule cssrule = (CSSStyleRuleImpl) rule;
					// AttributeConditionImpl acImpl = new
					// AttributeConditionImpl("url", value,
					// specified)
//					System.out.println("cssrule.getCssText:"+ cssrule.getCssText());
//					System.out.println("cssrule.getSelectorText:"+ cssrule.getSelectorText());
					CSSStyleDeclaration styles = cssrule
							.getStyle();
					for (int j = 0, n = styles.getLength(); j < n; j++) {
						if(styles.item(j).equals("background-image")){
							String picUrlProperity = styles.getPropertyValue(styles.item(j));
							String picUrlPath = picUrlProperity.substring(picUrlProperity.indexOf("url(")+4,picUrlProperity.indexOf(")"));
							picUrlPath = urlPath.substring(0,urlPath.lastIndexOf("/") + 1)+picUrlPath;//没有使用ValidatePath,因为是同级目录
							System.out.println("picUrlPath:"+picUrlPath);
							String picUrlFileName = stdString(picUrlPath);
							
							try {
								getImages(picUrlPath, WebDirName
										+ picUrlFileName);
							} catch (Exception e) {
								e.printStackTrace();
								continue;
							}
							String picUrlProperityChange = picUrlProperity.substring(0,(picUrlProperity.indexOf("url(")+4))
									+picUrlFileName
									+picUrlProperity.substring(picUrlProperity.indexOf(")"));
							System.out.println("picUrlProperityChange:"+picUrlProperityChange);
							styles.setProperty(styles.item(j), picUrlProperityChange, "");
						}
						
					}

				}
			}
		}
		// ElementSelectorImpl esImpl = new ElementSelectorImpl("url");
		// System.out.println("esImpl:"+esImpl.getLocalName());

		// System.out.println("cssToString:" + css.toString());

		// 写入文件
	

		OutputStreamWriter write = new OutputStreamWriter(
				new FileOutputStream(WebDirName + fileName),
				"UTF-8");
		BufferedWriter writer = new BufferedWriter(write);

		writer.write(css.toString());
		writer.close();
		
		//再存储一份到/Stylesheets_Download/
		write = new OutputStreamWriter(
				new FileOutputStream(WebDirName + "Stylesheets_Download/" + fileName),
				"UTF-8");
		writer = new BufferedWriter(write);

		writer.write(css.toString());
		writer.close();

	}

	/**
	 * 将JS写入文件
	 * 
	 * @param urlPath url路径
	 * @param fileName 保存的文件名
	 * @throws Exception
	 */
	public void writeJSToFile(String urlPath, String fileName)
			throws Exception {

		// 建立连接并得到JS文本内容
		URL url = new URL(urlPath);
		HttpURLConnection httpConn = (HttpURLConnection) url
				.openConnection();
		InputStreamReader input = new InputStreamReader(
				httpConn.getInputStream(), "utf-8");
		BufferedReader bufReader = new BufferedReader(input);
		String line = "";
		StringBuilder contentBuf = new StringBuilder();
		while ((line = bufReader.readLine()) != null) {
			contentBuf.append(line);
		}
		String buf = contentBuf.toString();

		// 写入文件
		
		OutputStreamWriter write = new OutputStreamWriter(
				new FileOutputStream(WebDirName + fileName),
				"UTF-8");
		BufferedWriter writer = new BufferedWriter(write);

		writer.write(buf);
		writer.close();
		
		//再存储一份到/Scripts_Download/
		write = new OutputStreamWriter(
				new FileOutputStream(WebDirName + "Scripts_Download/"+fileName),
				"UTF-8");
		writer = new BufferedWriter(write);

		writer.write(buf);
		writer.close();
		
	
	}

	/***
	 * 生成可访问的Url
	 * 
	 * @param urlPath url路径
	 * @param ele Jsoup中单个html元素的对象
	 * @return urlPath 注意: 不能处理还要被加工的Url e.x js中将url再次转换
	 */
	public String ValidateUrl(String urlPath, Element ele) {
		if (urlPath.length() <= 4){
			if(ele.baseUri().endsWith("/") && urlPath.startsWith("/"))
				urlPath = ele.baseUri().substring(0,ele.baseUri().length()-1) + urlPath;
			else
				urlPath = ele.baseUri() + urlPath;
		}
		else if (!urlPath.startsWith("http")) {// e.x
									// http://xxx/xx.html
									// ->
			// http://xxx/folder
			String urlPathTemp = ele.baseUri().substring(0,
					ele.baseUri().lastIndexOf("/") + 1);
			if(urlPathTemp.endsWith("/") && urlPath.startsWith("/"))
				urlPath = urlPathTemp.substring(0,urlPathTemp.length()-1) + urlPath;
			else
				urlPath = urlPathTemp + urlPath;
		}
//		if(urlPath.length() >= 100){
//			urlPath = urlPath.substring(urlPath.length()-100,urlPath.length());
//		}
		System.out.println("ValidateUrl:" + urlPath);
		return urlPath;

	}
	/**
	 * 得到第一个下载的网页的绝对路径,是用于浏览器启动的页面
	 * @return courseFile
	 */
	public String getFirstFilePath(){
		
		String docFileName = stdString(url);
		if(!docFileName.endsWith(".html")&&!docFileName.endsWith(".php"))
			docFileName = docFileName + ".html";
		WebDirName = stdString(url.substring(0,url.lastIndexOf("/")+1))+"/";
		docFileName = WebDirName + docFileName;
		File directory = new File("");//参数为空 
		String courseFile;
		try {
			courseFile = directory.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} 
//		System.out.println(courseFile); 

		courseFile = new String(courseFile +"/" +  docFileName);
		courseFile.replace('/', '\\');
		System.out.println(courseFile);
		return courseFile;
	}
	/**
	 * Download的正式执行内容,完成下载CSS,JS,jpg等图片,网页的功能
	 * 
	 * @throws Exception
	 */
	private void Start() throws Exception {
		lque.addUnvisitedUrl(url);
		mp.put(url, new Integer(0));
		while (!lque.unVisitedUrlsEmpty()) {
			System.out.println("unVisitedUrls size:"
					+ lque.getUnVisitedUrlNum());

			// 出队列
			String curUrl = lque.unVisitedUrlDeQueue();
			int curLevel = mp.get(curUrl);

			Log.info("--------curUrl:" + curUrl + "  level:"
					+ curLevel+"--------");

			if (mp.containsKey(curUrl)) {
				if (mp.get(curUrl) >= depth) {
					continue;
				}
			} else {
				System.out.println("error! fail to find current URL:"
						+ curUrl);
			}

			// 得到当前URL对应网页的内容
			Document curDoc;
			try {
				
				curDoc = Jsoup.connect(curUrl).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) " +
						"AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31").timeout(3000).get();
			} catch (Exception e1) {

				e1.printStackTrace();
				continue;
			}
			
			
			Elements links = curDoc.select("a[href]");
			Elements frameSrcLinks = curDoc.select("frame[src]");//frame src
			Elements iframeSrcLinks = curDoc.select("iframe[src]");//iframe src
			Elements srcJss = curDoc.select("[src$=.js]");
			Elements srcCsses = curDoc.select("[href$=.css]");
			
			// 1.<link href="xxx.css"
			System.out.println("down css");
			for (Element srcCss : srcCsses) {

				String urlNext = srcCss.attr("href");
				System.out.println("urlNext: " + urlNext);

				try {

					urlNext = ValidateUrl(urlNext, srcCss);

					// 成功连接到该页面后,取js的最后
					String fileName = urlNext;

					fileName = stdString(fileName);
					System.out.println("Css fileName:"
							+ fileName);

					// ??将当前网页的html结点的属性名字改成存储后的
					srcCss.attr("href", fileName);
					

					writeCSSToFile(urlNext, fileName,
							srcCss);

				} catch (Exception e) {// 有些网页内容大,会出现连接超时的现象
					e.printStackTrace();
					continue;
				}

			}

			// 2.<script src="xxx.js"
			System.out.println("down js");
			for (Element srcJs : srcJss) {

				String urlNext = srcJs.attr("src");
				System.out.println("urlNext: " + urlNext);

				try {

					urlNext = ValidateUrl(urlNext, srcJs);

					// 成功连接到该页面后,取js的最后
					String fileName = urlNext;

					fileName = stdString(fileName);
					System.out.println("js fileName:"
							+ fileName);

					// ??将当前网页的html结点的属性名字改成存储后的
					srcJs.attr("src", fileName);
					

					writeJSToFile(urlNext, fileName);

				} catch (Exception e) {// 有些网页内容大,会出现连接超时的现象
					e.printStackTrace();
					continue;
				}

			}

			// 3.<a href=
			System.out.println("down <a href(html)");
			for (Element link : links) {
				
				String urlNext = link.attr("href");

				System.out.println("urlNext:" + urlNext);
				// System.out.println("baseUrl:" +
				// link.baseUri());

				// !!!urlNext 可能与实际网页名称不同
				// 只访问不同页面的网页,且未访问过, 将其入队
				if (!urlNext.equals("#")) {
					try {
						urlNext = ValidateUrl(urlNext, link);
						if(urlNext.endsWith(".jpg") || urlNext.endsWith(".jpeg") || urlNext.endsWith(".png") || urlNext.endsWith(".gif") || urlNext.endsWith(".swf")
								||urlNext.endsWith(".css")||urlNext.endsWith(".js")){
							System.out.println("do not enqueue this");
							continue;
						}
							
						System.out.println("urlNext2:"+urlNext);
						
						
						String fileName = stdString(urlNext);
						if(!urlNext.endsWith(".html"))
							fileName = fileName + ".html";
						System.out.println("fileName:" + fileName);
						// 将当前网页的html结点的href名字改成存储后的
						link.attr("href", fileName);
						// 并记录level
						if (lque.addUnvisitedUrl(urlNext))
							mp.put(urlNext,
									new Integer(
											curLevel + 1));
						else
							continue;

					} catch (Exception e) {// 有些网页内容大,会出现连接超时的现象
						e.printStackTrace();
						continue;
					}

				}
			}
			
			
			//4.<frame src=
			System.out.println("down frame <src(html)");
			for (Element link : frameSrcLinks) {
				
				String urlNext = link.attr("src");

				System.out.println("urlNext:" + urlNext);
				// System.out.println("baseUrl:" +
				// link.baseUri());

				// !!!urlNext 可能与实际网页名称不同
				// 只访问不同页面的网页,且未访问过, 将其入队
				if (!urlNext.equals("#")) {
					try {
						urlNext = ValidateUrl(urlNext, link);
						if(urlNext.endsWith(".jpg") || urlNext.endsWith(".jpeg") || urlNext.endsWith(".png") || urlNext.endsWith(".gif") || urlNext.endsWith(".swf")
								||urlNext.endsWith(".css")||urlNext.endsWith(".js")){
							System.out.println("do not enqueue this");
							continue;
						}
							
						System.out.println("urlNext2:"+urlNext);
						
//									Document docNext = Jsoup
//											.connect(urlNext)
//											.timeout(2000)
//											.get();
//
//									// 若成功连接到该页面
//									String fileName = curDoc.title();
//									String fileName = stdString(fileName)+".html";
						
						String fileName = stdString(urlNext);
						if(!urlNext.endsWith(".html"))
							fileName = fileName + ".html";
						System.out.println("fileName:" + fileName);
						// 将当前网页的html结点的href名字改成存储后的
						link.attr("src", fileName);
						// 并记录level
						if (lque.addUnvisitedUrl(urlNext))
							mp.put(urlNext,
									new Integer(
											curLevel + 1));
						else
							continue;

					} catch (Exception e) {// 有些网页内容大,会出现连接超时的现象
						e.printStackTrace();
						continue;
					}

				}
			}
			
			//5.<iframe src=
			System.out.println("down iframe <src(html)");
			for (Element link : iframeSrcLinks) {
				
				String urlNext = link.attr("src");

				System.out.println("urlNext:" + urlNext);
				// System.out.println("baseUrl:" +
				// link.baseUri());

				// !!!urlNext 可能与实际网页名称不同
				// 只访问不同页面的网页,且未访问过, 将其入队
				if (!urlNext.equals("#")) {
					try {
						urlNext = ValidateUrl(urlNext, link);
						if(urlNext.endsWith(".jpg") || urlNext.endsWith(".jpeg") || urlNext.endsWith(".png") || urlNext.endsWith(".gif") || urlNext.endsWith(".swf")
								||urlNext.endsWith(".css")||urlNext.endsWith(".js")){
							System.out.println("do not enqueue this");
							continue;
						}
							
						System.out.println("urlNext2:"+urlNext);
						

						
						String fileName = stdString(urlNext);
						if(!urlNext.endsWith(".html"))
							fileName = fileName + ".html";
						System.out.println("fileName:" + fileName);
						// 将当前网页的html结点的href名字改成存储后的
						link.attr("src", fileName);
						// 并记录level
						if (lque.addUnvisitedUrl(urlNext))
							mp.put(urlNext,
									new Integer(
											curLevel + 1));
						else
							continue;

					} catch (Exception e) {// 有些网页内容大,会出现连接超时的现象
						e.printStackTrace();
						continue;
					}

				}
			}
			System.out.println("down pic");
			DownLoadPic(curUrl, curDoc);// 下载图片			
			
			
//			String docFileName = stdString(curDoc.title())+".html"; 
			
			String docFileName = stdString(curUrl);
			if(!docFileName.endsWith(".html")&&!docFileName.endsWith(".php"))
				docFileName = docFileName + ".html";
			System.out.println("write current html File:"+docFileName);
			
			
			writeHTMLToFile(docFileName,curDoc);
			
			
			System.out.println("---------------------------------");
		}
		
		
	}
	/**
	 * 规范化字符串,将保存的文件名中不允许的字符替换掉,且保证长度不超过windows允许总路径的255字符
	 * @param s 文件名 
	 * @return s 规范化文件名 
	 */
	public String stdString(String s) {
		s = s.replaceAll("[/\\\"|\\\\:? ]", "_");
		if(s.length() > 100){
			s = s.substring(s.length()-100);
		}
		return s;
	}
	/**
	 * 是否下载好
	 */
	public boolean isFinish(){
		return finishFlag;
	}
}
