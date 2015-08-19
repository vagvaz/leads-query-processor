import java.util.*;

import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.plugins.NutchTransformer;
import org.apache.avro.generic.GenericData;
import org.apache.gora.filter.FilterOp;
import org.apache.gora.filter.MapFieldValueFilter;
import org.apache.gora.infinispan.query.InfinispanQuery;
import org.apache.gora.query.PartitionQuery;
import org.apache.gora.query.Query;
import org.apache.gora.query.Result;
import org.apache.gora.store.DataStore;
import org.apache.gora.util.GoraException;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.storage.Mark;
import org.apache.nutch.storage.StorageUtils;
import org.apache.nutch.storage.WebPage;
import org.apache.nutch.util.NutchConfiguration;
/**
 * Created by vagvaz on 01/08/15.
 */
public class GoraInputHandler implements InputHandler<String,WebPage> {

    Configuration configuration = NutchConfiguration.create();
    DataStore<String,WebPage> store;
    long offset;
    long batchSize;
    boolean useParallel;
    String connectionString;
    long limit;
    long numberOfValues = 0;
    long totalResults;
    NutchTransformer transformer;
    Result currentResult;
    List<Result> listOfResults;
    GenericData.Record record = new GenericData.Record(WebPage.SCHEMA$);
    MapFieldValueFilter filter = new MapFieldValueFilter();

    @Override public void initialize(Properties conf) {

        LQPConfiguration.initialize();

        if(conf.containsKey("connectionString")){
            connectionString = conf.getProperty("connectionString");
        }
        else{
            connectionString = "127.0.0.1:11222";
        }
        configuration.set("gora.datastore.connectionstring",connectionString);
        configuration.set("gora.datastore.default",
            "org.apache.gora.infinipan.store.InfinispanStoreer");
        try {
            store = StorageUtils.createStore(configuration,String.class,WebPage.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (GoraException e) {
            e.printStackTrace();
        }

        filter.setFieldName(WebPage.Field.MARKERS.getName());
        filter.setFilterOp(FilterOp.LIKE);
        filter.setFilterIfMissing(false);
        filter.setMapKey(Mark.FETCH_MARK.getName());
        filter.getOperands().add("*");

        store.createSchema();
        Query query = store.newQuery();
        query.setFields("key");
        query.setLimit(1);
        query.setFilter(filter);
        query.execute();
        totalResults = ((InfinispanQuery)query).getResultSize();
        System.out.println("Total amount of pages (in the store): " + totalResults);

        if(conf.containsKey("offset")){
            offset = Long.parseLong(conf.getProperty("offset"));
        }
        else {
            offset = 0;
        }

        if(conf.containsKey("limit")){
            limit = Long.parseLong(conf.getProperty("limit"));
        }
        else{
            limit = totalResults;
        }

        if(conf.containsKey("batchSize")){
            batchSize = Long.parseLong(conf.getProperty("batchSize"));
        }else{
            batchSize = 10000;
        }
        if(conf.containsKey("useParallel")){
            useParallel = Boolean.parseBoolean(conf.getProperty("useParallel"));
        }
        else{
            useParallel = true;
        }
        readNextBatch();
    }

    @Override public Map getAll() {
        Map result = new HashMap();
        currentResult = issueQuery(offset,limit);
        if(result != null) {
            result = readTuplesFromResult(currentResult);
        }
        return result;
    }

    private Map readTuplesFromResult(Result result) {
        Map tuples = new HashMap();
        try {
            while(result.next()){
                WebPage page = (WebPage) result.get();
                tuples.put(page.getKey(),page);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tuples;
    }

    private Tuple getTupleFromPage(GenericData.Record record, WebPage page) {

        for(int i = 0; i < WebPage.SCHEMA$.getFields().size();i++){
            record.put(i,page.get(i));
        }
        Tuple result = transformer.transform(record);
        return result;
    }

    @Override public Map getNextBatch(long offset)
    {
//       currentResult = issueQuery(offset,batchSize);
        currentResult = readNextBatch();
        Map result = readTuplesFromResult(currentResult);
        return result;
    }

    private Result readNextBatch() {
        if(offset + batchSize < totalResults) {
            currentResult = issueQuery(offset, batchSize);
        }
        else{
            currentResult = issueQuery(offset, totalResults - offset);
        }
        return currentResult;
    }

    private Result issueQuery(long offset, long batchSize) {
        if(numberOfValues >= limit)
        {
            currentResult = null;
            return null;
        }
        if(batchSize <= 0 ){
            currentResult = null;
            return null;
        }

        Query query = store.newQuery();
        query.setOffset((int) offset);
        query.setLimit(batchSize);
        query.setSortingOrder(true);
        query.setSortingField("fetchTime");
        List<PartitionQuery> queries = ((InfinispanQuery)query).split();
        listOfResults = new LinkedList<>();
        for(PartitionQuery partitionQuery : queries) {
            listOfResults.add(partitionQuery.execute());
        }
        currentResult = listOfResults.remove(0);
        this.offset += batchSize;
        return currentResult;
    }

    @Override public void setBatchSize(long batchSize) {
        this.batchSize = batchSize;
    }

    @Override public long getBatchSize() {
        return batchSize;
    }

    @Override public void close() {
        store.close();
    }

    @Override public boolean hasNext() {
        boolean result = false;
        if(numberOfValues < limit){
            if(totalResults > offset){
                result = true;
            }
        }
        else{
            result = false;
        }
        return result;
    }

    @Override public Map.Entry<String, WebPage> next()
    {

        if(currentResult != null){
            try {
                if(currentResult.next())
                {
                    WebPage page = (WebPage) currentResult.get();
                        numberOfValues++;
                        return new AbstractMap.SimpleEntry<String, WebPage>(page.getUrl(), page);
                }
                else if(listOfResults.size() > 0){
                    currentResult = listOfResults.remove(0);
                    return next();
                }
                else{
                    if(hasNext()){
                        readNextBatch();
                        return next();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            if(hasNext()){
                readNextBatch();
                return next();
            }
        }
        return null;
    }



    @Override public void remove() {
        throw new UnsupportedOperationException("Unsupported remove");
    }
}
