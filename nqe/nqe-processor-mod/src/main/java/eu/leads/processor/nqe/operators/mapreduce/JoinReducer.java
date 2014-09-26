package eu.leads.processor.nqe.operators.mapreduce;

import eu.leads.processor.core.LeadsReducer;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.common.utils.InfinispanUtils;
import org.vertx.java.core.json.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: vagvaz
 * Date: 11/7/13
 * Time: 8:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class JoinReducer extends LeadsReducer<String, String> {


    String prefix;
   public JoinReducer(JsonObject configuration) {
      super(configuration);
   }
    @Override
    public void initialize() {
        isInitialized = true;
        super.initialize();
        prefix = conf.getString("output") + ":";

    }



    @Override
    public String reduce(String key, Iterator<String> iterator) {
        if (!isInitialized)
            initialize();
        ArrayList<Tuple> left = new ArrayList<Tuple>();
        ArrayList<Tuple> right = new ArrayList<Tuple>();

        String leftTable = conf.getString("left");
//        String rightTable = conf.getProperty("right");
        ArrayList<String> ignoreColumns = new ArrayList<String>();
        ignoreColumns.add("table");
        ignoreColumns.add("tupleId");
//        ignoreColumns.add((String) conf.getProperty(rightTable));
        while (iterator.hasNext()) {
            String tstring = iterator.next();
            Tuple t = new Tuple(tstring);
            if (t.getAttribute("table").equals(leftTable)) {
                left.add(t);
            } else {
                right.add(t);
            tstring = null;
            }
        }

        for (Tuple tl : left) {
            for (Tuple tr : right) {
                Tuple resultTuple = new Tuple(tl, tr, ignoreColumns);
//                System.out.println(this.getClass().toString()+" proc tuple");
                output.put(prefix + tr.getAttribute("tupleId") + "-" + tl.getAttribute("tupleId"), resultTuple.asString());
                progress();
                resultTuple = null;
            }
        }
        left.clear();
        right.clear();
        left = null;
        right = null;
        ignoreColumns.clear();
        ignoreColumns = null;
        return "";
    }

}
