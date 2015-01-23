package dbPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;

/**
 * The relying party's interface to the database
 * 
 */
public class RPdbConnection {

	private static RPdbConnection instance;

	// ------------- Change these ones
	// Replace the text within brackets (including the brackets themselves)
	// with your LiU ID and MySQL password, as shown below.
	// 
	// If you have previously taken an IDA course where you used a MySQL databse,
	// that database account is still valid. Otherwise you should have received
	// an e-mail with the password for your newly created database account.
	// If you don't have a database account at IDA, please contact your lab
	// assistant.
	// If you have forgotten your database password, you can get a new one here:
	// https://www.ida.liu.se/local/students/mysql/passwd.en.shtml
	private String url = "jdbc:mysql://db-und.ida.liu.se/guila302";
	private String user = "guila302"; // LiU ID
	private String password = "guila3028057"; // MySQL password
	// -------------
	private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
	private static final String constOPURI = "OPURI";
	private static final String constRedirectURI = "redirect_uri";
	private static final String constState = "state";
	private static final String constNonce = "nonce";
	private static final String constClientID = "client_id";
	private static final String constResponseType = "response_type";
	private static final String constScope = "scope";
	private static final String constRPAuthenticationRequestTable = "RPAuthenticationRequest";
	private static final String constSessionID = "session_id";
	private static final String constUserName = "user_name";
	private static final String constSessionIdToNameTable = "RPSessionIdTable";
	private static final String constRPLoggedInTable = "RPLoggedInTable";

	/**
	 * Saves the Authenticationrequest as strings in the database
	 * 
	 * @param req
	 *            - The Authenticationrequest to be saved
	 */
	public void saveAuthRequestToDB(AuthenticationRequest req) {

		String OPURI = req.getEndpointURI().toString();
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
			pst = connection.prepareStatement("INSERT INTO "
					+ constRPAuthenticationRequestTable + "(" + constOPURI
					+ ", " + constRedirectURI + ", " + constState + ", "
					+ constNonce + ", " + constClientID + ",  "
					+ constResponseType + ", " + constScope
					+ ") VALUES(?, ?, ?, ?, ?, ?, ?)");

			pst.setString(i++, OPURI);
			pst.setString(i++, redirectURI);
			pst.setString(i++, state);
			pst.setString(i++, nonce);
			pst.setString(i++, clientID);
			pst.setString(i++, reqsponseType);
			pst.setString(i++, scope);
			pst.executeUpdate();

		} catch (SQLException e) {
			System.out
					.println("RPdbConnection - ERROR: problem saving the AuthenticationRequest");
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
						.println("RPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Checks if there exists a AuthenticationRequest in the db which
	 * corresponds to the AuthenticationSuccessResponse
	 * 
	 * @param req
	 *            - The AuthenticationSuccesResponse we will check with
	 * @return - If there exists such a AuthenticationRequest
	 */
	public boolean compareAuthRequest(AuthenticationSuccessResponse req) {

		String state = req.getState().toString();
		ResultSet rs = null;
		PreparedStatement pst = null;
		Connection connection = null;
		boolean validresponse = false;
		int i = 1;

		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("SELECT * FROM "
					+ constRPAuthenticationRequestTable + " WHERE "
					+ constState + " =?");
			pst.setString(i++, state);
			rs = pst.executeQuery();
			validresponse = checkIfValidResponse(rs);
		} catch (SQLException ex) {
			System.out
					.println("RPdbConnection - ERROR: Select statement error");
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
						.println("RPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
				return false;
			}
		}
		return validresponse;
	}

	/**
	 * Check if the response from the db is a valid one
	 * 
	 * @param rs
	 *            - The response from the db
	 * @return - If the response is valid
	 */
	private boolean checkIfValidResponse(ResultSet rs) {
		if (rs == null) {
			System.out.println("RPdbConnection - ERROR: RS is null");
			return false;
		}
		int a = 0;
		try {
			while (rs.next()) {
				a++;
			}
		} catch (SQLException e) {
			System.out
					.println("RPdbConnection - ERROR: Problem reading response");
			e.printStackTrace();
			return false;
		}
		if (a != 1) {
			System.out
					.println("RPdbConnection - ERROR: Strange amount of specific authenticationRequests->"
							+ a);
			return false;
		}
		/*
		 * No error occurred and there exists ONE AuthenticationRequest with the
		 * same state, return true
		 */
		return true;
	}

	/**
	 * Saves session id to name in the db, and calls the method to put him in
	 * the logged in users table
	 * 
	 * @param id
	 *            - the session id
	 * @param name
	 *            - username
	 */
	public void saveSessionIDToName(String id, String name) {

		int i = 1;
		PreparedStatement pst = null;
		Connection connection = null;

		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("INSERT INTO "
					+ constSessionIdToNameTable + "(" + constSessionID + ", "
					+ constUserName + ") VALUES(?, ?)");

			pst.setString(i++, id);
			pst.setString(i++, name);
			pst.executeUpdate();

		} catch (SQLException e) {
			System.out
					.println("RPdbConnection - ERROR: problem saving the SessionIDToName");
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
						.println("RPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
			}
		}
		logInUser(id);
	}

	/**
	 * Get the username corresponding to the session id
	 * 
	 * @param id
	 * @return
	 */
	public String getSessionIdName(String id) {
		String name = "John Doe";

		ResultSet rs = null;
		PreparedStatement pst = null;
		Connection connection = null;
		int i = 1;

		try {
			connection = getDBConnection();
			pst = connection.prepareStatement("SELECT * FROM "
					+ constSessionIdToNameTable + " WHERE " + constSessionID
					+ " =?");
			pst.setString(i++, id);
			rs = pst.executeQuery();
			name = checkUserName(rs);
		} catch (SQLException ex) {
			System.out
					.println("RPdbConnection - ERROR: Select statement error");
			ex.printStackTrace();
			return name;
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
						.println("RPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
				return name;
			}
		}

		return name;
	}

	/**
	 * Checks so that the resultset only contains one userName, if not or error
	 * occurs "John Doe" is returned
	 * 
	 * @param rs
	 *            - The resulset
	 * @return - The userName
	 */
	private String checkUserName(ResultSet rs) {
		String name = "John Doe";
		int a = 0;
		try {
			while (rs.next()) {
				a++;
				name = rs.getString(constUserName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "John Doe";
		}
		if (a != 1) {
			return "John Doe";
		}
		return name;
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
			pst = connection.prepareStatement("DELETE " + constRPLoggedInTable
					+ " WHERE " + constSessionID + " = ?");

			pst.setString(i++, id);
			pst.executeUpdate();

		} catch (SQLException e) {
			System.out
					.println("RPdbConnection - ERROR: problem loging out the user");
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
						.println("RPdbConnection - ERROR: Problem closing the connection to the database");
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
			pst = connection.prepareStatement("INSERT INTO "
					+ constRPLoggedInTable + "(" + constSessionID
					+ ") VALUES(?)");

			pst.setString(i++, id);
			pst.executeUpdate();

		} catch (SQLException e) {
			System.out.println("RPdbConnection - ERROR: problem login in");
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
						.println("RPdbConnection - ERROR: Problem closing the connection to the database");
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
			pst = connection
					.prepareStatement("SELECT COUNT(*) FROM "
							+ constRPLoggedInTable + " WHERE " + constSessionID
							+ " =?");
			pst.setString(i++, id);
			rs = pst.executeQuery();
			isLoggedIn = checkIfUserIsLoggedIn(rs);
		} catch (SQLException ex) {
			System.out
					.println("RPdbConnection - ERROR: Select statement error");
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
						.println("RPdbConnection - ERROR: Problem closing the connection to the database");
				ex.printStackTrace();
				return false;
			}
		}

		return isLoggedIn;
	}

	/**
	 * Checks so that one and only one user exists in the resultset
	 * 
	 * @param rs
	 * @return
	 */
	private boolean checkIfUserIsLoggedIn(ResultSet rs) {
		int rowCount = 0;
		try {
			rs.next();
			rowCount = rs.getInt(1);
		} catch (SQLException e) {
			System.out
					.println("RPdbConnection - ERROR: Problem checking logged in user");
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
	 * Private constructor for the RPdatabaseconnection
	 */
	private RPdbConnection() {
		try {
			Class.forName(DRIVER_CLASS);

			// This method may be called after the server has been run
			// atleast once to remove all trace of the previous work in the
			// database, could be done to make sure the database is not filled
			// with unessary amount of data

			// clearDatabase();
			createAuthenticationtable();
			createSessionTable();
			createLoggedInTable();
		} catch (ClassNotFoundException e) {
			System.out.println("RPdbConnection - ERROR: No such class exists");
			e.printStackTrace();
		}
	}

	/**
	 * If the connection doesn't exists it creates one otherwise return the one
	 * existing
	 * 
	 * @return
	 */
	public static synchronized RPdbConnection getConnection() {
		if (instance == null){
			createClass();
		}
		return instance;
	}

	/**
	 * Creates the class as a singelton
	 */
	public static synchronized void createClass() {
		if (instance == null) {
			instance = new RPdbConnection();
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
	 * Drops all the tables at the relying party
	 */
	private void clearDatabase() {
		String drpTbl1 = "DROP TABLE " + constSessionIdToNameTable;
		String drpTbl2 = "DROP TABLE " + constRPAuthenticationRequestTable;
		String drpTbl3 = "DROP TABLE " + constRPLoggedInTable;

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
			System.out.println("Dropped all the RP tables");
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * A method to create the SessionId to UserName table
	 */
	private void createSessionTable() {

		System.out.println("Creating SessionIDTable");
		Statement stmt = null;
		Connection connection = null;
		try {
			connection = getDBConnection();
			stmt = connection.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "CREATE TABLE IF NOT EXISTS " + constSessionIdToNameTable
				+ " " + "(" + constSessionID + " VARCHAR(255), " + " "
				+ constUserName + " VARCHAR(255), " + " PRIMARY KEY ( "
				+ constSessionID + " ))";
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
		try {
			connection = getDBConnection();
			stmt = connection.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String sql = "CREATE TABLE IF NOT EXISTS "
				+ constRPAuthenticationRequestTable + " " + "(" + constOPURI
				+ " VARCHAR(255), " + " " + constRedirectURI
				+ " VARCHAR(255), " + " " + constState + " VARCHAR(255), "
				+ " " + constNonce + " VARCHAR(255), " + " " + constClientID
				+ " VARCHAR(255), " + " " + constResponseType
				+ " VARCHAR(255), " + " " + constScope + " VARCHAR(255), "
				+ " PRIMARY KEY ( " + constState + " ))";
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

		String sql = "CREATE TABLE IF NOT EXISTS " + constRPLoggedInTable + " "
				+ "(" + constSessionID + " VARCHAR(255), " + " PRIMARY KEY ( "
				+ constSessionID + " ))";
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

}
