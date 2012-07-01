package fi.foyt.ckc.gaedemo.domainmodel;

import com.google.appengine.api.datastore.Entity;

public class Revision extends AbstractObject {

	public Revision() {
		super("REVISION");
	}

	public Revision(Document document) {
		super("REVISION", document.getKey());
	}
	
	public Long getNumber() {
	  return number;
  }
	
	public void setNumber(Long number) {
	  this.number = number;
  }

	public String getPatch() {
	  return patch;
  }
	
	public void setPatch(String patch) {
	  this.patch = patch;
  }

	@Override
	public Entity toEntity() {
		Entity entity = newEntity();
		
		entity.setProperty("number", number);
		entity.setProperty("patch", patch);
		
		return entity;
	}

	@Override
	public void loadFromEntity(Entity entity) {
		if (entity.getKey() != null) {
			this.setKey(entity.getKey());
		}
		
		this.number = (Long) entity.getProperty("number");
		this.patch = (String) entity.getProperty("patch");
	}
	
	private Long number;
	private String patch;
}