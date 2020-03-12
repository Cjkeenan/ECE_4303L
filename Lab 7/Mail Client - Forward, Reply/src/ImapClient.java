

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.JOptionPane;
import javax.mail.search.FlagTerm;

import com.sun.mail.imap.IMAPFolder;

/**
 * @author Chris McMahon-Stone
 *
 */
public class ImapClient {

	Properties prop;
	Store store;
	IMAPFolder folder;
	IMAPFolder sent;
	IMAPFolder trash;
	IMAPFolder archive;
	IMAPFolder junk;
	IMAPFolder drafts;
	final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	/**
	 * Constructor for the IMAP client, set properties and initialises a session
	 * 
	 * @param username
	 * @param password
	 */

	public ImapClient(String username, String password) {

		prop = System.getProperties();
		store = null;
		folder = null;
		sent = null;
		trash = null;
		archive = null;
		junk= null;
		drafts = null;
		setMailProperties(username, password);
		initialiseSession();

	}

	/**
	 * Returns the folder containing the mail in the inbox
	 *
	 * @return IMAPFolder
	 */
	public IMAPFolder getMailFolder() {
		return folder;
	}
	
	public IMAPFolder getSentFolder() {
		return sent;
	}
	
	public IMAPFolder getTrashFolder() {
		return trash;
	}
	public IMAPFolder getArchiveFolder() {
		return archive;
	}
	public IMAPFolder getJunkFolder() {
		return junk;
	}
	public IMAPFolder getDraftsFolder() {
		return drafts;
	}

	/**
	 * 
	 * Set the required IMAP properties
	 * 
	 * @param username
	 * @param password
	 */
	private void setMailProperties(String username, String password) {

		prop.put("mail.user", username);
		prop.put("mail.password", password);
		prop.put("mail.store.protocol", "imaps");
		//prop.put("mail.imap.host", "imap.googlemail.com");
        prop.put("mail.imaps.socketFactory.class", SSL_FACTORY);
        prop.put("mail.imaps.socketFactory.fallback", "false");
        prop.put("mail.imaps.port", "993");
        prop.put("mail.imaps.socketFactory.port", "993");
        prop.put("mail.imaps.host", "outlook.office365.com");

	}

	/**
	 * 
	 * Initialises the IMAP session, creates the store for storing and
	 * retrieving messages. Populates an IMAPFolder with the inbox content.
	 * 
	 */
	private void initialiseSession() {

		Session session = Session.getDefaultInstance(prop);

		try {
			// Try to initialise session with given credentials
			store = session.getStore(prop.getProperty("mail.store.protocol"));
			store.connect(prop.getProperty("mail.imap.host"),
					prop.getProperty("mail.user"),
					prop.getProperty("mail.password"));

			folder = (IMAPFolder) store.getFolder("INBOX"); // Get the inbox
															// folder
			sent = (IMAPFolder) store.getFolder("Sent Items");
			
			trash =(IMAPFolder) store.getFolder("Deleted Items");
			drafts =(IMAPFolder) store.getFolder("Drafts");
			junk =(IMAPFolder) store.getFolder("Junk Email");
			archive =(IMAPFolder) store.getFolder("Archive");
			
			
			/*Folder[] f = store.getDefaultFolder().list();
			for(Folder fd:f)
			    System.out.println(">> "+fd.getName());*/
			
		} catch (MessagingException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Login failed."); // Show error
																	// message
																	// if login
																	// fails.
			System.exit(0);
		}
	}

}
