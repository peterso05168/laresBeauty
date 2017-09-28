package util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {
    
	public static boolean MailSender(final String email,final String newPassword) {
		String MailTemplate = "Here is your new password: " + newPassword;
		String MailTitle = "Do not reply: Reset Password";
		String[] MailRecipients = { email };
        boolean success = false;
		final String username = "techsupport@laresbeauty.com";
		final String password = "techsupport123456";
		Properties props = new Properties();
		props.put("mail.smtp.host", "ns28.hostingspeed.net");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");

		System.out.println("Start Auth Account");
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			for (int i = 0; i < MailRecipients.length; i++) {
				System.out.println("Start Sending Email to " + MailRecipients[i]);
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress("techsupport@laresbeauty.com"));
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(MailRecipients[i]));
				message.setSubject(MailTitle);
				message.setText(MailTemplate);
				Transport.send(message);
			}
			System.out.println("Email sending finished");
			success = true;
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		return success;
	}
}
