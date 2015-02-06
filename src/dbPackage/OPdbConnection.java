package dbPackage;

import java.io.Console;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.swing.text.Position;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;

/**
 * The openID providers interface to the database
 * 
 */
public class OPdbConnection {

	private static OPdbConnection instance;

	// ------------- Change these ones
	// Replace the text within brackets (including the brackets themselves)
	// with your LiU ID and MySQL password, as shown below.
	//
	// If you have previously taken an IDA course where you used a MySQL
	// databse,
	// that database account is still valid. Otherwise you should have received
	// an e-mail with the password for your newly created database account.
	// If you don't have a database account at IDA, please contact your lab
	// assistant.
	// If you have forgotten your database password, you can get a new one here:
	// https://www.ida.liu.se/local/students/mysql/passwd.en.shtml
	// private String url = "jdbc:mysql://db-und.ida.liu.se/guila302";
	// private String user = "guila302"; // LiU ID
	// private String password = "guila3028057"; // MySQL password

	// database on my laptop
	private String url = "jdbc:mysql://localhost:3306/openida";
	private String user = "root"; // LiU ID
	private String password = ""; // MySQL password
	// -------------
	private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
	private static final String constRedirectURI = "redirect_uri";
	private static final String constState = "state";
	private static final String constNonce = "nonce";
	private static final String constClientID = "client_id";
	private static final String constResponseType = "response_type";
	private static final String constScope = "scope";

	private static final String constUsername = "userName";
	private static final String constExpectedAnswer = "expectedAnswer";
	private static final String constPositions = "positions";
	private static final String constColors = "colors";
	private static final String constPassword = "password";

	private static final String constOPAuthenticationRequestTable = "OPAuthenticationRequest";
	private static final String constOPUserTable = "OPUserTable";
	private static final String constOPClientIdTable = "OPClientIdTable";
	private static final String constOPLoggedInTable = "OPLoggedInTable";
	private static final String constSessionID = "session_id";
	private static final String constUserPicture = "userPic";

	/**
	 * Saves the Authenticationrequest as strings in the database
	 * 
	 * @param req
	 *            - The Authenticationrequest to be saved
	 */
	public void saveAuthRequestToDB(AuthenticationRequest req) {

		String redirectURI = req.getRedirectionURI().toString();
		String state = req.getState().toString();
		String nonce = req.getNonce().toString();
		String clientID = req.getClientID().toString();
		String reqsponseType = req.getResponseType().toString();
		String scope = req.getScope().toString();
		int i = 1;
		PreparedStatement pst = null;
		Connection connection = null;

		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("INSERT INTO " + constOPAuthenticationRequestTable
					+ "(" + constRedirectURI + ", " + constState + ", " + constNonce + ", "
					+ constClientID + ",  " + constResponseType + ", " + constScope
					+ ") VALUES( ?, ?, ?, ?, ?, ?)");

			pst.setString(i++, redirectURI);
			pst.setString(i++, state);
			pst.setString(i++, nonce);
			pst.setString(i++, clientID);
			pst.setString(i++, reqsponseType);
			pst.setString(i++, scope);
			pst.executeUpdate();

		} catch (SQLException e) {
			System.out.println("OPdbConnection - ERROR: problem saving the AuthenticationRequest");
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out
						.println("OPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Compares if there exists ONE AuthRequest in the db with the same state
	 * 
	 * @param req
	 *            - The AuthRequest to compare to
	 * @return - If there exist ONE AuthenticationRequest with the same state
	 */
	public boolean compareAuthRequest(AuthenticationSuccessResponse req) {
		String nonce = req.getState().toString();
		ResultSet rs = null;
		PreparedStatement pst = null;
		Connection connection = null;
		int i = 1;
		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("SELECT count(*) from "
					+ constOPAuthenticationRequestTable + " WHERE " + constNonce + " = ?");
			pst.setString(i++, nonce);
			rs = pst.executeQuery();
		} catch (SQLException e) {
			System.out
					.println("OPdbConnection - ERROR: problem checking if AuthenticationRequest is in db");
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out
						.println("OPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
				return false;
			}
		}
		try {
			int count = 0;
			if (rs.next()) {
				count = rs.getInt(1);
			} else {
				System.out.println("OPdbConnection - ERROR: No such AuthenticationRequest");
				return false;
			}
			if (count != 1) {
				System.out
						.println("OPdbConnection - ERROR: Strange amount of AuthenticationRequest");
				return false;
			}
		} catch (SQLException e) {
			System.out.println("OPdbConnection - ERROR: Problem counting the responses");
			e.printStackTrace();
			return false;
		}
		/*
		 * No error and only ONE AuthenticationRequest => return true
		 */
		return true;
	}

	/**
	 * Checks if the end user supplied username and password is of a valid user
	 * 
	 * @param username
	 * @param passwor
	 * @return - If at least one user with that password and username exists
	 */
	public boolean validateUserAuthentication(String username, String answer) {
		System.out.println(username);
		System.out.println(answer);
		ResultSet rs = null;
		PreparedStatement pst = null;
		Connection connection = null;
		int i = 1;
		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("SELECT count(*) from " + constOPUserTable
					+ " WHERE " + constUsername + " = ? AND " + constExpectedAnswer + " = ?");
			pst.setString(i++, username);
			if (answer != null)
				answer = answer.toUpperCase();
			pst.setString(i++, answer); // TODO careful with order
			rs = pst.executeQuery();
		} catch (SQLException e) {
			System.out.println("OPdbConnection - ERROR: problem checking if User is in db");
			e.printStackTrace();
			return false;
		}
		try {
			int count = 0;
			if (rs.next()) {
				count = rs.getInt(1);
			} else {
				System.out.println("OPdbConnection - ERROR: No such User");
				return false;
			}
			if (count < 1) {
				System.out.println("OPdbConnection - ERROR: Still no user");
				return false;
			}
		} catch (SQLException e) {
			System.out
					.println("OPdbConnection - ERROR: Problem counting the responses from usertable");
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out
						.println("OPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
				return false;
			}
		}
		/*
		 * No error and atleast ONE user => return true
		 */
		return true;
	}

	/**
	 * Fetches the AuthenticationRequest corresponding to the nonce supplied,
	 * encapsuled in a AuthAllowed class
	 * 
	 * @param nonce
	 *            - The nonce to check from
	 * @return - The AuthReques, in a AuthAllowed class
	 */
	public AuthenticationRequest getAuthenticationRequest(String nonce) {
		ResultSet rs = null;
		AuthenticationRequest req = null;
		PreparedStatement pst = null;
		Connection connection = null;
		int i = 1;

		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("SELECT * FROM " + constOPAuthenticationRequestTable
					+ " WHERE " + constNonce + " =?");
			pst.setString(i++, nonce);
			rs = pst.executeQuery();
			req = getSpecificAuthRequest(rs);
		} catch (SQLException ex) {
			System.out
					.println("OPdbConnection - ERROR: Selecting a specific AuthenticationRequest statement error");
			ex.printStackTrace();
			return null;
		} finally {
			closeConnection(rs, pst, connection);
		}
		return req;
	}

	/**
	 * Takes the resulstset and filters it into a AuthAllowed object if possible
	 * 
	 * @param rs2
	 *            - The resulstset from the db
	 * @return - A AuthAllowed object or null
	 */
	private AuthenticationRequest getSpecificAuthRequest(ResultSet rs) {
		if (rs == null) {
			System.out.println("OPdbConnection - ERROR: Specific AuthenticationRequest RS is null");
			return null;
		}
		int a = 0;
		AuthenticationRequest auth = null;
		try {
			while (rs.next()) {
				auth = tryCreateAuthAllowed(rs);
				a++;
			}
		} catch (SQLException e) {
			System.out.println("OPdbConnection - ERROR: Problem reading response");
			e.printStackTrace();
			return null;
		}
		if (a != 1) {
			System.out
					.println("OPdbConnection - ERROR: Strange ammount of that AuthenticationRequest in DB->"
							+ a);
			return null;
		}
		/*
		 * No error occurred and there exists ONE AuthenticationRequest with the
		 * same state, return AuthAllowed object
		 */
		// Should probably remove AuthenticationRequest if true TODO
		return auth;
	}

	/**
	 * Tries to create an AuthAllowed object from the result from the db
	 * 
	 * @param rs2
	 *            - The result from the db
	 * @return - The AuthAlowed obejct created
	 * @throws SQLException
	 */
	private AuthenticationRequest tryCreateAuthAllowed(ResultSet rs) throws SQLException {
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put(constRedirectURI, rs.getString(constRedirectURI));
		hashMap.put(constState, rs.getString(constState));
		hashMap.put(constNonce, rs.getString(constNonce));
		hashMap.put(constClientID, rs.getString(constClientID));
		hashMap.put(constResponseType, rs.getString(constResponseType));
		hashMap.put(constScope, rs.getString(constScope));
		AuthenticationRequest req = null;

		try {
			req = AuthenticationRequest.parse(hashMap);
		} catch (ParseException e) {
			System.out
					.println("OPdbConnection - ERROR: Could not create a AuthenticationRequest form the parameters");
			e.printStackTrace();
		}
		return req;
	}

	/**
	 * Checks if the clientid exists in the db
	 * 
	 * @param clientID
	 *            - The client id to check for
	 * @return
	 */
	public boolean validateCLientID(ClientID clientID) {
		String id = clientID.toString();
		ResultSet rs = null;
		PreparedStatement pst = null;
		Connection connection = null;
		int i = 1;
		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("SELECT count(*) from " + constOPClientIdTable
					+ " WHERE " + constClientID + " = ?");
			pst.setString(i++, id);
			rs = pst.executeQuery();
		} catch (SQLException e) {
			System.out.println("OPdbConnection - ERROR: problem checking if clientid is in db");
			e.printStackTrace();
			return false;
		}
		try {
			int count = 0;
			if (rs.next()) {
				count = rs.getInt(1);
			} else {
				System.out.println("OPdbConnection - ERROR: No such clientid");
				return false;
			}
			if (count < 1) {
				System.out.println("OPdbConnection - ERROR: No such clientid");
				return false;
			}
		} catch (SQLException e) {
			System.out.println("OPdbConnection - ERROR: Problem counting the responses");
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out
						.println("OPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Logs the user out, removing his session id from the database
	 * 
	 * @param id
	 * @return
	 */
	public boolean logOutUser(String id) {

		int i = 1;
		PreparedStatement pst = null;
		Connection connection = null;

		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("DELETE " + constOPLoggedInTable + " WHERE "
					+ constSessionID + " = ?");

			pst.setString(i++, id);
			pst.executeUpdate();

		} catch (SQLException e) {
			System.out.println("OPdbConnection - ERROR: problem loging out the user");
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out
						.println("OPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Method that saves the user session id in the database
	 * 
	 * @param id
	 */
	public void logInUser(String id) {

		int i = 1;
		PreparedStatement pst = null;
		Connection connection = null;

		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("INSERT INTO " + constOPLoggedInTable + "("
					+ constSessionID + ") VALUES(?)");

			pst.setString(i++, id);
			pst.executeUpdate();

		} catch (SQLException e) {
			System.out.println("OPdbConnection - ERROR: problem saving the SessionId");
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out
						.println("OPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Checks if a user with that sessionId is loggedIn
	 * 
	 * @param id
	 *            - users session id
	 * @return
	 */
	public boolean isLoggedIn(String id) {
		boolean isLoggedIn = false;

		ResultSet rs = null;
		PreparedStatement pst = null;
		Connection connection = null;
		int i = 1;

		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("SELECT COUNT(*) FROM " + constOPLoggedInTable
					+ " WHERE " + constSessionID + " =?");
			pst.setString(i++, id);
			rs = pst.executeQuery();
			isLoggedIn = checkIfUserIsLoggedIn(rs);
		} catch (SQLException ex) {
			System.out.println("OPdbConnection - ERROR: Select statement error");
			ex.printStackTrace();
			return false;
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out
						.println("OPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
				return false;
			}
		}

		return isLoggedIn;
	}

	private boolean checkIfUserIsLoggedIn(ResultSet rs) {
		int rowCount = 0;
		try {
			rs.next();
			rowCount = rs.getInt(1);
		} catch (SQLException e) {
			System.out.println("OPdbConnection - ERROR: Problem checking logged in user");
			e.printStackTrace();
			return false;
		}
		if (rowCount != 1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Private constructor for the OPdatabaseconnection
	 */
	private OPdbConnection() {
		try {
			Class.forName(DRIVER_CLASS);

			// This method may be called after the server has been run
			// atleast once to remove all trace of the previous work in the
			// database, could be done to make sure the database is not filled
			// with unessary amount of data

			clearDatabase();
			createAuthenticationtable();
			createClientIdTable();
			createUserTable();
			putClientIDInTable();
			putDefaultUserInTable();
			createLoggedInTable();
		} catch (ClassNotFoundException e) {
			System.out.println("OPdbConnection - ERROR: No such class exists");
			e.printStackTrace();
		}
	}

	/**
	 * Generates a connection to the users sql database
	 * 
	 * @return - A connection to the database
	 * @throws SQLException
	 */
	private Connection getDBConnection() throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	/**
	 * If a instance of the class doesn't exists it creates one otherwise return
	 * the one existing
	 * 
	 * @return
	 */
	public static synchronized OPdbConnection getConnection() {
		if (instance == null) {
			createClass();
		}
		return instance;
	}

	/**
	 * Creates the class as a singelton
	 */
	public static synchronized void createClass() {
		if (instance == null) {
			instance = new OPdbConnection();
		}
	}

	/**
	 * Drops all the tables at the relying party
	 */
	private void clearDatabase() {
		String drpTbl1 = "DROP TABLE IF EXISTS " + constOPAuthenticationRequestTable;
		String drpTbl2 = "DROP TABLE IF EXISTS " + constOPClientIdTable;
		String drpTbl3 = "DROP TABLE IF EXISTS " + constOPLoggedInTable;
		String drpTbl4 = "DROP TABLE IF EXISTS " + constOPUserTable;

		Statement stmt = null;
		Connection connection = null;
		try {
			connection = getDBConnection();
			stmt = connection.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {
			stmt.executeUpdate(drpTbl1);
			stmt.executeUpdate(drpTbl2);
			stmt.executeUpdate(drpTbl3);
			stmt.executeUpdate(drpTbl4);
			System.out.println("Dropped all the OP tables");
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * A method to create the UserTable
	 */
	private void createUserTable() {
		System.out.println("Creating userTable");
		Statement stmt = null;
		Connection connection = null;

		String sql = "CREATE TABLE IF NOT EXISTS " + constOPUserTable + " " + "(" + constUsername
				+ " VARCHAR(255), " + " " + constExpectedAnswer + " VARCHAR(12), " + constPositions
				+ " BINARY(3), " + constColors + " BINARY(1), " + constPassword + " VARCHAR(12), "
				+ constUserPicture + " mediumblob, " + " PRIMARY KEY ( " + constUsername + " ))";
		try {
			connection = getDBConnection();
			stmt = connection.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			stmt.executeUpdate(sql);
			System.out.println("Created table in given database...");
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException se) {
			}
		}
	}

	/**
	 * A method to create the ClientIdsTable
	 */
	private void createClientIdTable() {
		System.out.println("Creating ClientIDTable");
		Statement stmt = null;
		Connection connection = null;
		try {
			connection = getDBConnection();
			stmt = connection.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "CREATE TABLE IF NOT EXISTS " + constOPClientIdTable + " " + "("
				+ constClientID + " VARCHAR(255), " + " PRIMARY KEY ( " + constClientID + " ))";
		try {
			stmt.executeUpdate(sql);
			System.out.println("Created table in given database...");
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException se) {
			}
		}
	}

	/**
	 * A method to create the AuthenticationRequestTable
	 */
	private void createAuthenticationtable() {
		System.out.println("Creating AuthenticationRequestTable");
		Statement stmt = null;
		Connection connection = null;

		String sql = "CREATE TABLE IF NOT EXISTS " + constOPAuthenticationRequestTable + " " + "("
				+ constRedirectURI + " VARCHAR(255), " + " " + constState + " VARCHAR(255), " + " "
				+ constNonce + " VARCHAR(255), " + " " + constClientID + " VARCHAR(255), " + " "
				+ constResponseType + " VARCHAR(255), " + " " + constScope + " VARCHAR(255), "
				+ " " + " PRIMARY KEY ( " + constState + " ))";
		try {
			connection = getDBConnection();
			stmt = connection.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			stmt.executeUpdate(sql);
			System.out.println("Created table in given database...");
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException se) {
			}
		}
	}

	/**
	 * Creates a row in the clientIdTable which will containt the string
	 * 'tddd17ClientId'
	 */
	private void putClientIDInTable() {

		String clientID = "tddd17ClientId";
		int i = 1;
		PreparedStatement pst = null;
		Connection connection = null;

		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("INSERT INTO " + constOPClientIdTable + "("
					+ constClientID + ") VALUES(?)");

			pst.setString(i++, clientID);
			pst.executeUpdate();

		} catch (SQLException e) {
			System.out
					.println("OPdbConnection - ERROR: problem saving the ClientID - Probably already exists");
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out
						.println("OPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Creates a row in the UserTable which will contain the 'user':
	 * username='qwe' & password='asd'
	 */
	private void putDefaultUserInTable() { //TODO

		String username = "qwe";
		String password = "asd";
		byte[] positions = new byte[]{7, 0, 0}; // first row
		byte colors = 2^2 + 2^3 + 2^4; // green, red, blue
		saveNewUser(username, password, positions, colors);
	}

	/**
	 * Creates a table to know which users is logged in based on their session
	 * id
	 */
	private void createLoggedInTable() {

		System.out.println("Creating LoggedInTable");
		Statement stmt = null;
		Connection connection = null;
		try {
			connection = getDBConnection();
			stmt = connection.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "CREATE TABLE IF NOT EXISTS " + constOPLoggedInTable + " " + "("
				+ constSessionID + " VARCHAR(255), " + " PRIMARY KEY ( " + constSessionID + " ))";
		try {
			stmt.executeUpdate(sql);
			System.out.println("Created table in given database...");
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException se) {
			}
		}
	}

	/**
	 * Saves the user to the database
	 * 
	 * @param username
	 * @param password
	 * @param positions
	 * @param colors
	 */
	public void saveNewUser(String username, String password, byte[] positions, byte colors) {

		int i = 1;
		PreparedStatement pst = null;
		Connection connection = null;

		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("INSERT INTO " + constOPUserTable + "("
					+ constUsername + ", " + constExpectedAnswer + ", " + constPositions + ", "
					+ constColors + ", " + constPassword + ") VALUES(?, ?, ?, ?, ?)");

			pst.setString(i++, username);
			pst.setNull(i++, java.sql.Types.VARCHAR);
			pst.setBytes(i++, positions);
			pst.setByte(i++, colors);
			pst.setString(i++, password);
			pst.executeUpdate();

		} catch (SQLException e) {
			System.out
					.println("OPdbConnection - ERROR: problem saving the User - Probably already exists");
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out
						.println("OPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Returns the user picture as a blob object, or null if error occurs
	 * 
	 * @param userName
	 *            - The user asking for his picture
	 * @return - The users picture as an Blob or null if error
	 */
	public Blob loadUserPicture(String userName) {

		int i = 1;
		PreparedStatement pst = null;
		Connection connection = null;
		ResultSet rs = null;
		Blob blob = null;
		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("SELECT " + constUserPicture + " FROM "
					+ constOPUserTable + " WHERE " + constUsername + " = ?");

			pst.setString(i++, userName);
			rs = pst.executeQuery();

			if (rs.next()) {
				blob = rs.getBlob(constUserPicture);
			}

		} catch (SQLException ex) {
			System.out.println("OPdbConnection - ERROR: Select statement error");
			ex.printStackTrace();
			return null;
		} finally {
			closeConnection(rs, pst, connection);
		}
		return blob;
	}

	/**
	 * Saves the picture in the users table
	 * 
	 * @param userName
	 * @param inputStream
	 */
	public void savePicture(String userName, InputStream inputStream) {

		if (inputStream == null) {
			return;
		}

		int i = 1;
		PreparedStatement pst = null;
		Connection connection = null;
		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("UPDATE " + constOPUserTable + " SET "
					+ constUserPicture + " = ? WHERE " + constUsername + " = ?");

			pst.setBlob(i++, inputStream);
			pst.setString(i++, userName);
			pst.executeUpdate();

		} catch (SQLException e) {
			System.out.println("OPdbConnection - ERROR: problem saving the Picture");
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out
						.println("OPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
			}
		}
	}

	public User getUserInfo(String username) {

		ResultSet rs = null;
		User userInfo = null;

		PreparedStatement pst = null;
		Connection connection = null;
		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("SELECT " + constPositions + ", " + constColors
					+ ", " + constPassword + " " + " FROM " + constOPUserTable + " WHERE "
					+ constUsername + " = ?");

			pst.setString(1, username);
			rs = pst.executeQuery();

			byte[] positions;
			if (rs.next()) {
				positions = rs.getBytes(constPositions);
				byte colors = rs.getByte(constColors);
				String password = rs.getString(constPassword);

				boolean[] colorsBool = UserSecretHandler.colorsByteToBool(colors);
				boolean[][] positionsBool = UserSecretHandler.positionsByteToBool(positions);
				userInfo = new User(username, positionsBool, colorsBool, password);
			} else {
				throw new UserNotFoundException(username);
			}
			System.out.println("positions: " + positions);

		} catch (SQLException ex) {
			System.out.println("OPdbConnection - ERROR: Select statement error");
			ex.printStackTrace();
		} finally {
			closeConnection(rs, pst, connection);
		}
		return userInfo;
	}

	public void saveExpectedAnswerOfUser(String username, String answer) {
		if (username == null) {
			return;
		}

		int i = 1;
		PreparedStatement pst = null;
		Connection connection = null;
		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("UPDATE " + constOPUserTable + " SET "
					+ constExpectedAnswer + " = ? WHERE " + constUsername + " = ?");

			pst.setString(i++, answer);
			pst.setString(i++, username);
			pst.executeUpdate();

		} catch (SQLException e) {
			System.out.println("OPdbConnection - ERROR: problem saving the expected answer");
			e.printStackTrace();
		} finally {
			try {
				if (pst != null) {
					pst.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
				System.out
						.println("OPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
			}
		}
	}

	public void deleteExpectedAnswer(String username) {
		saveExpectedAnswerOfUser(username, null);

	}

	private void closeConnection(ResultSet rs, PreparedStatement pst, Connection connection) {
		try {
			if (pst != null) {
				pst.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException ex) {
			System.out
					.println("OPdbConnection - ERROR: Problem closing the connection to the database");
			ex.printStackTrace();
		}
	}
}
