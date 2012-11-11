package routing.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.swing.text.NumberFormatter;
import routing.control.ApplicationException;
import routing.control.entities.Edge;
import routing.control.entities.Node;
import routing.control.entities.Graph;
import routing.view.editor.Canvas;
import routing.RoutingDemo;

/**
 * A utility class for manipulating graphs, including conversion from and to
 * String, and managing clipboard operations.
 * 
 * @author PIAPAAI.ELTE
 */
public class GraphUtil {

	/**
	 * Converts mor file contents to graph objects.
	 * 
	 * @param value
	 *            the file contents as String
	 * @return the graph object.
	 * @throws ApplicationException
	 *             the file format is incorrect or the file cannot be opened
	 */
	public static Graph fromString(String value) throws ApplicationException {
		Graph retVal = new Graph();

		BufferedReader stringReader = new BufferedReader(
				new StringReader(value));
		String line = null;
		Boolean error = false;
		Boolean end = false;

		do {
			try {
				line = stringReader.readLine();
				end = line == null || line.trim().equals("");

				if (!error && !end) {
					retVal = parseOneLine(line, retVal);
				}
			} catch (IOException e) {
				error = true;
			}

		} while (!error && !end);

		if (error) {
			throw new ApplicationException("IncorrectFileFormat", "Error");
		}

		return retVal;
	}

	private static Graph parseOneLine(String line, Graph net)
			throws IOException {
		Boolean endLine = false;
		String word = null;
		List<String> words = new ArrayList<String>();
		int chr = 0;

		BufferedReader lineReader = new BufferedReader(new StringReader(line));

		do {
			word = "";
			do {
				chr = lineReader.read();

				if (chr == -1) {
					endLine = true;
				} else if (chr != (int) (' ')) {
					word += (char) chr;
				}
			} while (chr != (int) (' ') && !endLine);

			words.add(word);
		} while (!endLine);

		if (words.get(0).equals("n")) {
			Node p = parseOneNode(words, net);
			net.addNode(p);
		} else if (words.get(0).equals("e")) {
			parseOneEdge(words, net);
		} else if (words.get(0).equals("h")) {
			net.name = words.get(1);
		}

		return net;
	}

	private static Node parseOneNode(List<String> line, Graph net) {
		Node retVal = null;
		Integer i = 0;

		if (!line.get(i).equals("n")) {
			// Error?
		}

		++i;

		retVal = new Node(net);
		retVal.x = Double.parseDouble(line.get(i));
		++i;
		retVal.y = Double.parseDouble(line.get(i));
		++i;
		retVal.id = Integer.parseInt(line.get(i));
		++i;
		// retVal.weight = Double.parseDouble(line.get(i));
		// ++i;

		if (line.get(i).equals("n")) {
			if (line.get(line.size() - 1).equals("ne")) {
				++i;
				String label = "";
				String word = "";
				do {
					word = line.get(i);
					++i;

					if (word.charAt(0) == '{') {
						word = word.substring(1);
					}

					if (word.charAt(word.length() - 1) == '}') {
						word = word.substring(0, word.length() - 1);
					}

					word = word.replace("\\{", "{");
					word = word.replace("\\}", "}");

					if (i < line.size()) {
						label += " " + word;
					}
				} while (i != line.size());

				retVal.label = label;
			}
		} else {
			// Error?
		}

		return retVal;
	}

	private static String trimString(String value) {
		int begin = 0;
		int end = value.length();

		for (int i = 0; i < value.length(); ++i) {
			String at = value.substring(i, i + 1);

			if ((begin >= i - 1) && (at.equals(" ") || at.equals("{"))) {
				++begin;
			}
		}

		for (int i = value.length() - 1; i > 0; --i) {
			String at = value.substring(i, i + 1);

			if ((begin >= i - 1) && (at.equals(" ") || at.equals("}"))) {
				--end;
			}
		}

		return value.substring(begin, end);
	}

	private static void parseOneEdge(List<String> line, Graph net) {
		Edge retVal = null;
		Integer i = 0;

		if (!line.get(i).equals("e")) {
			// Error?
		}

		++i;
		int id1 = Integer.parseInt(line.get(i));
		++i;

		int id2 = Integer.parseInt(line.get(i));
		++i;

		double weight = Double.parseDouble(line.get(i));
		++i;

		if (line.get(i).equals("n")) {
		} else {
			// Error?
		}

		net.setWeight(id1, id2, weight);
	}

	/**
	 * A conversion function that converts a graph object to a mor file
	 * content string.
	 * 
	 * @param value
	 *            the graph object
	 * @return the net formatted as string
	 */
	public static String fromObject(Graph value) {
		String retVal = "";

		Iterator<Node> it = value.getNodeList().iterator();

		while (it.hasNext()) {
			retVal += toString(it.next()) + "\n";
		}

		Iterator<Edge> ite = value.getEdgeList().iterator();

		while (ite.hasNext()) {
			retVal += toString(ite.next()) + "\n";
		}

		retVal += "h " + value.name + " n\n";

		return retVal;
	}

	private static String toString(Node p) {
		String lbl = (p.label == null || p.label.trim().equals("") ? ""
				: p.label.trim().replace("{", "\\{").replace("}", "\\}"));

		if (lbl.indexOf(" ") != -1) {
			lbl = "{" + lbl + "}";
		}

		if (!lbl.equals("")) {
			lbl = " " + lbl + " ne";
		}

		return "n " + f(p.x) + " " + f(p.y) + " " + p.id + " n" + lbl;
	}

	private static String toString(Edge e) {
		return "e " + e.from.id + " " + e.to.id + " " + e.weight + " n";
	}

	private static String f(double value) {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(8);
		format.setMinimumFractionDigits(1);
		format.setDecimalFormatSymbols(DecimalFormatSymbols
				.getInstance(Locale.ENGLISH));

		NumberFormatter formatter = new NumberFormatter(format);

		try {
			return formatter.valueToString(value).replace(",", "");
		} catch (ParseException e) {
			return "";
		}
	}

	/**
	 * Gets the copied graph from the clipboard.
	 * 
	 * @return the graph net instance
	 */
	public static Graph fromClipboard() {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipBoardContents = c.getContents(RoutingDemo
				.getInstance());
		Graph retVal = null;

		try {
			retVal = fromString(clipBoardContents.getTransferData(
					DataFlavor.stringFlavor).toString());
			toClipBoard(fromString(clipBoardContents.getTransferData(
					DataFlavor.stringFlavor).toString()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return retVal;
	}

	/**
	 * Copies the given graph to the clipboard.
	 * 
	 * @param net
	 *            the net to copy.
	 */
	public static void toClipBoard(Graph net) {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();

		net.translate(Canvas.TRANSITION_WIDTH, Canvas.TRANSITION_WIDTH);
		net.name = "Clipboard contents";

		c.setContents(new StringSelection(fromObject(net)),
				(RoutingDemo) RoutingDemo.getInstance());
	}

}
