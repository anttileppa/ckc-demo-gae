package fi.foyt.ckc.gaedemo.domainmodel;

import com.google.appengine.api.datastore.Entity;

public class RevisionProperty extends AbstractObject {

	public RevisionProperty() {
		super("REVISION_PROPERTY");
	}

	public RevisionProperty(Revision revision) {
		super("REVISION_PROPERTY", revision.getKey());
	}
	
	public String getName() {
	  return name;
  }
	
	public void setName(String name) {
	  this.name = name;
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

		entity.setProperty("name", this.name);
		entity.setProperty("value", this.value);

		return entity;
	}

	@Override
	public void loadFromEntity(Entity entity) {
		if (entity.getKey() != null) {
			this.setKey(entity.getKey());
		}

		this.name = (String) entity.getProperty("name");
		this.value = (String) entity.getProperty("value");
	}

	private String name;
	private String value;
}