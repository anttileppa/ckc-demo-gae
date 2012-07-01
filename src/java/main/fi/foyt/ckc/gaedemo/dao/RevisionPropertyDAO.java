package fi.foyt.ckc.gaedemo.dao;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Query;

import fi.foyt.ckc.gaedemo.domainmodel.Revision;
import fi.foyt.ckc.gaedemo.domainmodel.RevisionProperty;

public class RevisionPropertyDAO extends GenericDAO<RevisionProperty> {

	public RevisionPropertyDAO(DatastoreService datastoreService) {
		super(datastoreService, "REVISION_PROPERTY", true);
	}

	public RevisionProperty create(Revision revision, String name, String value) {
		RevisionProperty revisionProperty = new RevisionProperty(revision);
		revisionProperty.setName(name);
		revisionProperty.setValue(value);
		return (RevisionProperty) persist(revisionProperty);
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