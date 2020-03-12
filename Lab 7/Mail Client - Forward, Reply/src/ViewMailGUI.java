

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.mail.Session;

import com.sun.mail.imap.IMAPFolder;

//import jdk.nashorn.internal.scripts.JO;

//import com.sun.mail.imap.IMAPFolder;

/**
 * 
 * Represents the central GUI for viewing e-mails.
 * 
 * @author Chris McMahon-Stone
 *
 */

public class ViewMailGUI extends JFrame {

	private IMAPFolder inbox;
	private IMAPFolder sent;
	private IMAPFolder trash;
	private ImapClient imap;
	private DefaultListModel listmodel;
	private JPanel panel;
	private Message openMessage;
	private JTextArea messageArea;
	private Message[] emails;
	private boolean isRefresh = false;
	private List<String> keywords;
	private String[] messageBodies;
	private String keywordsPath = "src/networking/mail/client";

	/**
	 * Constructor for the GUI, takes the username and password to set up IMAP
	 * client
	 * 
	 * @param username
	 *            String
	 * @param password
	 *            String
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ViewMailGUI(String username, String password) {

		// Call JFrame constructor giving a name for the window
		super("View e-mails");

		imap = new ImapClient(username, password);

		// Create a new DefaultListModel to manage the emails in the list.
		listmodel = new DefaultListModel();

		// Add GUI components with absolute positioning.
		// (Mostly machine generated code)
		panel = new JPanel(new BorderLayout());
		panel.setSize(800, 400);
		getContentPane().add(panel, BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane();
		panel.add(splitPane);

		JList list = new JList(listmodel);
		list.setBorder(null);
		// Set so user can only select one email at a time.
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane scrollL = new JScrollPane(list);
		JButton refresh = new JButton("Refresh");
		JButton delete = new JButton("Delete");
		JButton recover = new JButton("Recover");

		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.add(scrollL, BorderLayout.CENTER);

		JPanel panel3 = new JPanel(new BorderLayout());
		panel2.add(panel3, BorderLayout.SOUTH);
		panel3.add(refresh, BorderLayout.EAST);
		panel3.add(delete,BorderLayout.WEST);
		panel3.add(recover,BorderLayout.CENTER);

		JMenuBar menu = new JMenuBar();
		JMenu email = new JMenu("Mail");
		JMenu file = new JMenu("File");
		JMenu subfolder = new JMenu("Folder");

		JMenuItem compose = new JMenuItem("Compose new e-mail");
		JMenuItem setFlags = new JMenuItem("Edit spam filter");
		JMenuItem sentFolder = new JMenuItem ("Sent Folder");
		JMenuItem trashFolder = new JMenuItem ("Trash Folder");
		JMenuItem draftFolder = new JMenuItem ("Draft Folder");
		JMenuItem archiveFolder = new JMenuItem ("Archive Folder");
		JMenuItem junkFolder = new JMenuItem ("Junk Folder");

		menu.add(file);
		menu.add(email);
		file.add(setFlags);
		email.add(compose);
		menu.add(subfolder);
		subfolder.add(sentFolder);
		subfolder.add(trashFolder);
		subfolder.add(draftFolder);
		subfolder.add(archiveFolder);
		subfolder.add(junkFolder);
		

		panel.add(menu, BorderLayout.NORTH);

		messageArea = new JTextArea();
		JScrollPane scrollR = new JScrollPane(messageArea);

		messageArea.setEditable(false);
		messageArea.setLineWrap(true);
		splitPane.setRightComponent(scrollR);
		splitPane.setLeftComponent(panel2);

		splitPane.setSize(300, 350);
		splitPane.setDividerLocation(0.6);

		openMessage = null;

		// Call the method to get the spam keywords from the file.
		populateKeywords();
		// Get the emails using the IMAP client.
		getEmails();

		// Add an action listener to the list to deal with selecting list item
		// events.
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!isRefresh)
					// Render the selected message into the JTextArea.
					renderSelectedMessage(list.getSelectedIndex());
			}

		});
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isRefresh) {
					deleteSelectedMessage(list.getSelectedIndex());
			}
			}
			
		});
		recover.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!isRefresh) {
					deleteSelectedMessage(list.getSelectedIndex());
			}
			}
			
		});
		

		// Add an action listener to the list refresh the email list when the
		// refresh button is pressed.
		refresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Clear the list and recall the getEmails method.
				isRefresh = true;
				list.clearSelection();
				getEmails();
				messageArea.setText("");
				isRefresh = false;
				openMessage = null;

			}
		});

		// Add an action listener to the compose email menu item.
		compose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Initialise a new compose message GUI.
				SendMessageGUI sendMessage = new SendMessageGUI();
				// Open the compose message GUI in the centre of the screen with
				// an appropriate size.
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				sendMessage.setLocation(dim.width / 2 - getSize().width / 2,
						dim.height / 2 - getSize().height / 2);
				sendMessage.setVisible(true);
			}
		});
		sentFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Clear the list and recall the getEmails method.
				isRefresh = true;
				list.clearSelection();
				getSent();
				messageArea.setText("");
				isRefresh = false;
				openMessage = null;

			}
			
		});
		trashFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Clear the list and recall the getEmails method.
				isRefresh = true;
				list.clearSelection();
				getTrash();
				messageArea.setText("");
				isRefresh = false;
				openMessage = null;

			}
			
		});
		draftFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Clear the list and recall the getEmails method.
				isRefresh = true;
				list.clearSelection();
				getDrafts();
				messageArea.setText("");
				isRefresh = false;
				openMessage = null;

			}
			
		});
		archiveFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Clear the list and recall the getEmails method.
				isRefresh = true;
				list.clearSelection();
				getArchive();
				messageArea.setText("");
				isRefresh = false;
				openMessage = null;

			}
			
		});
		junkFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Clear the list and recall the getEmails method.
				isRefresh = true;
				list.clearSelection();
				getJunk();
				messageArea.setText("");
				isRefresh = false;
				openMessage = null;

			}
			
		});
		
		// Add an action listener to the set flags
		setFlags.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Initialise a new flag manager GUI.
				@SuppressWarnings("serial")
				SetFlagsGUI flags = new SetFlagsGUI(keywords, keywordsPath) {

					// This method is called when you save the keywords in the
					// editor.
					@Override
					protected void onComplete() {
						populateKeywords();
						isRefresh = true;
						getEmails();
						isRefresh = false;
					}
				};
				// Open the set spam flags GUI in the centre of the screen with
				// an appropriate size.
				Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
				flags.setLocation(dim.width / 2 - getSize().width / 2,
						dim.height / 2 - getSize().height / 2);
				flags.setVisible(true);

			}
		});

	}

	/**
	 * Reads the keywords text file and populates the ArrayList of keywords.
	 */
	private void populateKeywords() {

		Path filePath = FileSystems.getDefault().getPath(keywordsPath,
				"keywords.text");
		// Only attempt to if the file exists.
		if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
			try {
				keywords = Files.readAllLines(filePath,
						Charset.defaultCharset());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"Failed to read spam keywords file.");
			}
		}
	}

	/**
	 * Renders the selected message into the TextArea on the left.
	 * 
	 * @param index
	 *            of the selected message
	 */
	@SuppressWarnings("unchecked")
	protected void renderSelectedMessage(int index) {

		openMessage = emails[emails.length - index - 1];
		try {
			// Change the SEEN flag to true when opening the message.
			if (!openMessage.getFlags().contains(Flag.SEEN)) {
				emails[index].setFlag(Flag.SEEN, true);
				listmodel.setElementAt("[READ]  " + openMessage.getSubject(),
						index); // Append a [Read] string to the subject line
			}
		} catch (MessagingException e1) {
			JOptionPane.showMessageDialog(null, "Failed to render message");
			e1.printStackTrace();
		}
		// Set the text of the message area.
		messageArea.setText(messageBodies[emails.length - index - 1]);

	}
	
	protected void deleteSelectedMessage(int index){
		try {
			if (emails[index].getFlags().contains(Flag.SEEN)) {
				emails[index].setFlag(Flag.DELETED, true);
				//inbox.close(true);
			}
		} catch (MessagingException e2) {
			JOptionPane.showMessageDialog(null, "Failed to delete message");
			e2.printStackTrace();
	}
		/*try {			
			inbox.open(Folder.READ_WRITE);
		} catch (MessagingException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	protected void recoverSelectedMessage(int index){
		try {
			emails[index].setFlag(Flag.DELETED, false);
			inbox.close(true);
		} catch (MessagingException e2) {
			JOptionPane.showMessageDialog(null, "Failed to recover message");
			e2.printStackTrace();
	}
		try {			
			inbox.open(Folder.READ_WRITE);
		} catch (MessagingException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Recursive method that converts each part of the message to a string.
	 * 
	 * @param type
	 *            of content
	 * @param content
	 *            itself
	 * @return the message as a string
	 * @throws IOException
	 * @throws MessagingException
	 */
	private String convertMessageToString(String type, Object content)
			throws IOException, MessagingException {
		// Base case for recursive call.
		if (type.contains("TEXT/")) {
			return content.toString();

		} 
		
		else if (type.contains("multipart/")) {

			Multipart multipart = (Multipart) content;
			StringBuilder sb = new StringBuilder();

			// Split the multipart up until we reach some plain text or
			// something that can't be rendered e.g. an image.

			for (int x = 0; x < multipart.getCount(); x++) {

				BodyPart bodyPart = multipart.getBodyPart(x);
				/*sb.append(convertMessageToString(bodyPart.getContentType(),
						bodyPart.getContent()));*/
				sb.append(bodyPart.getContent());

			}

			return sb.toString();
		} else {
			// Second base case for recursive call. This is returned if the type
			// of the multipart cannot be rendered.

			return "<Cant render	" + type.toString() + "	>";
		}

	}

	/**
	 * Gets the emails from the IMAP client and loads them into the list model.
	 */
	@SuppressWarnings("unchecked")
	private void getEmails() {

		// Close the inbox if still open.
		try {
			if (inbox != null)
				inbox.close(true);

			listmodel.clear();

			// Get the inbox folder
			inbox = imap.getMailFolder();

			// Open the inbox
			if (!inbox.isOpen())
				inbox.open(Folder.READ_WRITE);

			emails = inbox.getMessages();

			messageBodies = new String[emails.length];

			boolean spam = false;

			// For each email, starting with the newest, firstly, check whether
			// they are spam or not using the list of keywords and if they are
			// prepend a SPAM string to the subject.
			// Secondly, for emails that aren't spam, determine they're
			// FLAG.SEEN state and prepend appropriate strings to the subjects.

			for (int i = emails.length - 1; i >emails.length - 11 ; i--) {

				Boolean noSubject = false;

				messageBodies[i] = convertMessageToString(
						emails[i].getContentType(), emails[i].getContent());

				String messagebody = messageBodies[i].toLowerCase();

				if (emails[i].getSubject() == null) {
					noSubject = true;
				}
				
				/*for (String s : keywords) {

					if (!noSubject
							&& (messagebody.contains(s) || emails[i]
									.getSubject().toLowerCase().contains(s))) {
						spam = true;
						break;
					} else if (noSubject && messagebody.contains(s)) {
						spam = true;
						break;
					}
				}*/

				// PLEASE NOTE: I originally dealt with Flags.RECENT, however I
				// later realised when testing that GMail does not support this
				// so I removed that functionality.

				String subject;
				if (noSubject) {
					subject = "(no subject)";
				} else {
					subject = emails[i].getSubject();
				}

				if (spam) {
					listmodel.addElement("[SPAM]  " + subject);
				} else {

					if (emails[i].getFlags().contains(Flag.SEEN)) {
						listmodel.addElement("[READ]  " + subject);
					} else {
						listmodel.addElement("[NEW]  " + subject);

					}
				}
				spam = false;
			}
		} catch (MessagingException | IOException e) {
			JOptionPane.showMessageDialog(panel, e.getMessage());
			e.printStackTrace();

		}

	}
	private void getTrash() {

	// Close the inbox if still open.
	try {
		if (trash != null)
			trash.close(true);

		listmodel.clear();

		// Get the inbox folder
		trash = imap.getTrashFolder();

		// Open the inbox
		if (!trash.isOpen())
			trash.open(Folder.READ_WRITE);

		emails = trash.getMessages();

		messageBodies = new String[emails.length];

		boolean spam = false;

		// For each email, starting with the newest, firstly, check whether
		// they are spam or not using the list of keywords and if they are
		// prepend a SPAM string to the subject.
		// Secondly, for emails that aren't spam, determine they're
		// FLAG.SEEN state and prepend appropriate strings to the subjects.

		for (int i = emails.length - 1; i >emails.length - 11 ; i--) {

			Boolean noSubject = false;

			messageBodies[i] = convertMessageToString(
					emails[i].getContentType(), emails[i].getContent());

			String messagebody = messageBodies[i].toLowerCase();

			if (emails[i].getSubject() == null) {
				noSubject = true;
			}
			
			/*for (String s : keywords) {

				if (!noSubject
						&& (messagebody.contains(s) || emails[i]
								.getSubject().toLowerCase().contains(s))) {
					spam = true;
					break;
				} else if (noSubject && messagebody.contains(s)) {
					spam = true;
					break;
				}
			}*/

			// PLEASE NOTE: I originally dealt with Flags.RECENT, however I
			// later realised when testing that GMail does not support this
			// so I removed that functionality.

			String subject;
			if (noSubject) {
				subject = "(no subject)";
			} else {
				subject = emails[i].getSubject();
			}

			if (spam) {
				listmodel.addElement("[SPAM]  " + subject);
			} else {

				if (emails[i].getFlags().contains(Flag.SEEN)) {
					listmodel.addElement("[READ]  " + subject);
				} else {
					listmodel.addElement("[NEW]  " + subject);

				}
			}
			spam = false;
		}
	} catch (MessagingException | IOException e) {
		JOptionPane.showMessageDialog(panel, e.getMessage());
		e.printStackTrace();

	}

}
	private void getArchive() {

	// Close the inbox if still open.
	try {
		if (inbox != null)
			inbox.close(true);

		listmodel.clear();

		// Get the inbox folder
		inbox = imap.getArchiveFolder();

		// Open the inbox
		if (!inbox.isOpen())
			inbox.open(Folder.READ_WRITE);

		emails = inbox.getMessages();

		messageBodies = new String[emails.length];

		boolean spam = false;

		// For each email, starting with the newest, firstly, check whether
		// they are spam or not using the list of keywords and if they are
		// prepend a SPAM string to the subject.
		// Secondly, for emails that aren't spam, determine they're
		// FLAG.SEEN state and prepend appropriate strings to the subjects.

		for (int i = emails.length - 1; i >emails.length - 11 ; i--) {

			Boolean noSubject = false;

			messageBodies[i] = convertMessageToString(
					emails[i].getContentType(), emails[i].getContent());

			String messagebody = messageBodies[i].toLowerCase();

			if (emails[i].getSubject() == null) {
				noSubject = true;
			}
			
			/*for (String s : keywords) {

				if (!noSubject
						&& (messagebody.contains(s) || emails[i]
								.getSubject().toLowerCase().contains(s))) {
					spam = true;
					break;
				} else if (noSubject && messagebody.contains(s)) {
					spam = true;
					break;
				}
			}*/

			// PLEASE NOTE: I originally dealt with Flags.RECENT, however I
			// later realised when testing that GMail does not support this
			// so I removed that functionality.

			String subject;
			if (noSubject) {
				subject = "(no subject)";
			} else {
				subject = emails[i].getSubject();
			}

			if (spam) {
				listmodel.addElement("[SPAM]  " + subject);
			} else {

				if (emails[i].getFlags().contains(Flag.SEEN)) {
					listmodel.addElement("[READ]  " + subject);
				} else {
					listmodel.addElement("[NEW]  " + subject);

				}
			}
			spam = false;
		}
	} catch (MessagingException | IOException e) {
		JOptionPane.showMessageDialog(panel, e.getMessage());
		e.printStackTrace();

	}

}
	private void getJunk() {

		// Close the inbox if still open.
		try {
			if (inbox != null)
				inbox.close(true);

			listmodel.clear();

			// Get the inbox folder
			inbox = imap.getJunkFolder();

			// Open the inbox
			if (!inbox.isOpen())
				inbox.open(Folder.READ_WRITE);

			emails = inbox.getMessages();

			messageBodies = new String[emails.length];

			boolean spam = false;

			// For each email, starting with the newest, firstly, check whether
			// they are spam or not using the list of keywords and if they are
			// prepend a SPAM string to the subject.
			// Secondly, for emails that aren't spam, determine they're
			// FLAG.SEEN state and prepend appropriate strings to the subjects.

			for (int i = emails.length - 1; i >emails.length - 11 ; i--) {

				Boolean noSubject = false;

				messageBodies[i] = convertMessageToString(
						emails[i].getContentType(), emails[i].getContent());

				String messagebody = messageBodies[i].toLowerCase();

				if (emails[i].getSubject() == null) {
					noSubject = true;
				}
				
				/*for (String s : keywords) {

					if (!noSubject
							&& (messagebody.contains(s) || emails[i]
									.getSubject().toLowerCase().contains(s))) {
						spam = true;
						break;
					} else if (noSubject && messagebody.contains(s)) {
						spam = true;
						break;
					}
				}*/

				// PLEASE NOTE: I originally dealt with Flags.RECENT, however I
				// later realised when testing that GMail does not support this
				// so I removed that functionality.

				String subject;
				if (noSubject) {
					subject = "(no subject)";
				} else {
					subject = emails[i].getSubject();
				}

				if (spam) {
					listmodel.addElement("[SPAM]  " + subject);
				} else {

					if (emails[i].getFlags().contains(Flag.SEEN)) {
						listmodel.addElement("[READ]  " + subject);
					} else {
						listmodel.addElement("[NEW]  " + subject);

					}
				}
				spam = false;
			}
		} catch (MessagingException | IOException e) {
			JOptionPane.showMessageDialog(panel, e.getMessage());
			e.printStackTrace();

		}

	}
	private void getDrafts() {

		// Close the inbox if still open.
		try {
			if (inbox != null)
				inbox.close(true);

			listmodel.clear();

			// Get the inbox folder
			inbox = imap.getDraftsFolder();

			// Open the inbox
			if (!inbox.isOpen())
				inbox.open(Folder.READ_WRITE);

			emails = inbox.getMessages();

			messageBodies = new String[emails.length];

			boolean spam = false;

			// For each email, starting with the newest, firstly, check whether
			// they are spam or not using the list of keywords and if they are
			// prepend a SPAM string to the subject.
			// Secondly, for emails that aren't spam, determine they're
			// FLAG.SEEN state and prepend appropriate strings to the subjects.

			for (int i = emails.length - 1; i >emails.length - 11 ; i--) {

				Boolean noSubject = false;

				messageBodies[i] = convertMessageToString(
						emails[i].getContentType(), emails[i].getContent());

				String messagebody = messageBodies[i].toLowerCase();

				if (emails[i].getSubject() == null) {
					noSubject = true;
				}
				
				/*for (String s : keywords) {

					if (!noSubject
							&& (messagebody.contains(s) || emails[i]
									.getSubject().toLowerCase().contains(s))) {
						spam = true;
						break;
					} else if (noSubject && messagebody.contains(s)) {
						spam = true;
						break;
					}
				}*/

				// PLEASE NOTE: I originally dealt with Flags.RECENT, however I
				// later realised when testing that GMail does not support this
				// so I removed that functionality.

				String subject;
				if (noSubject) {
					subject = "(no subject)";
				} else {
					subject = emails[i].getSubject();
				}

				if (spam) {
					listmodel.addElement("[SPAM]  " + subject);
				} else {

					if (emails[i].getFlags().contains(Flag.SEEN)) {
						listmodel.addElement("[READ]  " + subject);
					} else {
						listmodel.addElement("[NEW]  " + subject);

					}
				}
				spam = false;
			}
		} catch (MessagingException | IOException e) {
			JOptionPane.showMessageDialog(panel, e.getMessage());
			e.printStackTrace();

		}

	}
	private void getSent() {
		// Close the inbox if still open.
		try {
			if (sent != null)
				sent.close(true);

			listmodel.clear();

			// Get the inbox folder
			sent = imap.getSentFolder();

			// Open the inbox
			if (!sent.isOpen())
				sent.open(Folder.READ_WRITE);

			emails = sent.getMessages();

			messageBodies = new String[emails.length];

			boolean spam = false;

			// For each email, starting with the newest, firstly, check whether
			// they are spam or not using the list of keywords and if they are
			// prepend a SPAM string to the subject.
			// Secondly, for emails that aren't spam, determine they're
			// FLAG.SEEN state and prepend appropriate strings to the subjects.

			for (int i = emails.length - 1; i > emails.length - 11; i--) {

				Boolean noSubject = false;

				messageBodies[i] = convertMessageToString(
						emails[i].getContentType(), emails[i].getContent());

				String messagebody = messageBodies[i].toLowerCase();

				if (emails[i].getSubject() == null) {
					noSubject = true;
				}
				
				/*for (String s : keywords) {

					if (!noSubject
							&& (messagebody.contains(s) || emails[i]
									.getSubject().toLowerCase().contains(s))) {
						spam = true;
						break;
					} else if (noSubject && messagebody.contains(s)) {
						spam = true;
						break;
					}
				}*/

				// PLEASE NOTE: I originally dealt with Flags.RECENT, however I
				// later realised when testing that GMail does not support this
				// so I removed that functionality.

				String subject;
				if (noSubject) {
					subject = "(no subject)";
				} else {
					subject = emails[i].getSubject();
				}

				if (spam) {
					listmodel.addElement("[SPAM]  " + subject);
				} else {

					if (emails[i].getFlags().contains(Flag.SEEN)) {
						listmodel.addElement("[READ]  " + subject);
					} else {
						listmodel.addElement("[NEW]  " + subject);

					}
				}
				spam = false;
			}
		} catch (MessagingException | IOException e) {
			JOptionPane.showMessageDialog(panel, e.getMessage());
			e.printStackTrace();

		}

	}

}
