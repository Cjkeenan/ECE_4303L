import javax.mail.*;
import java.util.*;
import java.io.*;
import java.io.FileWriter;

public class POP3Client {

  public static void main(String[] args) {
    
    Properties props = new Properties();
    props.put("mail.pop3.auth", "true");
    props.put("mail.pop3.ssl.enable", "true");
    props.put("mail.pop3.host", "outlook.office365.com");
    props.put("mail.pop3.port", "995");

    String provider = "pop3";

    try {

      // Connect to the POP3 server
      Session session = Session.getDefaultInstance(props, new MailAuthenticator());
      Store store = session.getStore(provider);
      // store.connect(host, username, password);
      store.connect();
      
      // Open the folder
      Folder inbox = store.getFolder("INBOX");
      if (inbox == null) {
        System.out.println("No INBOX");
        System.exit(1);
      }  
      inbox.open(Folder.READ_ONLY);
      
      // Get the messages from the server
      // Message[] messages = inbox.getMessages(1, 20);
      Message[] messages = inbox.getMessages();
      FileWriter myWriter = new FileWriter("messages.html");
      for (int i = 0; i < messages.length; i++) {
        // System.out.println("------------ Message " + (i+1) + " ------------");
        // messages[i].writeTo(System.out);
        myWriter.write("<br><br>------------ Message " + (i+1) + " ------------<br>");
        myWriter.write(messages[i].getContent().toString());
      }
      myWriter.close();

      // Close the connection 
      // but don't remove the messages from the server
      inbox.close(false);
      store.close();  
    } 
    catch (Exception ex) {
      ex.printStackTrace();
    }    
  }
}
