import java.applet.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import java.util.Properties;
import java.awt.event.*;
import java.awt.*;

public class SMTPApplet extends Applet {

  private Button    sendButton   = new Button("Send Message");
  private Label     fromLabel    = new Label("From: "); 
  private Label     subjectLabel = new Label("Subject: ");
  private Label      toLabel      = new Label("To: ");
  private Label      hostLabel    = new Label("SMTP Server: ");
  private Label      portLabel    = new Label("Port: ");
  private TextField fromField    = new TextField(40); 
  private TextField subjectField = new TextField(40);
  private TextField  toField      = new TextField(40);
  private TextField  hostField    = new TextField(40);
  private TextField  portField    = new TextField(40);
  private TextArea  message      = new TextArea(30, 60);

  private String toAddress = toField.getText();

  public SMTPApplet() {
   this.setLayout(new BorderLayout());

    // Defaults
    String host = "smtp.office365.com";
    String port = "587";
    String from = "cjkeenan@live.com";

    JPanel labels = new JPanel();
    labels.setLayout(new GridLayout(5, 1));
    labels.add(hostLabel);
    labels.add(portLabel);
    labels.add(fromLabel);
    labels.add(toLabel);
    labels.add(subjectLabel);

    JPanel fields = new JPanel();
    fields.setLayout(new GridLayout(5, 1));
    fields.add(hostField);
    fields.add(portField);
    fields.add(fromField);
    fields.add(toField);
    fields.add(subjectField);

    hostField.setText(host);
    portField.setText(port);
    fromField.setText(from);

    Box north = Box.createHorizontalBox();
    north.add(labels);
    north.add(fields);

    this.add(north, BorderLayout.NORTH);

    message.setFont(new Font("Monospaced", Font.PLAIN, 12));
    this.add(message, BorderLayout.CENTER);

    JPanel south = new JPanel();
    south.setLayout(new FlowLayout(FlowLayout.CENTER));
    south.add(sendButton);
    sendButton.addActionListener(new SMTPApplet.SendAction());
    this.add(south, BorderLayout.SOUTH);
  }
  
  public void init() {
    
    String subject = this.getParameter("subject");
    if (subject == null) subject = "";
    subjectField.setText(subject);
    
    toAddress = this.getParameter("to");
    if (toAddress == null) toAddress = "";
    toField.setText(toAddress);
    
    String fromAddress = this.getParameter("from");
    if (fromAddress == null) fromAddress = ""; 
    fromField.setText(fromAddress);      
    
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
      
        msg.setContent(message.getText(), "text/plain");
        msg.setFrom(from);
        msg.setRecipient(Message.RecipientType.TO, to);
        msg.setSubject(subjectField.getText());
        
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
        
        message.setText("");
      }
      catch (Exception ex) {
        // We should really bring up a more specific error dialog here.
        ex.printStackTrace(); 
      }
    } 
  }
}
