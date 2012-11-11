package routing.control;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import routing.control.entities.Graph;
import routing.control.entities.Session;
import routing.util.CompareUtil;
import routing.util.GraphUtil;
import routing.util.SessionUtil;

/**
 * A class that represents graph document file.
 * 
 * @author PIAPAAI.ELTE
 */
public class Document {

	private String _fileContents;

	/**
	 * The path to the file containing this net. "" or null means there isn't
	 * any.
	 */
	public String filePath;

	/**
	 * The name of the document. New documents get the name "New document",
	 * saved ones get the file name as document name.
	 */
	public String documentName;

	/**
	 * The graph instance.
	 */
	public Graph net;

	/**
	 * A collection of the sessions associated to the graph.
	 */
	public Vector<Session> sessions;

	/**
	 * Constructor.
	 */
	public Document() {
		_initDocument(null, "", "New Document");
	}

	/**
	 * Constructor.
	 * 
	 * @param filePath
	 *            the path to the file containing these contents.
	 * @param fileContents
	 *            the contents (in ndr format).
	 */
	public Document(String filePath, String fileContents) {
		_initDocument(filePath, fileContents, "New Document");
	}

	/**
	 * Constructor.
	 * 
	 * @param filePath
	 *            the path to the file containing these contents.
	 * @param fileContents
	 *            the contents (in ndr format).
	 * @param documentName
	 *            the name of the document
	 */
	public Document(String filePath, String fileContents, String documentName) {
		_initDocument(filePath, fileContents, documentName);
	}

	private void _initDocument(String filePath, String fileContents,
			String documentName) {
		this.documentName = documentName;
		this.filePath = filePath;
		this.setFileContents(fileContents);
	}

	/**
	 * File contetns getter.
	 * 
	 * @return file contents
	 */
	public String getFileContents() {
		_fileContents = GraphUtil.fromObject(net)
				+ SessionUtil.fromObject(sessions);

		return _fileContents;
	}

	/**
	 * File contents setter.
	 * 
	 * @param value
	 *            new contents of the file. Setting "" or null means erasing.
	 */
	public void setFileContents(String value) {
		_fileContents = value;

		if (value != null && !value.equals("")) {
			try {
				Pattern p = Pattern.compile("^(.*?\nh [^\n]+ n\n)(.*)$",
						Pattern.MULTILINE | Pattern.DOTALL);
				Matcher m = p.matcher(_fileContents);

				if (m.matches()) {
					net = GraphUtil.fromString(m.group(1));
					sessions = SessionUtil.fromString(m.group(2));
				} else {
					net = GraphUtil.fromString(_fileContents);
					sessions = new Vector<Session>();
				}
			} catch (ApplicationException e) {
				ErrorController.showError(e.getMessage(), e.getLevel());
			}
		} else {
			net = new Graph();
			sessions = new Vector<Session>();
		}
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Document)) {
			return false;
		}

		Document otherD = (Document) other;

		return CompareUtil.compare(getFileContents(), otherD.getFileContents())
				&& CompareUtil.compare(filePath, otherD.filePath)
				&& CompareUtil.compare(documentName, otherD.documentName)
				&& CompareUtil.compare(net, otherD.net);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 37
				* hash
				+ (this._fileContents != null ? this._fileContents.hashCode()
						: 0);
		hash = 37 * hash
				+ (this.filePath != null ? this.filePath.hashCode() : 0);
		hash = 37
				* hash
				+ (this.documentName != null ? this.documentName.hashCode() : 0);
		hash = 37 * hash + (this.net != null ? this.net.hashCode() : 0);
		return hash;
	}

}
