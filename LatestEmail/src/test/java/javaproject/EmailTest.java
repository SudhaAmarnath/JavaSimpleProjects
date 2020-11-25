package javaproject;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import JavaMailAPI.Email;
import JavaMailAPI.GetEmail;


public class EmailTest {

	@Test
	public void getLatestEmailFromInbox() {
		

		String host = "pop.gmail.com";
		int port = 995;
		// Turn on "Less secure app access in gmail privacy settings"
		// Set user name and password below.
		String user = "";
		String password = "";

		if (user != "" && password != "") {

			Email email = new Email();
			email = GetEmail.getLatestEmailFromInbox(host, port, user, password);

			String from = email.getFrom();
			String to = email.getTo();
			String subject = email.getSubject();
			byte[] body = email.getBody();

			assertNotEquals(null, email);
			assertNotEquals("", from);
			assertNotEquals("", to);
			assertNotEquals("", subject);
			assertNotEquals("", body);
		}
		
		
	}
}
