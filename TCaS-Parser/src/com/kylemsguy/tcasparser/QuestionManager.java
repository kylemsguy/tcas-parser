package com.kylemsguy.tcasparser;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class QuestionManager {
	private final String ASK_URL = SessionManager.BASE_URL + "apiw/qa/ask/";
	private final String QUESTION_URL = SessionManager.BASE_URL
			+ "apiw/qa/notifications/";

	private SessionManager session;

	public QuestionManager(SessionManager session) {
		this.session = session;
	}

	public void askQuestion(String question) throws Exception {
		String postQuestion = "text=" + URLEncoder.encode(question, "UTF-8");
		session.sendPost(ASK_URL, postQuestion);
	}

	public String[] getQuestions() throws Exception {
		// TODO: parse and tokenize data
		String rawData = session.getPageContent(QUESTION_URL);
		String[] splitData = rawData.split("\\^");
		for(String a: splitData){
			System.out.println(a);
		}
		return splitData;
	}

}
