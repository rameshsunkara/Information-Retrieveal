package edu.louisiana.cacs.csce561.assignment1.exception;

/**
 * 
 * Custom exception class for Information Retrieval operations
 * @author rsunkara
 *
 */
public class IRException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IRException(String message) {
        super(message);
    }

}
