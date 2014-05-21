package com.kylemsguy.tcasparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class QAObject {
	private int id;
	private String content;
	
	public QAObject(int id, String content) {
		this.id = id;
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public String getContent() {
		return content;
	}
	
	public static Map<Integer, Question> parseData(String data){
		Map<Integer, Question> questions = new TreeMap<Integer, Question>();
		
		// regex objects
		Pattern qPattern = Pattern.compile("lsQ\\^i(\\d+)\\^s(.*?)\\^\\^");
		Pattern aPattern = Pattern.compile("lsA\\^i(\\d+)\\^(\\d+)\\^s(.*?)\\^\\^");
		
		Matcher qMatcher = qPattern.matcher(data);
		Matcher aMatcher = aPattern.matcher(data);
		
		while(qMatcher.find()){
			// get data from regex
			int id = Integer.parseInt(qMatcher.group(1));
			Question q = new Question(id, qMatcher.group(2));
			
			// insert into map
			questions.put(id, q);
		}
		
		
		return questions;
	}
}
