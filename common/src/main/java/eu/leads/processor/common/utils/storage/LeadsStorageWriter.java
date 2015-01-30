package eu.leads.processor.common.utils.storage;

import org.apache.commons.configuration.Configuration;

import java.io.OutputStream;
import java.util.Map;


/**
 * Created by vagvaz on 12/17/14.
 */
public interface LeadsStorageWriter {
  boolean initializeWriter(Configuration configuration);
  boolean write(String uri, OutputStream stream);
  boolean write(Map<String,OutputStream> stream);
  boolean write(Map<String,OutputStream> streams, boolean merge);
}
