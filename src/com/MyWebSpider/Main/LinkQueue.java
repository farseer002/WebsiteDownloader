package com.MyWebSpider.Main;

import java.util.HashSet;
import java.util.Set;

public class LinkQueue {
	// 已访问的 url 集合
	private  Set visitedUrl = new HashSet();
	// 待访问的 url 集合
	private  MyQueue unVisitedUrl = new MyQueue();


	public  MyQueue getUnVisitedUrl() {
		return unVisitedUrl;
	}

	public  void addVisitedUrl(String url) {
		visitedUrl.add(url);
	}


	public  void removeVisitedUrl(String url) {
		visitedUrl.remove(url);
	}


	public  String unVisitedUrlDeQueue() {
		return (String) unVisitedUrl.deQueue();
	}
	// 保证每个 URL 只被访问一次
	public  boolean addUnvisitedUrl(String url) {
		if (url != null && !url.trim().equals("")
				&& !visitedUrl.contains(url)
				&& !unVisitedUrl.contains(url)){
			unVisitedUrl.enQueue(url);
			return true;
		}
		return false;
	}

	public  int getVisitedUrlNum() {
		return visitedUrl.size();
	}
	public  int getUnVisitedUrlNum() {
		return unVisitedUrl.size();
	}
	public  boolean unVisitedUrlsEmpty() {
		return unVisitedUrl.empty();
	}
}
