package eu.leads.processor.common.utils.storage;

import org.apache.commons.configuration.Configuration;

import java.io.InputStream;
import java.util.List;

/**
 * Created by vagvaz on 12/17/14.
 */
public interface LeadsStorageReader {
  boolean initializeReader(Configuration configuration);
  InputStream read(String uri);
  InputStream batchRead(List<String> uris);
  InputStream batcheReadMerge(List<String> uris);
}
