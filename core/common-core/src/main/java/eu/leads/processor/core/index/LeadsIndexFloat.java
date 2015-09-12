package eu.leads.processor.core.index;

import org.hibernate.search.annotations.*;

import java.io.Serializable;

/**
 * Created by angelos on 11/02/15.
 */
@Indexed
public class LeadsIndexFloat implements Serializable, LeadsIndex {
//    @Field(index= Index.NO, analyze= Analyze.NO, store= Store.YES)
//    private String cacheName;
//    @Field(index= Index.YES, analyze= Analyze.NO, store= Store.YES)
   // private String  attributeName;
    @Field(index= Index.YES, analyze= Analyze.NO, store= Store.YES)
    private Float attributeValue;
    @Field(index= Index.NO, analyze= Analyze.NO, store= Store.YES)
    private String keyName;

    public LeadsIndexFloat(){    }

    public void setCacheName(String cacheName) {
        ;//this.cacheName = cacheName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setAttributeName(String attributeName) {
        ;//this.attributeName = attributeName;
    }

    public void setAttributeValue(Object attributeValue) {
        this.attributeValue = (Float) attributeValue;
    }

    public String getCacheName() {
        return ""; //return cacheName;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getAttributeName() {
        return ""; //return attributeName;
    }

    public Object getAttributeValue() {
        return attributeValue;
    }
    @Override
    public boolean equals(LeadsIndex anObject) {
        return keyName.equals(anObject.getKeyName());
    }

    @Override
    public int hashCode() {
        return keyName.hashCode();
    }
}
