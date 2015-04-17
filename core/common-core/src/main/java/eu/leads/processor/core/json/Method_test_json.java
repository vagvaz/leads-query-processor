package eu.leads.processor.core.json;

import java.util.*;

/**
 * Created by angelos on 03/02/15.
 */
public class Method_test_json {

    public static void main(String[] args){

        // Create attribute names
        String attributeName1 = "name1";
        String attributeName2 = "name2";
        String attributeName3 = "name3";
        String attributeName4 = "name4";
        String attributeName5 = "name5";
        String attributeName6 = "name6";
        String attributeName7 = "longnum1";
        String attributeName8 = "longnum2";
        String attributeName9 = "doublenum1";
        String attributeName10 = "doublenum2";

        // Create attributes' values
        String value1 = "b9re9dmqls44ced";
        String value2 = "q2vklxkkexqxh1m";
        String value3 = "69eoihvawk6gco5";
        String value4 = "gd5taacfygbwwc2";
        String value5 = "yubuqp2zqv6hmwi";
        String value6 = "0fdyakype0bxu38";
        Long value7 = 3965960296543005006L;
        Long value8 = -5807565109641799382L;
        Double value9 = 0.7535329047380597;
        Double value10 = 0.47334616297362775;

        // creatre Json tuple
        Tuple tJson = new Tuple();

        // setAttribute
        long startTime = System.nanoTime();
        tJson.setAttribute(attributeName1,value1);
        long stopTime = System.nanoTime();
        System.out.println("Runtime setAttribute: " + (stopTime-startTime)/1000 + " μs\n");

        // getAttribute
        startTime = System.nanoTime();
        tJson.getAttribute(attributeName1);
        stopTime = System.nanoTime();
        System.out.println("Runtime getAttribute: " + (stopTime-startTime)/1000 + " μs\n");

        // asString
        startTime = System.nanoTime();
        tJson.asString();
        stopTime = System.nanoTime();
        System.out.println("Runtime asString: " + (stopTime-startTime)/1000000 + " ms\n");

        // keepOnly
        List<String> keepCol = new ArrayList<>();
        tJson.setAttribute(attributeName2,value2);
        tJson.setAttribute(attributeName3,value3);
        tJson.setAttribute(attributeName4,value4);
        tJson.setAttribute(attributeName5,value5);
        tJson.setAttribute(attributeName6,value6);
        tJson.setNumberAttribute(attributeName7, value7);
        tJson.setNumberAttribute(attributeName8, value8);
        tJson.setNumberAttribute(attributeName9, value9);
        tJson.setNumberAttribute(attributeName10, value10);
        keepCol.add(attributeName5);
        keepCol.add(attributeName6);
        keepCol.add(attributeName7);
        keepCol.add(attributeName8);
        keepCol.add(attributeName9);
        keepCol.add(attributeName10);
        System.out.println(tJson);
        startTime = System.nanoTime();
        tJson.keepOnly(keepCol);
        stopTime = System.nanoTime();
        System.out.println(tJson);
        System.out.println("Runtime keepOnly: " + (stopTime-startTime)/1000 + " μs\n");

        // removeAttribute
        startTime = System.nanoTime();
        tJson.removeAttribute(attributeName1);// remain: attributeName8 attributeName9 attributeName10
        stopTime = System.nanoTime();
        System.out.println("Runtime removeAttribute: " + (stopTime-startTime)/1000 + " μs\n");

        // removeAttributes
        List<String> remCol = new ArrayList<>();
        remCol.add(attributeName5);
        remCol.add(attributeName6);
        remCol.add(attributeName7);
        startTime = System.nanoTime();
        tJson.removeAtrributes(remCol);// remain: attributeName8 attributeName9 attributeName10
        stopTime = System.nanoTime();
        System.out.println("Runtime removeAttributes: " + (stopTime-startTime)/1000 + " μs\n");

        // getFieldNames
        Set<String> fieldNames;
        startTime = System.nanoTime();
        fieldNames = tJson.getFieldNames();
        stopTime = System.nanoTime();
        System.out.println("Runtime getFieldNames: " + (stopTime-startTime)/1000 + " μs\n");
        System.out.println(fieldNames);

        // hasField
        startTime = System.nanoTime();
        tJson.hasField(attributeName8);// true
        stopTime = System.nanoTime();
        System.out.println("Runtime hasField: " + (stopTime-startTime)/1000 + " μs\n");
        tJson.hasField(attributeName1);// false

        // renameAttribute
        startTime = System.nanoTime();
        tJson.renameAttribute(attributeName8, "longnumX");
        stopTime = System.nanoTime();
        System.out.println("Runtime renameAttribute: " + (stopTime-startTime)/1000 + " μs\n");

        //getGenericAttribute
        startTime = System.nanoTime();
        Object tj_attr = tJson.getGenericAttribute("longnumX");
        stopTime = System.nanoTime();
        System.out.println("Runtime getGenericAttribute: " + (stopTime-startTime)/1000 + " μs\n");

        // renameAttributes
        Map<String, List<String>> toRename = new HashMap<>();

        List<String> lst8 = new ArrayList<>();
        lst8.add(attributeName8+"X");
        lst8.add(attributeName8+"Y");

        List<String> lst9 = new ArrayList<>();
        lst9.add(attributeName9+"X");
        lst9.add(attributeName9 + "Y");

        List<String> lst10 = new ArrayList<>();
        lst10.add(attributeName10+"X");
        lst10.add(attributeName10 + "Y");

        List<String> lst11 = new ArrayList<String>();
        lst11.add(attributeName1+"X");
        lst11.add(attributeName1 + "Y");

        toRename.put("longnumX", lst8);
        toRename.put(attributeName9,lst9);
        toRename.put(attributeName10,lst10);
        toRename.put(attributeName1, lst11);

        startTime = System.nanoTime();
        System.out.println("Current tuple: "+tJson);

        tJson.renameAttributes(toRename);
        stopTime = System.nanoTime();
        System.out.println("Runtime renameAttributes: " + (stopTime-startTime)/1000 + " μs\n");

        System.out.println(tJson);// {"doublenum1":0.7535329047380597,"doublenum2":0.47334616297362775,"longnumX":-5807565109641799382}
    }
}