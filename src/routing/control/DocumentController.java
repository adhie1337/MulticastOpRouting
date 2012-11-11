package routing.control;

import java.nio.file.Paths;
import java.util.LinkedList;

import org.jdesktop.application.Action;

import routing.RoutingDemo;

/**
 * The class responsible for the control of the documents being currently open.
 * 
 * @author PIAPAAI.ELTE
 */
public class DocumentController {

	/**
	 * The storage of the currently opened document references.
	 */
	public LinkedList<Document> openedDocuments;

	private FileController _fileController;

	private Document _currentDocument;

	private int _nextNewIndex = 0;

	/**
	 * The function that actually loads the file from the given path.
	 * 
	 * @param path
	 *            The path of the file to read
	 * @return the contents of the file
	 * @throws ApplicationException
	 *             when an error is raised during the read (like file doesn't
	 *             exist or file not readable)
	 */
	private String reloadFile(String path) throws ApplicationException {
		return _fileController.readFile(path);
	}

	/**
	 * Writes a document to the specified file path, using the serializer.
	 * 
	 * @param document
	 *            the document to write to the file
	 * @param _fileName
	 *            the path to the file to write to.
	 * @throws ApplicationException
	 *             when the document is null, or an error occured while writing
	 *             the file
	 */
	public void writeToFile(Document document, String _fileName)
			throws ApplicationException {
		if (document != null) {
			if (_fileName == null || _fileName.equals("")) {
				_fileName = _fileController.selectFile(false);
			}

			if (_fileName != null && !_fileName.trim().equals("")) {
				document.documentName = Paths.get(_fileName).getFileName()
						.toString();
				_fileController
						.writeFile(_fileName, document.getFileContents());
			}
		} else {
			throw new ApplicationException("NothingToBeDone", "Warning");
		}
	}

	/**
	 * Writes the document to it's given file path. When the path is not given
	 * to the document instance, the application will make the user select a
	 * file.
	 * 
	 * @param document
	 *            the document to write to the file
	 * @throws ApplicationException
	 *             when the document is null or an error occured while writing
	 *             the file
	 */
	public void writeToFile(Document document) throws ApplicationException {
		if (document != null) {
			if (document.filePath == null || document.filePath.equals("")) {
				writeToFile(document, _fileController.selectFile(false));
			} else {
				writeToFile(document, document.filePath);
			}
		} else {
			throw new ApplicationException("NothingToBeDone", "Warning");
		}
	}

	/**
	 * Writes the currently opened document to a file. When to filepath is
	 * specified by the document, the user will have to select a file.
	 * 
	 * @throws ApplicationException
	 *             when an error occured while writing the file
	 */
	public void writeToFile() throws ApplicationException {
		writeToFile(_currentDocument);
	}

	/**
	 * Removes the document from the currently opened documents.
	 * 
	 * @param value
	 *            the document that is being removed
	 */
	public void removeDocument(Document value) {
		if (openedDocuments.contains(value)) {
			openedDocuments.remove(value);

			RoutingDemo.getMF().removePage(value);

			if (_currentDocument == value) {
				if (openedDocuments.size() == 0) {
					newFileAction();
				} else {
					setCurrentDocument(openedDocuments.getFirst());
				}
			}
		}
	}

	/**
	 * Sets the document to the currently showed document. It commands the view
	 * to show the given document, as a side-effect. If document is not stored
	 * as a currently opened document, the method will do the addition, and
	 * create an editor for the document, in the view section.
	 * 
	 * @param value
	 */
	public void setCurrentDocument(Document value) {
		if (_currentDocument == null || !_currentDocument.equals(value)) {
			_currentDocument = value;

			if (!openedDocuments.contains(value)) {
				RoutingDemo.getMF().getNewPage(value);
				openedDocuments.add(value);
			} else {
				RoutingDemo.getMF().setCurrentPage(value);
			}
			RoutingDemo.getMF().checkActionsState();
		}
	}

	/**
	 * Gives the document that is currently being shown to the user.
	 * 
	 * @return the document
	 */
	public Document getCurrentDocument() {
		return _currentDocument;
	}

	@Action
	public void newFileAction() {
		setCurrentDocument(new Document(null, "", "New Document"
				+ (_nextNewIndex > 0 ? (" " + _nextNewIndex) : "")));
		++_nextNewIndex;
	}

	@Action
	public void loadFileAction() {
		try {
			String filePath = _fileController.selectFile(true);

			if (filePath == null || filePath.equals("")) {
				return;
			}

			String fileContents = _fileController.readFile(filePath);
			Document doc = new Document(filePath, fileContents);
			doc.documentName = Paths.get(filePath).getFileName().toString();
			setCurrentDocument(doc);
		} catch (ApplicationException e) {
			ErrorController.showError(e.getMessage(), e.getLevel());
		}

	}

	@Action
	public void reloadFileAction() {
		try {
			if (_currentDocument == null) {
				throw new ApplicationException("NothingToBeDone", "Error");
			}

			_currentDocument
					.setFileContents(reloadFile(_currentDocument.filePath));

			RoutingDemo.getMF().updateCurrentLabel();
			RoutingDemo.getMF().getCurrentEditor().setDocument(_currentDocument);
		} catch (ApplicationException e) {
			ErrorController.showError(e.getMessage(), e.getLevel());
		}

	}

	@Action
	public void saveFileAction() {
		try {
			writeToFile();

			RoutingDemo.getMF().updateCurrentLabel();
		} catch (ApplicationException e) {
			ErrorController.showError(e.getMessage(), e.getLevel());
		}
	}

	@Action
	public void saveFileAsAction() {
		try {
			writeToFile(_currentDocument, "");

		} catch (ApplicationException e) {
			ErrorController.showError(e.getMessage(), e.getLevel());
		}
	}

	@Action
	public void closeFileAction() {
		removeDocument(getCurrentDocument());
	}

	@Action
	public void changeCurrentFileAction() {
		setCurrentDocument(RoutingDemo.getMF().getCurrentPage());
	}

	/**
	 * The singleton reference to the only instance of the controller class.
	 */
	private static DocumentController _instance;

	/**
	 * Gets the singleton reference to the only instance of the controller
	 * class.
	 */
	public static DocumentController getInstance() {
		if (_instance == null) {
			_instance = new DocumentController();
		}

		return _instance;
	}

	/**
	 * Constructor.
	 */
	private DocumentController() {
		if (_instance != null) {
			throw new Error("Invalid use of singleton pattern!");
		}

		_fileController = new FileController();
		openedDocuments = new LinkedList<Document>();
	}

}
