package eu.leads.processor.nqe.handlers;

import eu.leads.processor.common.infinispan.InfinispanManager;
import eu.leads.processor.core.Action;
import eu.leads.processor.core.ActionHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.nqe.operators.*;
import org.infinispan.Cache;

import java.util.List;

/**
 * Created by vagvaz on 8/6/14.
 */
public class OperatorActionHandler implements ActionHandler {
    private final Node com;
    private final LogProxy log;
    private final InfinispanManager persistence;
    private final String id;


    private String textFile;
    private transient Cache<?, ?> InCache;
    private transient Cache<?, List<?>> CollectorCache;
    private transient Cache<?, ?> OutCache;

    public OperatorActionHandler(Node com, LogProxy log, InfinispanManager persistence, String id) {
        this.com = com;
        this.log = log;
        this.persistence = persistence;
        this.id = id;
    }

    @Override
    public Action process(Action action) {
        Action result = action;
       result.getData().putString("owner",id);
       com.sendTo(action.getData().getString("monitor"),result.asJsonObject());
       Operator operator = OperatorFactory.createOperator(com,persistence,result);
       operator.init(result.getData());
       operator.execute();

       return result;
    }
}


