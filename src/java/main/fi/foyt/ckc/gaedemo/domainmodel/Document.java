package fi.foyt.ckc.gaedemo.domainmodel;

import java.io.UnsupportedEncodingException;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;

public class Document extends AbstractObject {
  
  public Document() {
    super("DOCUMENT");
  }

  public void setData(String data) {
	  this.data = data;
  }
  
  public String getData() {
	  return data;
  }
  
  public Long getRevisionNumber() {
	  return revisionNumber;
  }
  
  public void setRevisionNumber(Long revisionNumber) {
	  this.revisionNumber = revisionNumber;
  }
  
  @Override
  public Entity toEntity() {
    Entity entity = newEntity();
    
    if (data != null) {
	    try {
	      entity.setProperty("data", new com.google.appengine.api.datastore.Blob(data.getBytes("UTF-8")));
      } catch (UnsupportedEncodingException e) {
      }
    }
    
    entity.setProperty("revisionNumber", revisionNumber);
    
    return entity;
  }

  @Override
  public void loadFromEntity(Entity entity) {
    if (entity.getKey() != null) {
      this.setKey(entity.getKey());
    }
    
    com.google.appengine.api.datastore.Blob dataBlob = (Blob) entity.getProperty("data"); 
    try {
	    this.data = dataBlob != null ? new String(dataBlob.getBytes(), "UTF-8") : null;
    } catch (UnsupportedEncodingException e) {
	    this.data = null;
    }
    this.revisionNumber = (Long) entity.getProperty("revisionNumber"); 
  }

  private String data;
  private Long revisionNumber;
}