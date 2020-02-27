import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ReplyToEmail {
   public static void main(String args[]) 
   {
      Date date = null;
      String provider = "pop3";

      Properties props = new Properties();
      props.put("mail.pop3.auth", "true");
      props.put("mail.pop3.ssl.enable", "true");
      props.put("mail.pop3.host", "outlook.office365.com");
      props.put("mail.pop3.port", "995");
      props.put("mail.smtp.host", "smtp.office365.com");
      props.put("mail.smtp.port", "587");
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      
      

      // session.setDebug(true);
      try 
      {


    	 // Connect to the POP3 server
    	 Session session = Session.getDefaultInstance(props, new MailAuthenticator());
    	 Store store = session.getStore(provider);
    	 store.connect();
    	    
         Folder folder = store.getFolder("inbox");
         if (!folder.exists()) {
            System.out.println("inbox not found");
               System.exit(0);
         }
         folder.open(Folder.READ_ONLY);

         BufferedReader reader = new BufferedReader(new InputStreamReader(
            System.in));

         Message[] messages = folder.getMessages();
         if (messages.length != 0) {

            //for (int i = 0, n = messages.length; i < n; i++) 
        	 for (int i = messages.length - 1; i >= 0; i--)
        	 {
               Message message = messages[i];
               date = message.getSentDate();
               // Get all the information from the message
               String from = InternetAddress.toString(message.getFrom());
               if (from != null) {
                  System.out.println("From: " + from);
               }
               String replyTo = InternetAddress.toString(message
	         .getReplyTo());
               if (replyTo != null) {
                  System.out.println("Reply-to: " + replyTo);
               }
               String to = InternetAddress.toString(message
	         .getRecipients(Message.RecipientType.TO));
               if (to != null) {
                  System.out.println("To: " + to);
               }

               String subject = message.getSubject();
               if (subject != null) {
                  System.out.println("Subject: " + subject);
               }
               Date sent = message.getSentDate();
               if (sent != null) {
                  System.out.println("Sent: " + sent);
               }

               System.out.print("Do you want to reply [y/n] : ");
               String ans = reader.readLine();
               if ("Y".equals(ans) || "y".equals(ans)) {

                  Message replyMessage = new MimeMessage(session);
                  replyMessage = (MimeMessage) message.reply(false);
                  replyMessage.setFrom(new InternetAddress(to));
                  replyMessage.setText("Thanks");
                  replyMessage.setReplyTo(message.getReplyTo());

                  // Send the message by authenticating the SMTP server
                  // Create a Transport instance and call the sendMessage
                  Transport t = session.getTransport("smtp");
                  try {
	   	     //connect to the smpt server using transport instance
		     //change the user and password accordingly	
	             //t.connect("ekoo@cpp.edu", "aA!61090606");
	             t.sendMessage(replyMessage,
                        replyMessage.getAllRecipients());
                  } finally {
                     t.close();
                  }
                  System.out.println("message replied successfully ....");

                  // close the store and folder objects
                  folder.close(false);
                  store.close();

               } else if ("n".equals(ans)) {
                  break;
               }
            }//end of for loop

         } else {
            System.out.println("There is no msg....");
         }

      } catch (Exception e) {
         e.printStackTrace();
      }

   }

}