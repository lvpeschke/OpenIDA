package dbPackage;

public class UserSecretHandler {
	
	// matrix positions
	/*private static final int MATRIX11 = 1;
	private static final int MATRIX12 = 2;
	private static final int MATRIX13 = 4;
	
	private static final int MATRIX21 = 1;
	private static final int MATRIX22 = 2;
	private static final int MATRIX23 = 4;
	
	private static final int MATRIX31 = 1;
	private static final int MATRIX32 = 2;
	private static final int MATRIX33 = 4;
	
	// colors
	private static final int BLACK = 1;
	private static final int BROWN = 2;
	private static final int BLUE = 4;
	private static final int RED = 8;
	private static final int GREEN = 16;
	private static final int PURPLE = 32;*/
	
	/**
	 * Convert a byte matrix to boolean matrix for positions.
	 * @param positions: byte[3] array
	 * @return the boolean matrix of bytes for the positions in the matrix
	 */
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
	
	/**
	 * Convert a byte to a boolean matrix for the colors.
	 * @param colors: byte
	 * @return the boolean matrix of bytes for the colors in the matrix
	 */
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
}

