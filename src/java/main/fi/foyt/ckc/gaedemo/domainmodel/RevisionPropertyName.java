package fi.foyt.ckc.gaedemo.domainmodel;

import com.google.appengine.api.datastore.Entity;

public class RevisionPropertyName extends AbstractObject {

	public RevisionPropertyName() {
		super("REVISION_PROPERTY_NAME");
	}

	public String getName() {
	  return name;
  }
	
	public void setName(String name) {
	  this.name = name;
  }

	@Override
	public Entity toEntity() {
		Entity entity = newEntity();

		entity.setProperty("name", this.name);

		return entity;
	}

	@Override
	public void loadFromEntity(Entity entity) {
		if (entity.getKey() != null) {
			this.setKey(entity.getKey());
		}

		this.name = (String) entity.getProperty("name");
	}

	private String name;
}