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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

	public void sendPost(String url, String postParams) throws Exception {
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
		// connection.setRequestProperty("Content-Length",
		// Integer.toString(postParams.length()));
		connection.setFixedLengthStreamingMode(postParams.getBytes().length);
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
		// connection.setInstanceFollowRedirects(true);
		connection.setDoOutput(true);
		connection.setDoInput(true);

		// Send post request
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(postParams);
		wr.flush();
		wr.close();

		boolean redirect = false;

		// normally, 3xx is redirect
		int status = connection.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK) {
			if (status == HttpURLConnection.HTTP_MOVED_TEMP
					|| status == HttpURLConnection.HTTP_MOVED_PERM
					|| status == HttpURLConnection.HTTP_SEE_OTHER)
				redirect = true;
		}

		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code ... " + status);

		if (redirect) {

			// get redirect url from "location" header field
			String newUrl = connection.getHeaderField("Location");

			// get the cookie if need, for login
			String cookies = connection.getHeaderField("Set-Cookie");

			// open the new connnection again
			connection = (HttpURLConnection) new URL(newUrl).openConnection();
			connection.setRequestProperty("Cookie", cookies);
			connection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			connection.addRequestProperty("User-Agent", "Mozilla");
			connection.addRequestProperty("Referer", "google.com");

			System.out.println("Redirect to URL : " + newUrl);

		}

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// debug; prints success iff logged in as kylemsguy
		if (response.toString().matches(".*?[Kk]ylemsguy.*?")) {
			System.out.println("Login Successful");
		} else {
			System.out.println("Login Failed");
		}
	}

	public String getPageContent(String url) throws Exception {
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
		this.setCookies(connection.getHeaderFields().get("Set-Cookie"));

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

	public HttpURLConnection getConnection() {
		return connection;
	}

}
