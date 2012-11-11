package routing.view.simulation;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SimulationToolBar extends JPanel {

	private JButton btnStep;
	private JButton btnReset;
	private JTextField tfInterval;
	private JLabel lbInterval;
	private JCheckBox chkAuto;

	public SimulationToolBar() {
		initializeView();
	}

	private void initializeView() {
		setLayout(new FlowLayout(FlowLayout.LEADING, 10, 10));
		
		btnStep = new JButton("Step");
		add(btnStep);
		
		btnReset = new JButton("Reset");
		add(btnReset);
		
		tfInterval = new JTextField();
		tfInterval.setColumns(6);
		add(tfInterval);
		
		lbInterval = new JLabel(" ms");
		add(lbInterval);
		
		chkAuto = new JCheckBox("auto step");
		add(chkAuto);
	}
}
