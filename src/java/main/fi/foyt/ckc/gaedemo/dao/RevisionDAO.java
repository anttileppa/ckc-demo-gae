package fi.foyt.ckc.gaedemo.dao;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import fi.foyt.ckc.gaedemo.domainmodel.Document;
import fi.foyt.ckc.gaedemo.domainmodel.Revision;

public class RevisionDAO extends GenericDAO<Revision> {

	public RevisionDAO(DatastoreService datastoreService) {
		super(datastoreService, "REVISION", true);
	}

	public Revision create(Document document, Long number, String patch) {
		Revision revision = new Revision(document);
		revision.setNumber(number);
		revision.setPatch(patch);
		return (Revision) persist(revision);
	}

	public List<Revision> listByDocument(Document document) {
		Query query = new Query(getKind(), document.getKey());
		return getObjectList(query);
	}

	public List<Revision> listByDocumentAndRevisionGreaterThan(Document document, Long revision) {
		Query.Filter filter = new Query.FilterPredicate("number", FilterOperator.GREATER_THAN, revision);
		Query query = new Query(getKind(), document.getKey()).setFilter(filter);
		return getObjectList(query);
	}
	
	public void updateNumber(Revision revision, Long number) {
		revision.setNumber(number);
		persist(revision);
	}
	
}