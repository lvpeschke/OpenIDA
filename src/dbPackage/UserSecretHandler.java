package dbPackage;

/**
 * Static methods needed for conversion between the DB and Java
 *
 */
// added
public class UserSecretHandler {

	/**
	 * Convert a byte matrix to boolean matrix for positions.
	 * @param positions: byte[3] array
	 * @return the boolean matrix of bytes for the positions in the matrix
	 
	public static boolean[][] positionsByteToBool(byte[] positions) {
		boolean[][] result = new boolean[3][3];

		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				if ((positions[i] & (2^j)) == (2^j)) {
					result[i][j] = true;
				} else {
					result[i][j] = false;
				}
			}
		}
		return result;
	}
	*/

	/**
	 * Convert an int to boolean matrix for positions.
	 * @param positions
	 * @return the boolean matrix of bytes for the positions in the matrix
	 */
	public static boolean[][] positionsIntToBool(int positions) { // TODO check math!!!
		boolean[][] result = new boolean[3][3];

		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				int offset = 3*i+j;
				int binaryValue = (int) Math.pow(2.0, (double) offset);
				if ((positions & binaryValue) == binaryValue) {
					result[i][j] = true;
				} else {
					result[i][j] = false;
				}
			}
		}
		return result;
	}

	/**
	 * Convert a byte to a boolean matrix for the colors.
	 * @param colors: byte
	 * @return the boolean matrix of bytes for the colors in the matrix
	 
	public static boolean[] colorsByteToBool(byte colors) {
		boolean[] result = new boolean[6];

		for (int i=0; i<6; i++) {
			if ((colors & (2^i)) == (2^i)) {
				result[i] = true;
			} else {
				result[i] = false;
			}
		}
		return result;
	}
	*/

	/**
	 * Convert an int to a boolean matrix for the colors.
	 * @param colors
	 * @return the boolean matrix of bytes for the colors in the matrix
	 */
	public static boolean[] colorsIntToBool(int colors) {
		boolean[] result = new boolean[6];

		for (int i=0; i<6; i++) {
			int binaryValue = (int) Math.pow(2.0, ((double) i));
			if ((colors & binaryValue) == binaryValue) {
				result[i] = true;
			} else {
				result[i] = false;
			}
		}
		return result;
	}
}

