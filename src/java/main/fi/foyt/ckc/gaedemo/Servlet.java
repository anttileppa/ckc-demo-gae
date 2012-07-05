package fi.foyt.ckc.gaedemo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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
			String baseUrl = request.getRequestURL().toString();
			
			ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
			URL url = new URL(baseUrl + "/testdata.html");
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			InputStream inputStream = connection.getInputStream();
			byte[] buf = new byte[256];
			int l = 0;
			while ((l = inputStream.read(buf, 0, 256)) > 0) {
				contentStream.write(buf, 0, l);
			}
			
			Document document = documentDAO.create(new String(contentStream.toByteArray(), "UTF-8"));
			response.sendRedirect(baseUrl + "?documentId=" + document.getKey().getId());
		} else {
			Document document = documentDAO.findById(documentId);
			request.setAttribute("document", document);
		  response.setContentType("text/html; charset=utf-8");
		  request.getRequestDispatcher("/index.jsp").include(request, response);
		}
	}
}
