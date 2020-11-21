package JavaMailAPI;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GetEmail {

	public static Email getLatestEmailFromInbox(String host, int port, String user, String password) {

		try {

			Email email = new Email();

			Properties properties = new Properties();
			properties.setProperty("mail.store.protocol", "imaps");
			properties.put("mail.imap.port", String.valueOf(port));

			Session emailSessionObj = Session.getDefaultInstance(properties);

			Store storeObj = emailSessionObj.getStore("imaps");
			storeObj.connect("imap.gmail.com", user, password);

			Folder emailFolderObj = storeObj.getFolder("INBOX");
			emailFolderObj.open(Folder.READ_ONLY);

			int totalMessages = emailFolderObj.getMessageCount();

			System.out.println("Total messages in INBOX: " + totalMessages);
			System.out.println("Fetching Latest email number from INBOX: " + totalMessages);
			Message[] messageObjs = emailFolderObj.getMessages(totalMessages, totalMessages);

			System.out.println("Subject: " + messageObjs[0].getSubject());
			System.out.println("Sender: " + messageObjs[0].getFrom()[0]);
			List<String> toAddresses = new ArrayList<String>();
			Address[] recipients = messageObjs[0].getRecipients(Message.RecipientType.TO);
			for (Address address : recipients) {
				toAddresses.add(address.toString());
			}
			System.out.println("Receiver: " + toAddresses);
			System.out.println("Content: " + getTextFromMessage(messageObjs[0]));
			System.out.println("Date: " + messageObjs[0].getReceivedDate());

			email.setFrom(messageObjs[0].getFrom()[0].toString());
			email.setTo(String.join(",", toAddresses));
			email.setSubject(messageObjs[0].getSubject().toString());
			email.setBody(getTextFromMessage(messageObjs[0]).getBytes());

			emailFolderObj.close(false);
			storeObj.close();

			return email;

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String getTextFromMessage(Message message) throws MessagingException, IOException {
		String result = "";
		if (message.isMimeType("text/plain")) {
			result = message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			result = getTextFromMimeMultipart(mimeMultipart);
		}
		return result;
	}

	private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
		String result = "";
		int count = mimeMultipart.getCount();
		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/plain")) {
				System.out.println("text/plain");
				result = result + "\n" + bodyPart.getContent();
				break;
			} else if (bodyPart.isMimeType("text/html")) {
				String html = (String) bodyPart.getContent();
				System.out.println("text/html");
				result = result + "\n" + Jsoup.parse(html).text();
			} else if (bodyPart.getContent() instanceof MimeMultipart) {
				System.out.println("multipart");
				result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
			}
		}
		return result;
	}
}
