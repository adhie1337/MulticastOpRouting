package routing.control;

import routing.view.ErrorDialog;
import routing.RoutingDemo;

/**
 * The class that handles the error event during the application runtime.
 * @author PIAPAAI.ELTE
 */
public class ErrorController {

    private static ErrorDialog _errorDialog;

    private static ErrorDialog getErrorDialog()
    {
        if (_errorDialog == null) {
            _errorDialog = new ErrorDialog(RoutingDemo.getApplication().getMainFrame());
        }

        return _errorDialog;
    }

    /**
     * Shows an error dialog with the specified contetns.
     * @param message the message to show in the dialog
     * @param title the title of the dialog
     */
    public static void showError(String message, String title)
    {
        getErrorDialog().showError(message, title);
    }

}
