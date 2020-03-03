import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import javax.activation.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class SMTPClient extends JFrame {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  
  private JButton sendButton       = new JButton("Send Message");
  private JButton attachButton     = new JButton("Attach File");
  private JLabel      fromLabel    = new JLabel("From: "); 
  private JLabel      toLabel      = new JLabel("To: ");
  private JLabel      ccLabel      = new JLabel("CC: "); 
  private JLabel      bccLabel     = new JLabel("BCC: "); 
  private JLabel      hostLabel    = new JLabel("SMTP Server: "); 
  private JLabel      portLabel    = new JLabel("Port: "); 
  private JLabel      subjectLabel = new JLabel("Subject: ");
  private JLabel      attachLabel  = new JLabel("No Attachment");
  private JTextField  fromField    = new JTextField(40); 
  private JTextField  toField      = new JTextField(40);
  private JTextField  ccField      = new JTextField(40); 
  private JTextField  bccField     = new JTextField(40); 
  private JTextField  hostField    = new JTextField(40); 
  private JTextField  portField    = new JTextField(40); 
  private JTextField  subjectField = new JTextField(40); 
  private JTextArea   message      = new JTextArea(40, 72); 
  private JScrollPane jsp          = new JScrollPane(message);

  public SMTPClient() {
  
    super("SMTP Client");
    Container contentPane = this.getContentPane();
    contentPane.setLayout(new BorderLayout());
    
    // Defaults
    String host = "smtp.office365.com";
    String port = "587";
    String from = "cjkeenan@live.com";
    
    JPanel labels = new JPanel();
    labels.setLayout(new GridLayout(7, 1));
    labels.add(hostLabel);
    labels.add(portLabel);
    labels.add(fromLabel);
    labels.add(toLabel);
    labels.add(ccLabel);
    labels.add(bccLabel);
    labels.add(subjectLabel);
    
    JPanel fields = new JPanel();
    fields.setLayout(new GridLayout(7, 1));
    fields.add(hostField);
    fields.add(portField);
    fields.add(fromField);
    fields.add(toField);
    fields.add(ccField);
    fields.add(bccField);
    fields.add(subjectField);

    hostField.setText(host);
    portField.setText(port);
    fromField.setText(from);
    
    Box north = Box.createHorizontalBox();
    north.add(labels, BorderLayout.WEST);
    north.add(fields, BorderLayout.EAST);
    
    contentPane.add(north, BorderLayout.NORTH);
    
    message.setFont(new Font("Monospaced", Font.PLAIN, 12));
    contentPane.add(jsp, BorderLayout.CENTER);

    JPanel south = new JPanel();
    south.setLayout(new FlowLayout(FlowLayout.CENTER));
    south.add(sendButton);
    south.add(attachButton);
    south.add(attachLabel);
    sendButton.addActionListener(new SendAction());
    attachButton.addActionListener(new AttachAction());
    contentPane.add(south, BorderLayout.SOUTH);       
    
    this.pack(); 
  }

  class SendAction implements ActionListener {
  
    public void actionPerformed(ActionEvent evt) {
      
      try {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", hostField.getText());
        props.put("mail.smtp.port", portField.getText());
        
        Session mailConnection = Session.getInstance(props, new MailAuthenticator(fromField.getText()));
        final Message msg = new MimeMessage(mailConnection);
  
        Address to = new InternetAddress(toField.getText());
        Address from = new InternetAddress(fromField.getText());
      
        msg.setFrom(from);
        msg.setRecipient(Message.RecipientType.TO, to);
        msg.setSubject(subjectField.getText());

        // Setup for Carbon Copy and Blind Carbon Copy
        if(ccField.getText().isEmpty() == false){
          Address cc = new InternetAddress(ccField.getText());
          msg.addRecipient(Message.RecipientType.CC, cc);
        }
        if(bccField.getText().isEmpty() == false){
          Address bcc = new InternetAddress(bccField.getText());
          msg.addRecipient(Message.RecipientType.BCC, bcc);
        }

        // Setup Multipart
        Multipart multipart = new MimeMultipart();  

        // Setup for Message Text
        BodyPart messageBodyPart1 = new MimeBodyPart();
        messageBodyPart1.setContent(message.getText(), "text/plain");
        multipart.addBodyPart(messageBodyPart1);  

        // Setup for Attachments
        String attachment = attachLabel.getText();
        if(attachment.isEmpty() == false){
          MimeBodyPart messageBodyPart2 = new MimeBodyPart();

          DataSource source = new FileDataSource(attachment);
          messageBodyPart2.setDataHandler(new DataHandler(source));  
          messageBodyPart2.setFileName(attachment);

          multipart.addBodyPart(messageBodyPart2);
        }

        // Set Content as Multipart
        msg.setContent(multipart);
        
        // This can take a non-trivial amount of time so 
        // spawn a thread to handle it. 
        Runnable r = new Runnable() {
          public void run() {
            try {
              Transport.send(msg);
            }
            catch (Exception ex) {
              ex.printStackTrace(); 
            }
          } 
        };
      Thread t = new Thread(r);
      t.start();
      // message.setText("");
      }
      catch (Exception ex) {
        // I should really bring up a more specific error dialog here.
        ex.printStackTrace(); 
      }
    } 
  }

  class AttachAction implements ActionListener {
  
    public void actionPerformed(ActionEvent evt) {
      
      try {
        JFileChooser c = new JFileChooser();
        // Demonstrate "Open" dialog:
        int rVal = c.showOpenDialog(null);
        if (rVal == JFileChooser.APPROVE_OPTION) {
          attachLabel.setText(c.getCurrentDirectory().getCanonicalPath() + "\\" + c.getSelectedFile().getName());
        }
      }
      catch (Exception ex) {
        // I should really bring up a more specific error dialog here.
        ex.printStackTrace(); 
      }
    } 
  }

  public static void main(String[] args) {

    SMTPClient client = new SMTPClient();
    // Next line requires Java 1.3 or later. I want to set up the
    // exit behavior here rather than in the constructor since
    // other programs that use this class may not want to exit 
    // the application when the SMTPClient window closes.
    client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    client.setVisible(true);
  }
}
