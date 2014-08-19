package eu.leads.processor.common.test;

 
import java.util.Iterator;
import java.util.Properties;

import eu.leads.processor.common.LeadsReducer;

public class WordCountReducer extends LeadsReducer<String, Integer> {

   public WordCountReducer(Properties configuration) {
		super(configuration);
		// TODO Auto-generated constructor stub
	}

private static final long serialVersionUID = 1901016598354633256L;

   public Integer reduce(String key, Iterator<Integer> iter) {
      int sum = 0;
      while (iter.hasNext()) {
         Integer i = iter.next();
         sum += i;
      }
      return sum;
   }
}
