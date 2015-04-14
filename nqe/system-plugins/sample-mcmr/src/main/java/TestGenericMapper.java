import eu.leads.processor.core.Tuple;
import eu.leads.processor.infinispan.LeadsMapper;
import org.apache.commons.configuration.XMLConfiguration;
import org.infinispan.distexec.mapreduce.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by vagvaz on 3/31/15.
 */
public class TestGenericMapper extends LeadsMapper<String,Tuple, String,Tuple> {

   Random random;
   int numberOfFields;

   public TestGenericMapper(){
     super();
   }
   @Override
   public void initialize(XMLConfiguration mapConfig) {
      super.initialize(mapConfig);
      numberOfFields = mapConfig.getInt("fields");
      random = new Random();
   }

   public void map(String key, Tuple value, Collector<String, Tuple> collector) {
      Set<String> fields  = value.getFieldNames();
      List<String> alist = new ArrayList<String>(fields);
      ArrayList<String> chosen = new ArrayList<String>(numberOfFields);
      chosen.add(alist.get(random.nextInt() % fields.size()));
      Tuple t = new Tuple();
      String ikey  = "";
      for(String field : chosen){
         ikey += field;
         t.setAttribute(field,value.getValue(field));
      }

      collector.emit(ikey,t);
   }
}
