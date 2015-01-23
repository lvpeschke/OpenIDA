package openidProviderPackage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dbPackage.OPdbConnection;

/**
 * A class which gets the user stored picture if such a one exists, and returns
 * it to the client. If no such picture exists it sends a default picture
 * 
 */
public class OPGetUserPicHandler extends HttpServlet {

	private OPdbConnection dbConnection;

	/**
	 * Creates a connection to the database
	 */
	@Override
	public void init() {
		dbConnection = OPdbConnection.getConnection();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		generateResponse(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		generateResponse(request, response);
	}

	/**
	 * Main method which sends back a picture
	 * 
	 * @param request
	 *            - The object containing the request
	 * @param response
	 *            - The object handling the response back to the user
	 * @throws IOException
	 */
	private void generateResponse(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		BufferedImage bi = null;

		HttpSession session = request.getSession();
		String userName = (String) session.getAttribute("OPUserName");
		if (userName == null) {
			response.sendRedirect("/OPViews/logIn.html");
		} else {
			bi = getUsersPicture(userName);
			if (bi == null) {
				// Return default picture
				File f = new File("WEB-INF/img/OPdefault.jpg");
				bi = ImageIO.read(f);
			}
			ImageIO.write(bi, "jpg", out);
			out.close();
		}
	}

	/**
	 * Returns the user saved picture if such a picture exists, else null
	 * 
	 * @param userName
	 * @return
	 */
	private BufferedImage getUsersPicture(String userName) {
		Blob blob = dbConnection.loadUserPicture(userName);
		BufferedImage img = null;
		if (blob == null) {
			return null;
		}
		try {
			img = ImageIO.read(blob.getBinaryStream());
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			img = null;
		}
		return img;
	}

}