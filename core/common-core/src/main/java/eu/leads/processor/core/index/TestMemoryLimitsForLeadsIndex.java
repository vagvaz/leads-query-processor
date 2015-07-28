package eu.leads.processor.core.index;

import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by angelos on 11/02/15.
 */
public class TestMemoryLimitsForLeadsIndex {

    public static void main(String[] args) {
        int numStrings = 10000;

        // find number of generated tuples: run until memory exception
        List<LeadsIndexString> lstLeadsIndex = new ArrayList<>();
        List<String> lstStr = new ArrayList<>();

        Random rand = new Random();
        int randomInd;
        int i=0;
        try {
            // create lots of objects here and stash them somewhere
            for(i=0;i<numStrings; i++){
                lstStr.add(RandomStringUtils.randomAlphabetic(10));
            }
            while (true) {
                randomInd = rand.nextInt(numStrings);
                LeadsIndexString lInd = new LeadsIndexString();
                lInd.setCacheName("indexedCache");
                lInd.setAttributeName("attributeName");
                String randomVal = lstStr.get(randomInd);
                lInd.setAttributeValue(randomVal);
                lInd.setKeyName("infinispanKey" + i);
                lstLeadsIndex.add(lInd);
                i++;
            }
        }catch(OutOfMemoryError E){
            System.out.println("Number of generated LeadsIndex instances: "+i);
        }
    }
}
