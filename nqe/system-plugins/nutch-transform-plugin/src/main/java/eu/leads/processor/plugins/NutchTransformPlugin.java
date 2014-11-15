package eu.leads.processor.plugins;

import eu.leads.processor.common.infinispan.InfinispanManager;
import org.apache.commons.configuration.Configuration;
import org.apache.nutch.storage.WebPage;
import org.infinispan.Cache;
import org.infinispan.query.remote.client.avro.AvroMarshaller;

import java.io.IOException;

/**
 * Created by vagvaz on 11/10/14.
 */
public class NutchTransformPlugin implements PluginInterface {
  private final String id = NutchTransformPlugin.class.getCanonicalName();
  private Configuration conf;
  @Override public String getId() {
    return id;
  }

  @Override public void setId(String id) {
    ;
  }

  @Override public String getClassName() {
    return NutchTransformPlugin.class.getCanonicalName();
  }

  @Override public void initialize(Configuration config, InfinispanManager manager) {

  }

  @Override public void cleanup() {
    ;
  }

  @Override public void modified(Object key, Object value, Cache<Object, Object> cache) {
    processPage(key,value);
  }

  @Override public void created(Object key, Object value, Cache<Object, Object> cache) {
    processPage(key,value);
  }

  private void processPage(Object key, Object value) {
    AvroMarshaller<WebPage> marshaller = new AvroMarshaller<>(WebPage.class);
    WebPage wp = null;
    try {
      wp = (WebPage) marshaller.objectFromByteBuffer((byte[]) value);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  @Override public void removed(Object key, Object value, Cache<Object, Object> cache) {
      ;
  }

  @Override public Configuration getConfiguration() {
    return conf;
  }

  @Override public void setConfiguration(Configuration config) {
    this.conf = config;
  }
}
