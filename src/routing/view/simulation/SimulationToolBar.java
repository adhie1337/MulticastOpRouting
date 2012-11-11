package routing.view.simulation;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationContext;

import routing.RoutingDemo;
import routing.control.SimulationController;

public class SimulationToolBar extends JPanel implements ItemListener,
		ActionListener {

	private static SimulationToolBar toolBarInstance;

	private Timer timer;

	private JButton btnStep;
	private JButton btnReset;
	private JTextField tfInterval;
	private JLabel lbInterval;
	private JCheckBox chkAuto;

	public SimulationToolBar() {
		toolBarInstance = this;
		timer = new Timer(1000, this);
		initializeView();
	}

	private void initializeView() {
		ApplicationContext c = RoutingDemo.getApplication().getContext();
		ActionMap simActions = c.getActionMap(SimulationController
				.getInstance());
		ActionMap tbActions = c.getActionMap(this);

		setLayout(new FlowLayout(FlowLayout.LEADING, 10, 10));

		btnStep = new JButton(simActions.get("stepSimulationAction"));
		btnStep.setText("Step");
		add(btnStep);

		btnReset = new JButton(tbActions.get("resetAction"));
		btnReset.setText("Reset");
		add(btnReset);

		tfInterval = new JFormattedTextField(NumberFormat.getIntegerInstance());
		tfInterval.setColumns(6);
		tfInterval.setText("1000");
		add(tfInterval);

		lbInterval = new JLabel(" ms");
		add(lbInterval);

		chkAuto = new JCheckBox("auto step");
		chkAuto.addItemListener(this);
		add(chkAuto);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		tfInterval.setEditable(!chkAuto.isSelected());
		if (chkAuto.isSelected()) {
			try {
				timer.setDelay(NumberFormat.getIntegerInstance()
						.parse(tfInterval.getText()).intValue());
			} catch (Exception ex) {
				tfInterval.setText("1000");
				timer.setDelay(1000);
			}
			timer.start();
		} else {
			timer.stop();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SimulationController.getInstance().stepSimulationAction();
	}

	@Action
	public void resetAction() {
		unselectAutoStep();
		SimulationController.getInstance().resetSimulationAction();
	}

	public void closingDialog() {
		resetAction();
	}
	
	public void unselectAutoStep() {
		chkAuto.setSelected(false);
	}
}
