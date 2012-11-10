package routing.control;

/**
 * An exception class represents the errors that are specific to this
 * application.
 * 
 * @author PIAPAAI.ELTE
 */
public class ApplicationException extends Exception {

    private String _level;
	
    /**
     * Constructor.
     * @param message
     */
    public ApplicationException(String message, String level) {

        super(message);

        _level = level;
    }

    /**
     * Getter of the error level.
     * @return the level of the error ("Warning" or "Error")
     */
    public String getLevel()
    {
        return _level;
    }

}
