package routing.control;

import routing.util.GraphUtil;
import routing.control.entities.Graph;
import routing.util.CompareUtil;

/**
 * A class that represents graph document file.
 * @author PIAPAAI.ELTE
 */
public class Document{


    private String _fileContents;

    /**
     * The path to the file containing this net. "" or null means there isn't any.
     */
    public String filePath;

    /**
     * The name of the document. New documents get the name "New document", saved ones get the file name as document name.
     */
    public String documentName;

    /**
     * The Petri net instance.
     */
    public Graph net;



    /**
     * Constructor.
     */
    public Document() {
        _initDocument(null, "", "New Document");
    }

    /**
     * Constructor.
     * @param filePath the path to the file containing these contents.
     * @param fileContents the contents (in ndr format).
     */
    public Document(String filePath, String fileContents) {
        _initDocument(filePath, fileContents, "New Document");
    }

    /**
     * Constructor.
     * @param filePath the path to the file containing these contents.
     * @param fileContents the contents (in ndr format).
     * @param documentName the name of the document
     */
    public Document(String filePath, String fileContents, String documentName) {
        _initDocument(filePath, fileContents, documentName);
    }

    private void _initDocument(String filePath, String fileContents, String documentName) {
        this.documentName = documentName;
        this.filePath = filePath;
        this.setFileContents(fileContents);
    }



    /**
     * File contetns getter.
     * @return file contents
     */
    public String getFileContents() {
        _fileContents = GraphUtil.fromPetriNet(net);

        return _fileContents;
    }

    /**
     * File contetns setter.
     * @param value new contetns of the file. Setting "" or null means erasing.
     */
    public void setFileContents(String value) {
        _fileContents = value;

        if(value != null && !value.equals(""))
        {
            try {
                net = GraphUtil.fromString(_fileContents);
            }
            catch(ApplicationException e) {
                ErrorController.showError(e.getMessage(), e.getLevel());
            }
        }
        else
        {
            net = new Graph();
        }
    }



    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Document)) {
            return false;
        }

        Document otherD = (Document)other;

        return CompareUtil.compare(getFileContents(), otherD.getFileContents())
                && CompareUtil.compare(filePath, otherD.filePath)
                && CompareUtil.compare(documentName,otherD.documentName)
                && CompareUtil.compare(net, otherD.net);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this._fileContents != null ? this._fileContents.hashCode() : 0);
        hash = 37 * hash + (this.filePath != null ? this.filePath.hashCode() : 0);
        hash = 37 * hash + (this.documentName != null ? this.documentName.hashCode() : 0);
        hash = 37 * hash + (this.net != null ? this.net.hashCode() : 0);
        return hash;
    }


}