import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.internet.MimeBodyPart;
import java.io.IOException;
import javax.mail.Part;


public class DownloadAttachments {
   public static void main(String args[]) 
   {
      Date date = null;

      
      Properties props = new Properties();
      props.put("mail.pop3.auth", "true");
      props.put("mail.pop3.ssl.enable", "true");
      props.put("mail.pop3s.host", "outlook.office365.com");
      props.put("mail.pop3s.port", "995");
      props.put("mail.smtp.host", "smtp.office365.com");
      props.put("mail.smtp.port", "587");
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      
      
      

      // session.setDebug(true);
      try 
      {


    	 // Connect to the POP3 server
    	 Session session = Session.getDefaultInstance(props, new MailAuthenticator());
    	 Store store = session.getStore("pop3s");
    	 store.connect();
    	 String saveDirectory = "C:\\Users\\eunsu\\Documents\\GitHub\\ECE_4303L\\Lab 6 - Mail Attachments\\Receiving Attachments\\Attachments";  
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
        	 OUTER_LOOP:
        	 for (int i = messages.length - 1; i >= 0; i--)
        	 {
               Message message = messages[i];
               date = message.getSentDate();
               // Get all the information from the message
               String from = InternetAddress.toString(message.getFrom());
               String contentType = message.getContentType();
               String messageContent = "";
               if (from != null) {
                  System.out.println("From: " + from);
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
               System.out.println("Message : ");

               Multipart multipart = (Multipart) messages[i].getContent();
               
               for (int x = 0; x < multipart.getCount(); x++) {
               BodyPart bodyPart = multipart.getBodyPart(x);

               String disposition = bodyPart.getDisposition();
               System.out.println(bodyPart.getContent());
               if (disposition != null && (disposition.equals(BodyPart.ATTACHMENT))) {
               System.out.println("Mail have some attachment");
               }
               }
               String attachFiles = "";
               if (contentType.contains("multipart")) {
                   // content may contain attachments
                   Multipart multiPart = (Multipart) message.getContent();
                   int numberOfParts = multiPart.getCount();
                   for (int partCount = 0; partCount < numberOfParts; partCount++) {
                       MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                       if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                           // this part is attachment
                           String fileName = part.getFileName();
                           attachFiles += fileName + ", ";
                           part.saveFile(saveDirectory + File.separator + fileName);
                       } else {
                           // this part may be the message content
                           messageContent = part.getContent().toString();
                       }
                   }

                   if (attachFiles.length() > 1) {
                       attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                   }
               } else if (contentType.contains("text/plain")
                       || contentType.contains("text/html")) {
                   Object content = message.getContent();
                   if (content != null) {
                       messageContent = content.toString();
                   }
               }              

               


               System.out.print("Do you want to view next message? [y/n] : ");
               String ans = reader.readLine();
               if ("N".equals(ans) || "n".equals(ans)) {
            	   System.out.print("Quiting email viewer program...");
            	   break OUTER_LOOP;
            	   
               }
               } 
           //end of for loop

         }
         else {
            System.out.println("There is no msg....");
            folder.close(false);
            store.close();
         }

      } catch (Exception e) {
         e.printStackTrace();
      }

   }

}