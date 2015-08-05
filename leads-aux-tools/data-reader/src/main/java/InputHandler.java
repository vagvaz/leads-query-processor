import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created by vagvaz on 01/08/15.
 */
public interface InputHandler<K,V> extends Iterator<Map.Entry<K,V>> {

    /**
     *  InputHandler is responsible for reading webpages from disk or remote enseble or other caches
     * @param conf Configuration for the inputHandler
     */
    void initialize(Properties conf);
    Map<K,V> getAll();
    Map<K,V> getNextBatch(long offset);
    void setBatchSize(long batchSize);
    long getBatchSize();
    void close();
}
