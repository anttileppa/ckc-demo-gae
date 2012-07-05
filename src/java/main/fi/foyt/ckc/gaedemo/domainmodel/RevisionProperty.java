package fi.foyt.ckc.gaedemo.domainmodel;

import com.google.appengine.api.datastore.Entity;

public class RevisionProperty extends AbstractObject {

	public RevisionProperty() {
		super("REVISION_PROPERTY");
	}

	public RevisionProperty(Revision revision) {
		super("REVISION_PROPERTY", revision.getKey());
	}
	
	public Long getNameId() {
	  return nameId;
  }
	
	public void setNameId(Long nameId) {
	  this.nameId = nameId;
  }
	
	public String getValue() {
	  return value;
  }
	
	public void setValue(String value) {
	  this.value = value;
  }

	@Override
	public Entity toEntity() {
		Entity entity = newEntity();

		entity.setProperty("nameId", this.nameId);
		entity.setProperty("value", this.value);

		return entity;
	}

	@Override
	public void loadFromEntity(Entity entity) {
		if (entity.getKey() != null) {
			this.setKey(entity.getKey());
		}

		this.nameId = (Long) entity.getProperty("nameId");
		this.value = (String) entity.getProperty("value");
	}

	private Long nameId;
	private String value;
}