package emails;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
	private static final String senderEmail = "irakliakhoba@gmail.com";
	private static final String appPassword = "zpwi xslt dpxf yxxs";
	
	public static void sendEmail(String recipientEmail, String subject, String bodyText) throws AddressException, MessagingException {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", 587);
		
		 Session session = Session.getInstance(properties, new Authenticator() {
	            @Override
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(senderEmail, appPassword);
	            }
	        });
		 
		 Message message = new MimeMessage(session);
		 message.setFrom(new InternetAddress(recipientEmail));
		 message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
         message.setSubject(subject);
         message.setText(bodyText);
         
         Transport.send(message);
         System.out.println("ელ-ფოსტა წარმატებით გაიგზავნა ამ მისამართზე: " + recipientEmail);
	} 
}
