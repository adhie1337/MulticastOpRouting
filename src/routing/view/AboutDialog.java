package routing.view;

import routing.RoutingDemo;

import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

/**
 * The about dialog class.
 * @author PIAPAAI.ELTE
 */
public class AboutDialog extends JDialog {

    private JButton closeButton;

    private JLabel appNameLabel;
    private JLabel appDescLabel;
    private JLabel authorNameLabel;

    /**
     * Constructor.
     * @param owner the owner of the window.
     */
    public AboutDialog(java.awt.Frame owner) {
        super(owner);

        initializeView();
    }

    private void initializeView() {
        ApplicationContext c = Application.getInstance(RoutingDemo.class).getContext();
        ActionMap actionMap = c.getActionMap(AboutDialog.class, this);
        ResourceMap rm = c.getResourceMap(AboutDialog.class);

        setModal(true);
        setResizable(false);

        appNameLabel = new JLabel(rm.getString("AboutDialog.JLabel.AppName.Text"));
        appNameLabel.setFont(appNameLabel.getFont()
                .deriveFont(appNameLabel.getFont().getSize() + 4)
                .deriveFont(appNameLabel.getFont().getStyle()|java.awt.Font.BOLD));

        appDescLabel = new JLabel(rm.getString("AboutDialog.JLabel.AppDesc.Text"));

        authorNameLabel = new JLabel(rm.getString("AboutDialog.JLabel.AuthorName.Text"));

        closeButton = new JButton(actionMap.get("closeAboutDialogAction"));
        closeButton.setText(rm.getString("AboutDialog.JButton.Close.Text"));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGap(5)
            .addComponent(appNameLabel)
            .addComponent(appDescLabel)
            .addComponent(authorNameLabel)
            .addComponent(closeButton)
            .addGap(5)
        );
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGap(5)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(appNameLabel, GroupLayout.Alignment.LEADING)
                .addComponent(appDescLabel, GroupLayout.Alignment.LEADING)
                .addComponent(authorNameLabel, GroupLayout.Alignment.LEADING)
                .addComponent(closeButton, GroupLayout.Alignment.TRAILING)
            )
            .addGap(5)
        );

        getRootPane().setDefaultButton(closeButton);
    }

    /**
     * An action to close the dialog.
     */
    @Action
    public void closeAboutDialogAction() {
        dispose();
    }

}
