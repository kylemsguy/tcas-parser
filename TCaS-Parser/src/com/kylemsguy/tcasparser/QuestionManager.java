package com.kylemsguy.tcasparser;

import java.net.URLEncoder;

public class QuestionManager {
	private final String ASK_URL = SessionManager.BASE_URL + "/apiw/qa/ask/";

	private SessionManager session;

	public QuestionManager(SessionManager session) {
		this.session = session;
	}

	public void askQuestion(String question) throws Exception{
		String postQuestion = "text=" + URLEncoder.encode(question, "UTF-8");
		session.sendPost(ASK_URL, postQuestion);
	}

	public void getQuestions() {
		// TODO implement getting questions
	}

}
