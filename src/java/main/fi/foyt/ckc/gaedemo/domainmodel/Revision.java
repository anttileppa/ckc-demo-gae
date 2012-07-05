package fi.foyt.ckc.gaedemo.domainmodel;

import java.io.UnsupportedEncodingException;

import com.google.appengine.api.datastore.Blob;
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
		if (this.patch != null) {
      try {
	      entity.setProperty("patch", new com.google.appengine.api.datastore.Blob(patch.getBytes("UTF-8")));
      } catch (UnsupportedEncodingException e) {
      }
		}
		
		return entity;
	}

	@Override
	public void loadFromEntity(Entity entity) {
		if (entity.getKey() != null) {
			this.setKey(entity.getKey());
		}
		
		this.number = (Long) entity.getProperty("number");
		com.google.appengine.api.datastore.Blob dataBlob = (Blob) entity.getProperty("patch"); 
    try {
	    this.patch = dataBlob != null ? new String(dataBlob.getBytes(), "UTF-8") : null;
    } catch (UnsupportedEncodingException e) {
	    this.patch = null;
    }
	}
	
	private Long number;
	private String patch;
}