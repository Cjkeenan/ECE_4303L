import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class EmailGui {
    private JList EmailFolderList;
    private JList Emails;
    private JTextArea EmailContents;

    public EmailGui() {
        EmailFolderList.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
            }
        });
    }
}
