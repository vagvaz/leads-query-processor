package eu.leads.processor.common.utils.storage;

import org.apache.commons.configuration.Configuration;

/**
 * Created by vagvaz on 12/17/14.
 */
public interface LeadsStorage extends LeadsStorageWriter,LeadsStorageReader {
  boolean initialize(Configuration configuration);
}
