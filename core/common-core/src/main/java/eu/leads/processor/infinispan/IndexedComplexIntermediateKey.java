package eu.leads.processor.infinispan;

import org.hibernate.search.annotations.*;

import java.io.Serializable;

/**
 * Created by vagvaz on 3/7/15.
 */
@Indexed
public class IndexedComplexIntermediateKey implements Comparable,Serializable {
   @Field(index= Index.YES, analyze= Analyze.NO, store= Store.YES)
   private String site;

   @Field(index= Index.YES, analyze= Analyze.NO, store= Store.YES)
   private String node;
   @Field(index= Index.YES, analyze= Analyze.NO, store= Store.YES)
   private String key;

   public IndexedComplexIntermediateKey(String site, String node) {
      this.site = site;
      this.node = node;
   }

   public IndexedComplexIntermediateKey(String site, String node,String key) {
      this.site = site;
      this.node = node;
      this.key = key;
   }

  public IndexedComplexIntermediateKey() {

  }


  public String getSite() {
      return site;
   }

   public void setSite(String site) {
      this.site = site;
   }

   public String getNode() {
      return node;
   }

   public void setNode(String node) {
      this.node = node;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      IndexedComplexIntermediateKey that = (IndexedComplexIntermediateKey) o;

      if (site != null ? !site.equals(that.site) : that.site != null) return false;
      if (node != null ? !node.equals(that.node) : that.node != null) return false;
      return !(key != null ? !key.equals(that.key) : that.key != null);

   }

   @Override
   public int hashCode() {
      return key.hashCode();
   }

   @Override
   public int compareTo(Object o) {
      if (this == o) return 0;
      if (o == null || getClass() != o.getClass()) return -1;

      IndexedComplexIntermediateKey that = (IndexedComplexIntermediateKey) o;
      int result = 0;
      if (site != null){
         result = site.compareTo(that.site);
         if(result != 0)
            return result;
      }

      if (node != null){
         result = node.compareTo(that.node);
         if(result != 0)
            return result;
      }
      if (key != null )
      {
         return  key.compareTo(that.key);
      }
      return -1;
   }
   String getUniqueKey(){
      return site+node+key;
   }

  @Override public String toString() {
    return "IndexedComplexIntermediateKey{" +
             "site='" + site + '\'' +
             ", node='" + node + '\'' +
             ", key='" + key + '\'' +
             '}';
  }
}
