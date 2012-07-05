package fi.foyt.ckc.gaedemo.dao;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import fi.foyt.ckc.gaedemo.domainmodel.Revision;
import fi.foyt.ckc.gaedemo.domainmodel.RevisionProperty;
import fi.foyt.ckc.gaedemo.domainmodel.RevisionPropertyName;

public class RevisionPropertyDAO extends GenericDAO<RevisionProperty> {

	public RevisionPropertyDAO(DatastoreService datastoreService) {
		super(datastoreService, "REVISION_PROPERTY", true);
	}

	public RevisionProperty create(Revision revision, Long nameId, String value) {
		RevisionProperty revisionProperty = new RevisionProperty(revision);
		revisionProperty.setNameId(nameId);
		revisionProperty.setValue(value);
		return (RevisionProperty) persist(revisionProperty);
	}

	public RevisionProperty findByRevisionAndPropertyName(Revision revision, RevisionPropertyName propertyName) {
		Query.Filter filter = new Query.FilterPredicate("nameId", FilterOperator.EQUAL, propertyName.getKey().getId());
		Query query = new Query(getKind(), revision.getKey()).setFilter(filter);
		return getSingleObject(query);
  }
	
	public List<RevisionProperty> listByRevision(Revision revision) {
		Query query = new Query(getKind(), revision.getKey());
		return getObjectList(query);
	}

	public void updateValue(RevisionProperty revisionProperty, String value) {
		revisionProperty.setValue(value);
		persist(revisionProperty);
	}
	
}