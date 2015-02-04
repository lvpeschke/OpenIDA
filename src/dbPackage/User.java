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

	// TODO
	public User(String username, byte[] positions, boolean[] colors, String password) {
		this.username = username;
		// setPositions(positions);
		this.colors = colors;
		this.password = password;
	}

	private void setPositions(boolean[] positions) {
		int length = positions.length;
		this.positions = new boolean[3][3];

		for (int i = 0; i < length; i++) {
			this.positions[(int) i / 3][i % 3] = positions[i];
		}
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
