package eu.leads.processor.infinispan;

import java.io.Serializable;

/**
 * Created by vagvaz on 3/6/15.
 */
public class ComplexIntermediateKey implements Comparable, Serializable {
   private String site;
   private String node;
   private String key;
   private Integer counter;

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

   public Integer getCounter() {
      return counter;
   }

   public void setCounter(Integer counter) {
      this.counter = counter;
   }

   @Override
   public int hashCode() {
      return key.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ComplexIntermediateKey that = (ComplexIntermediateKey) o;

      if (site != null ? !site.equals(that.site) : that.site != null) return false;
      if (node != null ? !node.equals(that.node) : that.node != null) return false;
      if (key != null ? !key.equals(that.key) : that.key != null) return false;
      return !(counter != null ? !counter.equals(that.counter) : that.counter != null);

   }

   @Override
   public int compareTo(Object o) {
      if (this == o) return 0;
      if (o == null || getClass() != o.getClass()) return -1;

      ComplexIntermediateKey that = (ComplexIntermediateKey) o;
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
         result = key.compareTo(that.key);
         if(result != 0)
            return result;
      }
      if(counter != null){
         return counter.compareTo(that.counter);
      }
      return -1;
   }
}
