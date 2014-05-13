package com.kylemsguy.tcasparser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SessionManager {
	// let's define some constants
	private final String USER_AGENT = "Mozilla/5.0";
	private final String LOGIN = "http://www.twocansandstring.com/login/";

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
		connection
				.setRequestProperty("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
		connection.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Content-Length",
				Integer.toString(postParams.length()));
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		// COOKIES
		for (String cookie : this.cookies) {
			connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
		}
		connection.setRequestProperty("Host", "twocansandstring.com");

		connection.setRequestProperty("User-Agent", USER_AGENT);

		connection.setRequestProperty("Referrer",
				"http://twocansandstring.com/login/");

		connection.setDoOutput(true);
		connection.setDoInput(true);

		// Send post request
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(postParams);
		wr.flush();
		wr.close();

		int responseCode = connection.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println(response.toString());
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

	public String getFormParams(String html, String username, String password)
			throws UnsupportedEncodingException {

		System.out.println("Extracting form's data...");

		Document doc = Jsoup.parse(html);

		// form id
		Element loginform = doc.getElementsByTag("form").get(0);
		Elements inputElements = loginform.getElementsByTag("input");
		List<String> paramList = new ArrayList<String>();
		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");

			if (key.equals("login_username"))
				value = username;
			else if (key.equals("login_password"))
				value = password;
			paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
		}

		// build parameters list
		StringBuilder result = new StringBuilder();
		for (String param : paramList) {
			if (result.length() == 0) {
				result.append(param);
			} else {
				result.append("&" + param);
			}
		}
		return result.toString();
	}

	public List<String> getCookies() {
		return cookies;
	}

	public void setCookies(List<String> cookies) {
		this.cookies = cookies;
	}

}
