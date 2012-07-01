package fi.foyt.ckc.gaedemo.dao;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import fi.foyt.ckc.gaedemo.domainmodel.Document;

public class DocumentDAO extends GenericDAO<Document> {

	public DocumentDAO(DatastoreService datastoreService) {
		super(datastoreService, "DOCUMENT", true);
	}

	public Document create(String data) {
		Document document = new Document();
		document.setData(data);
		document.setRevisionNumber(0l);
		return (Document) persist(document);
	}

	public Document findById(Long id) {
		return findObjectByKey(KeyFactory.createKey(getKind(), id));
	}

	public Document findByKey(Key key) {
		return findObjectByKey(key);
	}

	public List<Document> list() {
		Query query = new Query(getKind());
		return getObjectList(query);
	}
	
	public void updateData(Document document, String data) {
		document.setData(data);
		persist(document);
	}

	public void updateRevisionNumber(Document document, Long revisionNumber) {
		document.setRevisionNumber(revisionNumber);
		persist(document);
	}
}
