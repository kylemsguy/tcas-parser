package com.kylemsguy.tcasparser;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class QuestionManager {
	private final String ASK_URL = SessionManager.BASE_URL + "apiw/qa/ask/";
	private final String QUESTION_URL = SessionManager.BASE_URL
			+ "apiw/qa/notifications/";

	private SessionManager session;
	private Map<Integer, Question> questionAns;

	public QuestionManager(SessionManager session) {
		this.session = session;
	}

	public void askQuestion(String question) throws Exception {
		String postQuestion = "text=" + URLEncoder.encode(question, "UTF-8");
		session.sendPost(ASK_URL, postQuestion);
	}

	public Map<Integer, Question> getQuestions() throws Exception {
		String rawData = session.getPageContent(QUESTION_URL);
		System.out.println(rawData);
		questionAns = QAObject.parseData(rawData);
		
		return questionAns;
	}

}
