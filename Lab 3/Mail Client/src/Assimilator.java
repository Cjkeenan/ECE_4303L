import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.io.*;

public class Assimilator {

  public static void main(String[] args) {
    final String username = "cjkeenan@live.com";

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "outlook.office365.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props, new MailAuthenticator(username));

    try {
      System.out.println("Enter recipient's email address: ");
      String recipient = System.console().readLine();
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));
      message.setRecipients(Message.RecipientType.TO,
          InternetAddress.parse(recipient));
      message.setContent("Resistance is futile. You will be assimilated!", "text/plain");
      message.setSubject("You must comply.");

      Transport.send(message);

      System.out.println("Done sending email to " + recipient);
    }
    catch (Exception ex) {
      ex.printStackTrace(); 
    }
    
  }
}
