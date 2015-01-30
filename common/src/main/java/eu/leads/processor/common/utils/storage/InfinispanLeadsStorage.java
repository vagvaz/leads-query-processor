package eu.leads.processor.common.utils.storage;

import org.apache.commons.configuration.Configuration;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by vagvaz on 12/17/14.
 */
public class InfinispanLeadsStorage implements LeadsStorage {

  @Override public boolean initialize(Configuration configuration) {
    return false;
  }

  @Override public boolean initializeReader(Configuration configuration) {
    return false;
  }

  @Override public InputStream read(String uri) {
    return null;
  }

  @Override public InputStream batchRead(List<String> uris) {
    return null;
  }

  @Override public InputStream batcheReadMerge(List<String> uris) {
    return null;
  }

  @Override public boolean initializeWriter(Configuration configuration) {
    return false;
  }

  @Override public boolean write(String uri, OutputStream stream) {
    return false;
    
  }

  @Override public boolean write(Map<String, OutputStream> stream) {
    return false;
  }

  @Override public boolean write(Map<String, OutputStream> streams, boolean merge) {
    return false;

  }
}
