package routing.view.simulation;

import java.awt.BorderLayout;

import javax.swing.JDialog;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import routing.RoutingDemo;

public class SimulationDialog extends JDialog {

	public SimulationDialog() {
		setModal(true);

		initializeView();
	}

	private void initializeView() {
		ApplicationContext c = Application.getInstance(RoutingDemo.class)
				.getContext();
		ResourceMap res = c.getResourceMap(SimulationDialog.class);

		setTitle(res.getString("SimulationDialog.Title"));

		setLayout(new BorderLayout());
		add(new SimulationToolBar(), BorderLayout.NORTH);
		add(new DataPanel(), BorderLayout.WEST);
		add(new ProgressPanel(), BorderLayout.EAST);
	}

	private static boolean shown = false;

	public static boolean isShown() {
		return shown;
	}

	public void showDialog() {
		shown = true;
		RoutingDemo.getApplication().show(this);
	}

	public void closeDialog() {
		dispose();
		RoutingDemo.getApplication().getMainFrame().repaint();
		shown = false;
	}
}
