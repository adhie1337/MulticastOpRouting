package routing.view.simulation;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import routing.RoutingDemo;

public class ProgressPanel extends JPanel {

	private ResourceMap rm;
	private JPanel content;
	private HashMap<Integer, SessionProgress> sessionProgresses;

	public ProgressPanel() {
		ApplicationContext c = Application.getInstance(RoutingDemo.class)
				.getContext();
		rm = c.getResourceMap(ProgressPanel.class);

		setBorder(BorderFactory.createTitledBorder(rm
				.getString("ProgressPanel.Title")));

		setLayout(new BorderLayout());
		JScrollPane scroller = new JScrollPane();
		add(scroller, BorderLayout.CENTER);

		content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		scroller.setViewportView(content);

		clearSessions();
	}

	public void clearSessions() {
		content.removeAll();
		sessionProgresses = new HashMap<Integer, ProgressPanel.SessionProgress>();
	}

	public void addSession(int sessionId, String sessionName,
			Map<Integer, String> destData) {
		if (!sessionProgresses.containsKey(sessionId)) {
			SessionProgress sp = new SessionProgress(sessionName, destData);
			content.add(sp);
			sessionProgresses.put(sessionId, sp);
		}
	}

	public void setPercentage(int sessionId, int destId, int percentage) {
		if (sessionProgresses.containsKey(sessionId)) {
			SessionProgress sp = sessionProgresses.get(sessionId);
			sp.setDestPercentage(destId, percentage);
		}
	}

	class SessionProgress extends JPanel {

		private JLabel sessionNameLabel;
		private JProgressBar sessionProgressBar;
		private HashMap<Integer, JProgressBar> progressBars;

		public SessionProgress(String sessionName, Map<Integer, String> destData) {
			sessionNameLabel = new JLabel(sessionName);
			sessionProgressBar = new JProgressBar();
			progressBars = new HashMap<Integer, JProgressBar>();

			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1d;

			c.gridy = 0;
			c.gridx = 0;
			add(sessionNameLabel, c);

			c.gridx = 1;
			add(sessionProgressBar, c);

			for (Entry<Integer, String> dest : destData.entrySet()) {
				JLabel destLabel = new JLabel(dest.getValue());
				JProgressBar destBar = new JProgressBar(0, 100);

				c.gridy++;
				c.gridx = 0;
				add(destLabel, c);

				c.gridx = 1;
				add(destBar, c);

				progressBars.put(dest.getKey(), destBar);
			}

			JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
			c.gridy++;
			c.gridx = 0;
			c.gridwidth = 2;
			c.anchor = GridBagConstraints.NORTH;
			c.weighty = 1d;
			add(sep, c);
		}

		public void setDestPercentage(int destId, int percentage) {
			if (progressBars.containsKey(destId)) {
				progressBars.get(destId).setValue(percentage);

				int numBars = progressBars.size();
				int sumBars = 0;

				for (Entry<Integer, JProgressBar> ent : progressBars.entrySet()) {
					sumBars += ent.getValue().getValue();
				}

				sessionProgressBar.setValue((int) (sumBars / (double) numBars));
			}
		}
	}
}
