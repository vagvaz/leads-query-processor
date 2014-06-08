package eu.leads.processor.common;

import eu.leads.processor.common.infinispan.InfinispanManager;

import java.io.Serializable;

/**
 * Created by vagvaz on 6/7/14.
 */
public interface LeadsListener extends Serializable {


    public InfinispanManager getManager();

    public void setManager(InfinispanManager manager);

    public void initialize(InfinispanManager manager);

    public String getId();


}
