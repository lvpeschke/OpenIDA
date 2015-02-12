package dbPackage;

/**
 * Exception thrown when no User object is found
 *
 */
// added
public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param username
	 */
	public UserNotFoundException(String username) {
		super(username);
	}
}
