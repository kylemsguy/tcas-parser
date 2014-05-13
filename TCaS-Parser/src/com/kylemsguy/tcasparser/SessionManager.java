package com.kylemsguy.tcasparser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SessionManager {
	// let's define some constants
	private final String USER_AGENT = "Mozilla/5.0";
	private final String LOGIN = "http://www.twocansandstring.com/login/";
	private final String QUESTION_URL = "http://twocansandstring.com/apiw/qa/getquestion";

	private List<String> cookies;
	private HttpURLConnection connection;

	public SessionManager() {
		// TODO Auto-generated constructor stub
	}

	public void login(String username, String password) throws Exception {
		// make sure cookies are on
		CookieHandler.setDefault(new CookieManager());

		// GET form's data
		String page = getPageContent(LOGIN);
		String postParams = getFormParams(page, username, password);

		// Send data to login
		sendPost(LOGIN, postParams);

	}

	private void sendPost(String url, String postParams) throws Exception {
		// method borrows heavily from
		// http://www.mkyong.com/java/how-to-automate-login-a-website-java-example/
		// start the connection
		URL obj = new URL(url);
		connection = (HttpURLConnection) obj.openConnection();

		// now time to act like a browser
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Host", "twocansandstring.com");
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection
				.setRequestProperty("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
		connection.setRequestProperty("Referrer",
				"http://twocansandstring.com/login/");
		// COOKIES
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Content-Type", "text/html");
		connection.setRequestProperty("Content-Length",
				Integer.toString(postParams.length()));
	}

	private String getPageContent(String url) throws Exception {
		// method practically copied from
		// http://www.mkyong.com/java/how-to-automate-login-a-website-java-example/
		// start the connection
		URL obj = new URL(url);
		connection = (HttpURLConnection) obj.openConnection();

		// default is GET
		connection.setRequestMethod("GET");

		connection.setUseCaches(false);

		// act like a browser
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection
				.setRequestProperty("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (cookies != null) {
			for (String cookie : this.cookies) {
				connection
						.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
			}
		}
		int responseCode = connection.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Get the response cookies
		setCookies(connection.getHeaderFields().get("Set-Cookie"));

		return response.toString();
	}

	private String getFormParams(String page, String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getCookies() {
		return cookies;
	}

	public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	}

}
