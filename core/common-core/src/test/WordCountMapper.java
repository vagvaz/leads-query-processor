package eu.leads.processor.common.test;

import eu.leads.processor.common.LeadsMapper;
import org.infinispan.distexec.mapreduce.Collector;
import org.vertx.java.core.json.JsonObject;


public class WordCountMapper extends LeadsMapper<String, String, String, Integer> {
    private static final long serialVersionUID = -5943370243108735560L;
    private static int chunks = 0, words = 0;


    public WordCountMapper(JsonObject configuration) {
        super(configuration);
        // TODO Auto-generated constructor stub
    }

    public void map(String key, String value, Collector<String, Integer> c) {
        //  System.out.printf("Analyzed %s words in %s lines%n", words, chunks);
        // chunks++;
      /*
       * Split on punctuation or whitespace, except for ' and - to catch contractions and hyphenated
       * words
       */
        for (String word : value.split("[\\p{Punct}\\s&&[^'-]]+")) {
            if (word != null) {
                String w = word.trim();
                if (w.length() > 0) {
                    c.emit(word.toLowerCase(), 1);
                    words++;
                }
            }
        }

        /// if (chunks % 1 == 0)
        //     System.out.printf("Analyzed %s words in %s lines%n", words, chunks);
    }

}
