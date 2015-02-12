package dbPackage;

/**
 * User object to handle the challenge in Java
 *
 */
// added
public class User {

	private String username;
	private boolean[][] positions;
	private boolean[] colors;
	private String password;

	/**
	 * Constructor
	 * 
	 * @param username
	 * @param positions
	 * @param colors
	 * @param password
	 */
	public User(String username, boolean[][] positions, boolean[] colors, String password) {
		this.username = username;
		this.positions = positions;
		this.colors = colors;
		this.password = password;
	}

	/*
	 * Getters 
	 */
	public String getUsername() {
		return username;
	}
	public boolean[][] getPositions() {
		return positions;
	}
	public boolean[] getColors() {
		return colors;
	}
	public String getPassword() {
		return password;
	}
}
