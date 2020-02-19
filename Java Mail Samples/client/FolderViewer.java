package client;

import java.awt.*;
import javax.mail.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * @author	Christopher Cotton
 * @author	Bill Shannon
 */

public class FolderViewer extends JPanel {

    FolderModel model = new FolderModel();
    JScrollPane scrollpane;
    JTable table;

    public FolderViewer() {
	this(null);
    }

    public FolderViewer(Folder what) {
	super(new GridLayout(1,1));

	table = new JTable(model);
	table.setShowGrid(false);

	scrollpane = new JScrollPane(table);

	// setup the folder we were given
	setFolder(what);
	
	// find out what is pressed
	table.getSelectionModel().addListSelectionListener(
	    new FolderPressed());
	scrollpane.setPreferredSize(new Dimension(700, 300));
	add(scrollpane);
    }

    /**
     * Change the current Folder for the Viewer
     *
     * @param what	the folder to be viewed
     */
    public void setFolder(Folder what) {
	try {
	    table.getSelectionModel().clearSelection();
	    if (SimpleClient.mv != null)
		SimpleClient.mv.setMessage(null);
	    model.setFolder(what);
	    scrollpane.invalidate();
	    scrollpane.validate();
	} catch (MessagingException me) {
	    me.printStackTrace();
	}
    }

    class FolderPressed implements ListSelectionListener {

	public void valueChanged(ListSelectionEvent e) {
	    if (model != null && !e.getValueIsAdjusting()) {
		ListSelectionModel lm = (ListSelectionModel) e.getSource();
		int which = lm.getMaxSelectionIndex();
		if (which != -1) {
		    // get the message and display it
		    Message msg = model.getMessage(which);
		    SimpleClient.mv.setMessage(msg);
		}
	    }
	}
    }
}
