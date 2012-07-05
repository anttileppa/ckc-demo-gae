package fi.foyt.ckc.gaedemo.dao;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import fi.foyt.ckc.gaedemo.domainmodel.RevisionPropertyName;

public class RevisionPropertyNameDAO extends GenericDAO<RevisionPropertyName> {

	public RevisionPropertyNameDAO(DatastoreService datastoreService) {
		super(datastoreService, "REVISION_PROPERTY_NAME", true);
	}

	public RevisionPropertyName create(String name) {
		RevisionPropertyName revisionPropertyName = new RevisionPropertyName();
		revisionPropertyName.setName(name);
		return (RevisionPropertyName) persist(revisionPropertyName);
	}

	public RevisionPropertyName findById(Long id) {
		return findObjectByKey(KeyFactory.createKey(getKind(), id));
  }

	public RevisionPropertyName findByName(String name) {
		Query.Filter filter = new Query.FilterPredicate("name", FilterOperator.EQUAL, name);
		Query query = new Query(getKind()).setFilter(filter);
		return getSingleObject(query);
	}
	
	public List<RevisionPropertyName> listAll() {
		Query query = new Query(getKind());

		return getObjectList(query);
	}
}