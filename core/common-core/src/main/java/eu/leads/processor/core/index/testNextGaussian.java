package eu.leads.processor.core.index;

import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by angelos on 13/02/15.
 */
public class testNextGaussian {

    public static void main(String[] args){
//        Random randq = new Random();
//        System.out.println((randq.nextGaussian()));
//        System.exit(0);

        int numStrings = 10;// 10000
        int numTuples = 8;// 1500000

        // find number of generated tuples: run until memory exception
        List<String> lstStr = new ArrayList<>();
        List<Integer> lstInt = new ArrayList<>();
        List<Double> lstDouble = new ArrayList<>();
        List<Float> lstFloat = new ArrayList<>();
        List<Long> lstLong = new ArrayList<>();
        Random rand = new Random();
        int randomInd;

        for(int i=0;i<numStrings; i++){
            lstStr.add(RandomStringUtils.randomAlphabetic(10));
            lstInt.add(rand.nextInt());
            lstDouble.add(rand.nextDouble());
            lstFloat.add(rand.nextFloat());
            lstLong.add(rand.nextLong());
        }
        for(int i=0;i<numTuples;i++) {
            randomInd = (int) Math.ceil(rand.nextGaussian() * 2 + (numStrings/2));
            System.out.println(lstInt.get(randomInd));
        }
    }
}
