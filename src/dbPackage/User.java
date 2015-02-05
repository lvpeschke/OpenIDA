package dbPackage;

public class User {

	private String username;
	private boolean[][] positions;
	private boolean[] colors;
	private String password;

	public User(String username, boolean[][] positions, boolean[] colors, String password) {
		this.username = username;
		this.positions = positions;
		this.colors = colors;
		this.password = password;
	}

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
