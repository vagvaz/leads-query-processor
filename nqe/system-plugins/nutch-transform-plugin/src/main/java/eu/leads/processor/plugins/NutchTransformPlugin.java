package eu.leads.processor.plugins;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Tuple;
import org.apache.commons.configuration.Configuration;
import org.apache.nutch.storage.WebPage;
import org.apache.nutch.util.URLUtil;
import org.infinispan.Cache;
import org.infinispan.query.remote.client.avro.AvroMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by vagvaz on 11/10/14.
 */
public class NutchTransformPlugin implements PluginInterface {
  private final String id = NutchTransformPlugin.class.getCanonicalName();
  private Configuration conf;
  private transient Cache outputCache = null;
  private Logger log;
  private AvroMarshaller<WebPage> marshaller;
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
    log = LoggerFactory.getLogger(NutchTransformPlugin.class);
    String outputCacheName  = config.getString("outputCacheName","default.webpages");
    log.error("NutchTransform Plugin output = " + outputCacheName);
    marshaller = new AvroMarshaller<>(WebPage.class);
    log.error("Initialized Marshaller");
    outputCache = (Cache) manager.getPersisentCache(outputCacheName);
    log.error("Nutch Transform Plugin Initialized");
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
    log.error("Invoked for key " + key);

    log.error("");
    WebPage wp = null;
    try {

      wp = (WebPage) marshaller.objectFromByteBuffer((byte[]) value);
      log.error("unmarshall wp");
      Tuple tuple = new Tuple();
      tuple.setAttribute("url",wp.getKey());
      tuple.setAttribute("body",wp.getContent());
      tuple.setAttribute("headers",wp.getHeaders());
      tuple.setAttribute("responseTime",wp.getFetchInterval());
      tuple.setAttribute("responseCode",wp.getProtocolStatus().getCode());
      tuple.setAttribute("charset",wp.getContentType());
      tuple.setAttribute("links", Arrays.asList(wp.getOutlinks().values()));
      tuple.setAttribute("title",wp.getTitle());
      tuple.setAttribute("pagerank",-1.0);
      Date date = new Date(wp.getFetchTime());
      SimpleDateFormat df2 = new SimpleDateFormat();
      tuple.setAttribute("published",df2.format(date));
      tuple.setAttribute("sentiment",-1.0);
      tuple.setAttribute("domainName", URLUtil.getDomainName(wp.getKey()));
      log.error("Tuple Created " + tuple.toString());
       outputCache.put(outputCache.getName()+":"+tuple.getAttribute("url").toString(),tuple);
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
