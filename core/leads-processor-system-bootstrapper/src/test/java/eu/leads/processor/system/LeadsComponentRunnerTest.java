package eu.leads.processor.system;

import eu.leads.processor.common.StringConstants;

/**
 * Created by vagvaz on 8/21/14.
 */
public class LeadsComponentRunnerTest {
   public static void main(String[] args) {
      String[] gargs = {StringConstants.GROUP_ID+"~default-comp-mod~"+StringConstants.VERSION,"defaultGroup","/tmp/comp" +args[0]+".json"};
      LeadsComponentRunner.main(gargs);
   }
}
