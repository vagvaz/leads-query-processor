package eu.leads.processor.infinispan;

import java.io.Serializable;

/**
 * Created by vagvaz on 3/6/15.
 */
public class ComplexIntermediateKey implements Comparable, Serializable {
  String site;
  String node;
  String key;
  Integer counter;

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    ComplexIntermediateKey that = (ComplexIntermediateKey) o;

    if (site != null ? !site.equals(that.site) : that.site != null)
      return false;
    if (node != null ? !node.equals(that.node) : that.node != null)
      return false;
    if (key != null ? !key.equals(that.key) : that.key != null)
      return false;
    return !(counter != null ? !counter.equals(that.counter) : that.counter != null);

  }

  @Override public int hashCode() {
    return key != null ? key.hashCode() : 0;
  }

  @Override public int compareTo(Object o) {
    if (this == o)
      return 0;
    if (o == null || getClass() != o.getClass())
      return -1;

    ComplexIntermediateKey that = (ComplexIntermediateKey) o;
    int result = 0;
    if (site != null)
    {
      result = site.compareTo(that.site);
      if(result != 0)
        return result;
    }
    if (node != null)
    {
     result = node.compar
    }
      return false;
    if (key != null ? !key.equals(that.key) : that.key != null)
      return false;
    return !(counter != null ? !counter.equals(that.counter) : that.counter != null);
  }
}
