package com.kylemsguy.tcasparser;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnswerManager {
	private final String QUESTION_URL = "http://twocansandstring.com/apiw/qa/getquestion";

	private SessionManager session;

	public AnswerManager(SessionManager session) {
		this.session = session;
	}

	public Map<String, String> getQuestion() throws Exception {
		Map<String, String> question = new TreeMap<String, String>();

		String pageContent = session.getPageContent(QUESTION_URL);

		// check if logged in
		if (pageContent.matches(".*?<div id=\"login_nav\">"
				+ "<a href=\"/login/\">Login</a>"
				+ ".*?That page could not be found.*?")) {
			throw new Exception("Not logged in.");
		} else {
			// extract ID
			Pattern idPattern = Pattern.compile("(.*?)sid\\^(.*?)\\^(.*?)");
			Matcher idMatcher = idPattern.matcher(pageContent);
			if (idMatcher.find()) {
				question.put("id", idMatcher.group(2));
			} else {
				throw new Exception("Not logged in.");
			}

			// extract content
			Pattern contentPattern = Pattern
					.compile("(.*?)stext\\^s(.*?)\\^(.*?)");
			Matcher contentMatcher = contentPattern.matcher(pageContent);
			if (contentMatcher.find()) {
				String content = contentMatcher.group(2);

				String newContent = content.replaceAll("\\$n", "\n");

				question.put("content", newContent);
			} else {
				throw new Exception("Not logged in.");
			}
		}
		return question;
	}

	public void sendAnswer(String id, String answer) {
		// TODO implement method
	}

	public void skipQuestion(boolean forever) {
		// TODO implement method
	}

}
