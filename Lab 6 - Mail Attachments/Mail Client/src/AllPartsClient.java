import javax.mail.*;
import java.util.*;
import java.io.*;

public class AllPartsClient {

  public static void main(String[] args) {
    
    // if (args.length == 0) {
    //   System.err.println(
    //    "Usage: java AllPartsClient protocol://username@host:port/foldername");
    //   return; 
    // }
    // URLName server = new URLName(args[0]);
    URLName server = new URLName("pop3", "outlook.office365.com", 995, "INBOX", "cjkeenan@cpp.edu", null);

    try {

      Properties props = new Properties();
      props.put("mail.pop3.auth", "true");
      props.put("mail.pop3.ssl.enable", "true");
      props.put("mail.pop3.host", server.getHost());
      props.put("mail.pop3.port", server.getPort());

      Session session = Session.getDefaultInstance(props, new MailAuthenticator(server.getUsername()));

      // Connect to the server and open the folder
      Folder folder = session.getFolder(server);
      if (folder == null) {
        System.out.println("Folder " + server.getFile() + " not found.");
        System.exit(1);
      }  
      folder.open(Folder.READ_ONLY);
      
      // Get the messages from the server
      Message[] messages = folder.getMessages();
      FileWriter myWriter = new FileWriter("everything.html");
      for (int i = 0; i < messages.length; i++) {
        myWriter.write("<br><br>------------ Message " + (i+1) + " ------------<br>");
        
        // Print message headers
        Enumeration headers = messages[i].getAllHeaders();
        while (headers.hasMoreElements()) {
          Header h = (Header) headers.nextElement();
          myWriter.write(h.getName() + ": " + h.getValue() + "<br>");
        }       
        myWriter.write("<br>");
        
        // Enumerate parts
        Object body = messages[i].getContent();
        if (body instanceof Multipart) {
          processMultipart((Multipart) body, myWriter);          
        }
        else { // ordinary message
          processPart(messages[i], myWriter);
        }
        
        myWriter.write("<br>");
        
      }
      myWriter.close();

      // Close the connection 
      // but don't remove the messages from the server
      folder.close(false);
      
    } 
    catch (Exception ex) {
      ex.printStackTrace();
    }  
          
    // Since we may have brought up a GUI to authenticate,
    // we can't rely on returning from main() to exit
    System.exit(0);     
    
  }
  
  public static void processMultipart(Multipart mp, FileWriter writer) 
  throws MessagingException {

    for (int i = 0; i < mp.getCount(); i++) {
      processPart(mp.getBodyPart(i), writer);
    }
    
  }

  public static void processPart(Part p, FileWriter writer) {
    
    try {
      String fileName = p.getFileName();
      String disposition = p.getDisposition();
      String contentType = p.getContentType();
      if (contentType.toLowerCase().startsWith("multipart/")) {
      processMultipart((Multipart)  p.getContent(), writer );
      }
      else if (fileName == null 
      && (Part.ATTACHMENT.equalsIgnoreCase(disposition) 
      || !contentType.equalsIgnoreCase("text/plain"))) {
        // pick a random file name. This requires Java 1.2 or later.
        fileName = File.createTempFile("attachment", ".txt").getName();
      }
      if (fileName == null) { // likely inline
        writer.write(p.toString());
      }
      else {
        File f = new File(fileName);
        // find a file that does not yet exist
        for (int i = 1; f.exists(); i++) {
          String newName = fileName + " " + i;
          f = new File(newName);
        }
        OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        
        // We can't just use p.writeTo() here because it doesn't
        // decode the attachment. Instead we copy the input stream 
        // onto the output stream which does automatically decode
        // Base-64, quoted printable, and a variety of other formats.
        InputStream in = new BufferedInputStream(p.getInputStream());
        int b;
        while ((b = in.read()) != -1) out.write(b); 
        out.flush();
        out.close();
        in.close();
      }
    }    
    catch (Exception ex) {
      System.err.println(ex);
      ex.printStackTrace();
    }
  // since we brought up a GUI returning from main() won't exit
  System.exit(0);   
  }
}
