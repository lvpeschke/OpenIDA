package openidProviderPackage;

import java.io.IOException;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import dbPackage.OPdbConnection;

/**
 * The class handling saving the user supplied picture
 * 
 */
public class OPSetUserPicture extends HttpServlet {

	private OPdbConnection dbConnection;

	/**
	 * Create a database connection
	 */
	@Override
	public void init() {
		dbConnection = OPdbConnection.getConnection();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		setUserPicture(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		setUserPicture(request, response);
	}

	/**
	 * The main method for saving the user supplied picture to the database,
	 * then updates the page by sending the user to the same webpage
	 * 
	 * @param request
	 *            - The object containing the request
	 * @param response
	 *            - The object handling the response back to the user
	 * @throws IOException
	 */
	private void setUserPicture(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		String userName = (String) session.getAttribute("OPUserName");

		try {
			List<FileItem> multiparts = new ServletFileUpload(
					new DiskFileItemFactory()).parseRequest(request);

			for (FileItem fileItem : multiparts) {
				dbConnection.savePicture(userName, fileItem.getInputStream());
			}
		} catch (FileUploadException e1) {
			e1.printStackTrace();
		}

		response.sendRedirect("/OPViews/loggedIn.html");
	}
}