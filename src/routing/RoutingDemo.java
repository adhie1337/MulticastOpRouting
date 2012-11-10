package routing;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import routing.control.DocumentController;
import routing.view.MainFrame;

/**
 *
 * @author PIAPAAI.ELTE
 */
public class RoutingDemo extends SingleFrameApplication implements ClipboardOwner {

    /**
     * The entry point of the application.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(RoutingDemo.class, args);
    }

    /**
     * The function called to initialize view.
     */
    @Override
    protected void startup() {
        show(new MainFrame(this));

        DocumentController.getInstance().newFileAction();
    }

    /**
     * The application instance getter.
     */
    public static RoutingDemo getApplication() {
        return Application.getInstance(RoutingDemo.class);
    }
    
    public static MainFrame getMF() {
    	return (MainFrame)getApplication().getMainView();
    }

    /**
     * lostOwnership listener
     * @param clipboard
     * @param contents
     */
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}
