package com.kylemsguy.tcasparser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PageParser {
	private final Pattern notLoggedIn = Pattern.compile("^Login | Register");

	private WebDriver driver = null;
	private boolean onAnswerPage = false;

	public PageParser() {
		driver = new HtmlUnitDriver();
	}

	public boolean getLoggedIn() {
		WebElement loggedInElement = driver.findElement(By.tagName("div"));
		Matcher notLoggedInMatcher = notLoggedIn.matcher(loggedInElement
				.getText());

		if (notLoggedInMatcher.find()) {
			return false;
		} else {
			return true;
		}
	}

	public void login(String username, String password) {
		onAnswerPage = false;
		WebElement loggedInElement;
		WebElement usernameElement;
		WebElement passwordElement;
		WebElement loggestInElement;

		driver.get("http://www.twocansandstring.com/login/");

		if (!getLoggedIn()) {
			// log in
			usernameElement = driver.findElement(By.name("login_username"));
			passwordElement = driver.findElement(By.name("login_password"));
			loggestInElement = driver.findElement(By.name("submit"));

			// enter login info into form
			usernameElement.sendKeys(username);
			passwordElement.sendKeys(password);

			// submit form and login
			loggestInElement.click();
		} else {
			// already logged in

		}
	}

	public String getQuestion() throws Exception {
		WebElement questionElement = null;
		WebElement answerLink = driver.findElement(By.linkText("Answer"));
		onAnswerPage = true;
		answerLink.click(); // go to Answer page

		// find the div with id "question_area". Right below it is the question
		List<WebElement> divElements = driver.findElements(By.tagName("div"));

		for (WebElement element : divElements) {
			if (element.getAttribute("id") == null)
				continue;

			if (element.getAttribute("id").equals("question_area")) {
				questionElement = element;
			}
		}
		if (questionElement == null) {
			throw new Exception("No question area found");
		}
		System.out.println(questionElement.getText());
		return null;
	}
}
