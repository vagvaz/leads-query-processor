package eu.leads.processor.core.index;

/**
 * Created by angelos on 13/02/15.
 */
public interface LeadsIndex {
  public void setCacheName(String cacheName);

  public void setKeyName(String keyName);

  public void setAttributeName(String attributeName);

  public void setAttributeValue(Object attributeValue);

  public String getCacheName();

  public String getKeyName();

  public String getAttributeName();

  public Object getAttributeValue();

  public boolean equals(LeadsIndex anObject);

  public int hashCode();
}
