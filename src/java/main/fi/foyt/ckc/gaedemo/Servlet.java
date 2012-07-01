package fi.foyt.ckc.gaedemo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.appengine.api.datastore.DatastoreServiceFactory;

import fi.foyt.ckc.gaedemo.dao.DocumentDAO;
import fi.foyt.ckc.gaedemo.domainmodel.Document;

public class Servlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DocumentDAO documentDAO = new DocumentDAO(DatastoreServiceFactory.getDatastoreService());
		
		Long documentId = NumberUtils.createLong(request.getParameter("documentId"));
		if (documentId == null) {
			Document document = documentDAO.create(DEFAULT_CONTENT);
			response.sendRedirect(request.getRequestURI() + "?documentId=" + document.getKey().getId());
		} else {
			Document document = documentDAO.findById(documentId);
			request.setAttribute("document", document);
		  response.setContentType("text/html; charset=utf-8");
		  request.getRequestDispatcher("/index.jsp").include(request, response);
		}
	}
	
	private final static String DEFAULT_CONTENT = "<h3>Welcome to test CKEditor's collaboration plugin!</h3>";
}
