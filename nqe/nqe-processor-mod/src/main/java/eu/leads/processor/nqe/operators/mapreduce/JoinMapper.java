package eu.leads.processor.nqe.operators.mapreduce;

import eu.leads.processor.core.LeadsMapper;

import eu.leads.processor.core.Tuple;
import eu.leads.processor.common.utils.InfinispanUtils;
import org.infinispan.distexec.mapreduce.Collector;
import org.vertx.java.core.json.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import static java.lang.System.getProperties;

/**
 * Created with IntelliJ IDEA.
 * User: vagvaz
 * Date: 11/7/13
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class JoinMapper extends LeadsMapper<String, String, String, String> {

    public JoinMapper(JsonObject configuration) {
        super(configuration);
    }

    public void initialize() {
        isInitialized = true;
        super.initialize();

    }

    @Override
    public void map(String key, String value, Collector<String, String> collector) {
//        if (!isInitialized)
//            initialize();
//        String tableName = key;
//        String columnName = (String) conf.getString(tableName);
//
//        Map<String, String> tuples = InfinispanUtils.getOrCreatePersistentMap(key);
//        String outkey = "";
//        for (Map.Entry<String, String> entry : tuples.entrySet()) {
//            progress();
//            Tuple t = new Tuple(entry.getValue());
//            handlePagerank(t);
//            outkey = t.getAttribute(columnName);
//            t.setAttribute("tupleId", entry.getKey().substring(entry.getKey().indexOf(":") + 1));
//            t.setAttribute("table", tableName);
//            collector.emit(outkey, t.asString());
//            t = null;
//            outkey = null;
//        }
    }




}
