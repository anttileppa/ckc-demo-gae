package fi.foyt.ckc.gaedemo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import fi.foyt.ckc.LoadResult;
import fi.foyt.ckc.SaveResult;
import fi.foyt.ckc.Status;
import fi.foyt.ckc.UpdateResult;
import fi.foyt.ckc.gaedemo.dao.DocumentDAO;
import fi.foyt.ckc.gaedemo.dao.RevisionDAO;
import fi.foyt.ckc.gaedemo.dao.RevisionPropertyDAO;
import fi.foyt.ckc.gaedemo.dao.RevisionPropertyNameDAO;
import fi.foyt.ckc.gaedemo.domainmodel.Document;
import fi.foyt.ckc.gaedemo.domainmodel.Revision;
import fi.foyt.ckc.gaedemo.domainmodel.RevisionProperty;
import fi.foyt.ckc.gaedemo.domainmodel.RevisionPropertyName;
import fi.foyt.ckc.gaedemo.utils.DiffUtils;
import fi.foyt.ckc.gaedemo.utils.PatchResult;
import fi.foyt.ckc.utils.CKCUtils;

public class DemoConnector implements CKCConnector {
	
	private static final String TOKEN_SESSION_ATTR = "__token__";
	
	private static String[] propertyNames = {
		"title",
		"langCode",
		"keywords",
		"langDir",
		"textColor",
		"backgroundColor",
		"backgroundImage",
		"backgroundAttachment",
		"pageMarginLeft",     
		"pageMarginTop",     
		"pageMarginRight",    
		"pageMarginBottom",   
		"metaDescription"
  };

	@Override
  public InitResult init(HttpServletRequest request, String documentId) throws CKCConnectorException {
	  DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

		RevisionPropertyNameDAO revisionPropertyNameDAO = new RevisionPropertyNameDAO(datastoreService);

		for (String propertyName : propertyNames) {
			if (revisionPropertyNameDAO.findByName(propertyName) == null) {
				Transaction transaction = datastoreService.beginTransaction();
  			revisionPropertyNameDAO.create(propertyName);
  			transaction.commit();
			}
	  }

	  String token = UUID.randomUUID().toString();
	  
	  HttpSession session = request.getSession(true);
	  session.setAttribute(TOKEN_SESSION_ATTR, token);
		
	  return new InitResult(Status.OK, token);
  }

	@Override
  public CreateResult create(HttpServletRequest request, String content) {
		// Document is created elsewhere in this example
 	  return null;
  }

	@Override
  public LoadResult load(HttpServletRequest request, String documentId) throws CKCConnectorException {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Transaction transaction = datastoreService.beginTransaction();
		try {
			DocumentDAO documentDAO = new DocumentDAO(datastoreService);
  		RevisionDAO revisionDAO = new RevisionDAO(datastoreService);
  		RevisionPropertyNameDAO revisionPropertyNameDAO = new RevisionPropertyNameDAO(datastoreService);
  		RevisionPropertyDAO revisionPropertyDAO = new RevisionPropertyDAO(datastoreService);
		
  		Long id = NumberUtils.createLong(documentId);
  		Document document = documentDAO.findById(id);
			
			Status status = Status.OK;
			Map<String, String> properties = new HashMap<String, String>();
			
			List<Revision> documentRevisions = revisionDAO.listByDocument(document);
			Collections.sort(documentRevisions, new Comparator<Revision>() {
  			@Override
  			public int compare(Revision documentRevision1, Revision documentRevision2) {
  			  return documentRevision2.getNumber().compareTo(documentRevision1.getNumber());
  			}
  		});
			
			List<RevisionPropertyName> propertyNames = revisionPropertyNameDAO.listAll();
			for (RevisionPropertyName propertyName : propertyNames) {
				for (Revision documentRevision : documentRevisions) {
					RevisionProperty revisionProperty = revisionPropertyDAO.findByRevisionAndPropertyName(documentRevision, propertyName);
					if (revisionProperty != null) {
						properties.put(propertyName.getName(), revisionProperty.getValue());
						break;
					}
				}
			}
			
  		return new LoadResult(status, document.getRevisionNumber(), document.getData(), properties);
		} catch (Exception e) {
			e.printStackTrace();
			
			transaction.rollback();
  		return new LoadResult(Status.UNKNOWN_ERROR, null, null, null);
		} finally {
			transaction.commit();
		}
  }

	@Override
  public UpdateResult update(HttpServletRequest request, String documentId, Long revisionNumber) throws CKCConnectorException {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Transaction transaction = datastoreService.beginTransaction();
		try {
  		DocumentDAO documentDAO = new DocumentDAO(datastoreService);
  		RevisionDAO revisionDAO = new RevisionDAO(datastoreService);
  		RevisionPropertyNameDAO revisionPropertyNameDAO = new RevisionPropertyNameDAO(datastoreService);
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
  				RevisionPropertyName revisionPropertyName = revisionPropertyNameDAO.findById(revisionProperty.getNameId());
  				revision.addProperty(revisionPropertyName.getName(), revisionProperty.getValue());
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
  public SaveResult save(HttpServletRequest request, String documentId, String patch, String properties) throws CKCConnectorException {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Transaction transaction = datastoreService.beginTransaction();
		try {
  		DocumentDAO documentDAO = new DocumentDAO(datastoreService);
  		RevisionDAO revisionDAO = new RevisionDAO(datastoreService);
  		RevisionPropertyNameDAO revisionPropertyNameDAO = new RevisionPropertyNameDAO(datastoreService);
  		RevisionPropertyDAO revisionPropertyDAO = new RevisionPropertyDAO(datastoreService);
  		
  		Status status = Status.OK;
  		Long revisionNumber = null;
  		Document document = documentDAO.findById(NumberUtils.createLong(documentId));
  		
  
  		String revisionData = null;
  	  revisionNumber = document.getRevisionNumber() + 1;
  		
      if (StringUtils.isNotBlank(patch)) {
    		String oldData = document.getData();
    	  PatchResult patchResult = DiffUtils.applyPatch(oldData, patch);
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
      		RevisionPropertyName propertyName = revisionPropertyNameDAO.findByName(key);
      		if (propertyName == null) {
      		  propertyName = revisionPropertyNameDAO.create(key);	
      		}
      		
      		revisionPropertyDAO.create(documentRevision, propertyName.getKey().getId(), value);
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
