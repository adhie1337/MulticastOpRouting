package routing.view.simulation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JDialog;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import routing.RoutingDemo;
import routing.control.EditorController;
import routing.view.MainFrame;
import routing.view.editor.DocumentEditor.EditorMode;
import routing.view.editor.RenderInfo;

public class SimulationDialog extends JDialog implements ComponentListener {

	private SimulationToolBar toolbar;
	private DataPanel dataPanel;
	private ProgressPanel progressPanel;

	public SimulationToolBar getToolbar() {
		return toolbar;
	}

	public DataPanel getDataPanel() {
		return dataPanel;
	}

	public ProgressPanel getProgressPanel() {
		return progressPanel;
	}
	
	public void updateStep(int step) {
		ApplicationContext c = Application.getInstance(RoutingDemo.class)
				.getContext();
		ResourceMap res = c.getResourceMap(SimulationDialog.class);
		if(step == -1) {
			setTitle(res.getString("SimulationDialog.Title"));
		} else {
			setTitle(res.getString("SimulationDialog.TitleWithStep") + " " + step);
		}
		
	}

	public SimulationDialog() {
		setModal(true);
		setMinimumSize(new Dimension(425, 325));
		setPreferredSize(new Dimension(425, 325));

		initializeView();
	}

	private void initializeView() {
		ApplicationContext c = Application.getInstance(RoutingDemo.class)
				.getContext();
		ResourceMap res = c.getResourceMap(SimulationDialog.class);

		setTitle(res.getString("SimulationDialog.Title"));

		setLayout(new BorderLayout());

		toolbar = new SimulationToolBar();
		add(toolbar, BorderLayout.NORTH);

		dataPanel = new DataPanel();
		dataPanel.setPreferredSize(new Dimension(200, 0));
		add(dataPanel, BorderLayout.WEST);

		progressPanel = new ProgressPanel();
		add(progressPanel, BorderLayout.CENTER);

		addComponentListener(this);
	}

	private static boolean shown = false;

	public static boolean isShown() {
		return shown;
	}

	public void showDialog() {
		shown = true;
		RoutingDemo.getApplication().show(this);

		MainFrame mf = RoutingDemo.getMF();
		mf.getCurrentEditor().setEditorMode(EditorMode.Simulation);
		mf.getCurrentEditor().repaint();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		toolbar.closingDialog();
		dispose();
		MainFrame mf = RoutingDemo.getMF();
		mf.getCurrentEditor().repaint();
		mf.getCurrentEditor().setEditorMode(EditorMode.Selection);

		RenderInfo ri = new RenderInfo();
		ri.session = RoutingDemo.getMF().sessionPanel.getSelectedSession();
		EditorController.setCurrentRenderInfo(ri);
		shown = false;
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

}