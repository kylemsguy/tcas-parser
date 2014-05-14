package com.kylemsguy.tcasparser;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnswerManager {
	private final String QUESTION_URL = "http://twocansandstring.com/apiw/qa/getquestion";
	private final String ANSWER_URL = "http://twocansandstring.com/apiw/qa/answer/";
	private final String SKIP_URL = "http://twocansandstring.com/apiw/qa/skip/";

	private SessionManager session;

	public AnswerManager(SessionManager session) {
		this.session = session;
	}

	/**
	 * Gets a question from TwoCansandString
	 * 
	 * @return A map containing the ID and question
	 * @throws Exception
	 */
	public Map<String, String> getQuestion() throws Exception {
		String pageContent = session.getPageContent(QUESTION_URL);
		Map<String, String> question = null;

		question = extractQuestionData(pageContent);
		return question;
	}

	private Map<String, String> extractQuestionData(String rawData)
			throws Exception {
		// check if logged in
		if (rawData.matches(".*?<div id=\"login_nav\">"
				+ "<a href=\"/login/\">Login</a>"
				+ ".*?That page could not be found.*?")) {
			throw new Exception("Not logged in.");
		}
		Map<String, String> question = new TreeMap<String, String>();
		// extract ID
		Pattern idPattern = Pattern.compile("(.*?)sid\\^i(.*?)\\^(.*?)");
		Matcher idMatcher = idPattern.matcher(rawData);
		if (idMatcher.find()) {
			question.put("id", idMatcher.group(2));
		} else {
			throw new Exception("Not logged in.");
		}

		// extract content
		Pattern contentPattern = Pattern.compile("(.*?)stext\\^s(.*?)\\^(.*?)");
		Matcher contentMatcher = contentPattern.matcher(rawData);
		if (contentMatcher.find()) {
			String content = contentMatcher.group(2);

			String newContent = content.replaceAll("\\$n", "\n");

			question.put("content", newContent);
		} else {
			throw new Exception("Not logged in.");
		}
		return question;
	}

	/**
	 * Takes in the ID of the question and the answer, and sends the request to
	 * TwoCansandString
	 * 
	 * @param id
	 * @param rawAnswer
	 * @return A map containing data for the next question.
	 * @throws Exception
	 */
	public Map<String, String> sendAnswer(String id, String rawAnswer)
			throws Exception {
		// String answer = rawAnswer.replaceAll("\n", "$s");
		String answer = rawAnswer;
		String postParams = null;

		postParams = "text" + "=" + URLEncoder.encode(answer, "UTF-8");

		String nextQuestion = session.sendPost(ANSWER_URL + id + "/",
				postParams.toString());

		Map<String, String> nextQuestionMap = extractQuestionData(nextQuestion);

		return nextQuestionMap;
	}

	/**
	 * Skips the question
	 * 
	 * @param forever
	 *            whether to skip forever
	 * @throws Exception
	 */
	public Map<String, String> skipQuestion(String id, boolean forever)
			throws Exception {
		String skip = SKIP_URL + id + "/";
		if (forever) {
			skip += "forever/";
		}

		String pageContent = session.getPageContent(skip);
		Map<String, String> question = null;

		// check if logged in
		if (pageContent.matches(".*?<div id=\"login_nav\">"
				+ "<a href=\"/login/\">Login</a>"
				+ ".*?That page could not be found.*?")) {
			throw new Exception("Not logged in.");
		} else {
			question = extractQuestionData(pageContent);
		}
		return question;
	}
}
