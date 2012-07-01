package fi.foyt.ckc.gaedemo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;

import fi.foyt.ckc.CKCConnector;
import fi.foyt.ckc.CKCConnectorException;
import fi.foyt.ckc.CreateResult;
import fi.foyt.ckc.InitResult;
import fi.foyt.ckc.SaveResult;
import fi.foyt.ckc.Status;
import fi.foyt.ckc.UpdateResult;
import fi.foyt.ckc.gaedemo.dao.DocumentDAO;
import fi.foyt.ckc.gaedemo.dao.RevisionDAO;
import fi.foyt.ckc.gaedemo.dao.RevisionPropertyDAO;
import fi.foyt.ckc.gaedemo.domainmodel.Document;
import fi.foyt.ckc.gaedemo.domainmodel.Revision;
import fi.foyt.ckc.gaedemo.domainmodel.RevisionProperty;
import fi.foyt.ckc.gaedemo.utils.DiffUtils;
import fi.foyt.ckc.gaedemo.utils.PatchResult;
import fi.foyt.ckc.utils.CKCUtils;

public class DemoConnector implements CKCConnector {
	
	private static final String TOKEN_SESSION_ATTR = "__token__";

	@Override
  public InitResult init(HttpServletRequest request, String documentId) throws CKCConnectorException {
	  String token = UUID.randomUUID().toString();

	  HttpSession session = request.getSession(true);
	  session.setAttribute(TOKEN_SESSION_ATTR, token);
		
	  return new InitResult(Status.OK, token);
  }

	@Override
  public CreateResult create(HttpServletRequest request, String parentId, String title, String content) {
 	  return null;
  }

	@Override
  public UpdateResult update(HttpServletRequest request, String documentId, Long revisionNumber) throws CKCConnectorException {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Transaction transaction = datastoreService.beginTransaction();
		try {
  		DocumentDAO documentDAO = new DocumentDAO(datastoreService);
  		RevisionDAO revisionDAO = new RevisionDAO(datastoreService);
  		RevisionPropertyDAO revisionPropertyDAO = new RevisionPropertyDAO(datastoreService);
  		
  		Status status = Status.OK;
  		List<fi.foyt.ckc.Revision> revisions = new ArrayList<fi.foyt.ckc.Revision>();
  		
  		Long id = NumberUtils.createLong(documentId);
  		
  		Document document = documentDAO.findById(id);
  		
  		List<Revision> documentRevisions = revisionDAO.listByDocumentAndRevisionGreaterThan(document, revisionNumber);
  		Collections.sort(documentRevisions, new Comparator<Revision>() {
  			@Override
  			public int compare(Revision documentRevision1, Revision documentRevision2) {
  			  return documentRevision1.getNumber().compareTo(documentRevision2.getNumber());
  			}
  		});
  		
  		for (Revision documentRevision : documentRevisions) {
  			String patchData = documentRevision.getPatch();
  
  			fi.foyt.ckc.Revision revision = new fi.foyt.ckc.Revision(documentRevision.getNumber(), patchData);
  			List<RevisionProperty> revisionProperties = revisionPropertyDAO.listByRevision(documentRevision);
  			for (RevisionProperty revisionProperty : revisionProperties) {
  				revision.addProperty(revisionProperty.getName(), revisionProperty.getValue());
  			}
  			
        revisions.add(revision);
  		}
  
  		return new UpdateResult(status, revisions);
		} catch (Exception e) {
			e.printStackTrace();
			
			transaction.rollback();
  		return new UpdateResult(Status.UNKNOWN_ERROR, null);
		} finally {
			transaction.commit();
		}
  }

	@Override
  public SaveResult save(HttpServletRequest request, String documentId, String patch) throws CKCConnectorException {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Transaction transaction = datastoreService.beginTransaction();
		try {
  		DocumentDAO documentDAO = new DocumentDAO(datastoreService);
  		RevisionDAO revisionDAO = new RevisionDAO(datastoreService);
  		RevisionPropertyDAO revisionPropertyDAO = new RevisionPropertyDAO(datastoreService);
  		
  		Status status = Status.OK;
  		Long revisionNumber = null;
  		Document document = documentDAO.findById(NumberUtils.createLong(documentId));
  		
  
  	  String patchData = request.getParameter("patch");
      String properties = request.getParameter("properties");
  		String revisionData = null;
  	  revisionNumber = document.getRevisionNumber() + 1;
  		
      if (StringUtils.isNotBlank(patchData)) {
    		String oldData = document.getData();
    	  PatchResult patchResult = DiffUtils.applyPatch(oldData, patchData);
    	  if (!patchResult.allApplied()) {
    	  	status = Status.CONFLICT;
    	  } else {
      	  String data = patchResult.getPatchedText();
      	  
      		if (!StringUtils.isEmpty(oldData) && !data.equals(oldData)) {
        		revisionData = DiffUtils.makePatch(oldData, data);
      		}
      
  	      documentDAO.updateData(document, data);
        }
      }
      
      Revision documentRevision = revisionDAO.create(document, revisionNumber, revisionData);
  
      if (StringUtils.isNotBlank(properties)) {
      	Map<String, String> changedProperties = CKCUtils.parseProperties(properties);
      	Iterator<String> keyIterator = changedProperties.keySet().iterator();
      	while (keyIterator.hasNext()) {
      		String key = keyIterator.next();
      		String value = changedProperties.get(key);
      		revisionPropertyDAO.create(documentRevision, key, value);
      	}
      }
      
      documentDAO.updateRevisionNumber(document, revisionNumber);
  
  		return new SaveResult(status, revisionNumber);
		} catch (Exception e) {
			e.printStackTrace();
			
			transaction.rollback();
  		return new SaveResult(Status.UNKNOWN_ERROR, null);
		} finally {
			transaction.commit();
		}
  }

	@Override
  public boolean validateToken(HttpServletRequest request, String token) throws CKCConnectorException {
		HttpSession session = request.getSession(true);
	  Object sessionToken = session.getAttribute(TOKEN_SESSION_ATTR);
	  return token.equals(sessionToken);
  }


}
