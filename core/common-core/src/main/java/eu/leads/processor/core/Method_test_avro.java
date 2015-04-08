package eu.leads.processor.core;
import org.vertx.java.core.json.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by angelos on 03/02/15.
 */
public class Method_test_avro {
    public static void main(String[] args) throws IOException {
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
        Tuple tavro = new Tuple();
// setAttribute
        long startTime = System.nanoTime();
        tavro.setAttribute(attributeName1, value1);
        long stopTime = System.nanoTime();
        tavro.setAttribute(attributeName2, value2);
        tavro.setAttribute(attributeName3, value3);
        tavro.setAttribute(attributeName4, value4);
        tavro.setAttribute(attributeName5, value5);
        tavro.setAttribute(attributeName6, value6);
        tavro.setNumberAttribute(attributeName7, value7);
        tavro.setNumberAttribute(attributeName8, value8);
        tavro.setNumberAttribute(attributeName9, value9);
        tavro.setNumberAttribute(attributeName10, value10);
        System.out.println("############Runtime setAttribute: " + (stopTime-startTime)/1000000 + " ns\n");
        System.out.println(tavro.toString());

// json to avro
        JsonObject job = new JsonObject();
        job.putString(attributeName1, value1);
        job.putString(attributeName2, value2);
        job.putString(attributeName3, value3);
        Tuple tavroJ = new Tuple(job.toString());
        System.out.println("\n\n******************tavroJ"+tavroJ);

// copy test
        Tuple tavroNew = new Tuple();
        tavroNew.copy(tavro.AvroRec);
        Tuple tavroNew2 = new Tuple();
        tavroNew2.copy(tavro.AvroRec);
        tavroNew.removeAttribute(attributeName2);
        tavroNew.removeAttribute(attributeName7);
        tavroNew.removeAttribute(attributeName8);
        System.out.println("\n\n******************tavroNew"+tavroNew);
        System.out.println("\n\n******************tavroNew2"+tavroNew2);

// asJsonObject test
        System.out.println("\n\nAs JSON Object: "+tavro.asJsonObject());

// constructor test
        ArrayList<String> ignoreColumns = new ArrayList<>();
        ignoreColumns.add(attributeName1);
        ignoreColumns.add(attributeName4);
        ignoreColumns.add(attributeName10);
        Tuple tavroNew3 = new Tuple(tavroNew2, tavroNew, ignoreColumns);
        System.out.println("\n\n-------------------Contructor test: "+tavroNew3);
        System.out.println("\n\n-------------------Contructor test: " + tavroNew2);
        //System.exit(0);
// getAttribute
        startTime = System.nanoTime();
        tavro.getAttribute(attributeName1);
        stopTime = System.nanoTime();
        System.out.println("############Runtime getAttribute: " + (stopTime - startTime) / 1000 + " μs\n");
        System.out.println(tavro.getAttribute(attributeName1));
// asString
        startTime = System.nanoTime();
        tavro.asString();
        stopTime = System.nanoTime();
        System.out.println("############Runtime asString: " + (stopTime - startTime) / 1000 + " μs\n");
        System.out.println("tuple avro as String: " + tavro.asString() + "\n");
// remove attribute
        startTime = System.nanoTime();
        tavro.removeAttribute(attributeName1);
        stopTime = System.nanoTime();
        System.out.println("############Runtime removeAtrribute: " + (stopTime - startTime) / 1000 + " μs\n");
        System.out.println("tuple avro: " + tavro + "\n");
// removeAttributes
        List<String> remCol = new ArrayList<>();
        remCol.add(attributeName5);
        remCol.add(attributeName6);
        remCol.add(attributeName7);
        startTime = System.nanoTime();
        tavro.removeAtrributes(remCol);// remain: attributeName8 attributeName9 attributeName10
        stopTime = System.nanoTime();
        System.out.println("############Runtime removeAttributes: " + (stopTime - startTime) / 1000 + " μs\n");
        System.out.println("tuple avro: "+tavro.asString()+"\n");
// keepOnly
        List<String> keepCol = new ArrayList<>();
        tavro.setAttribute(attributeName2, value2);
        tavro.setAttribute(attributeName3,value3);
        tavro.setAttribute(attributeName4, value4);
        tavro.setAttribute(attributeName5,value5);
        tavro.setAttribute(attributeName6,value6);
        tavro.setNumberAttribute(attributeName7, value7);
        tavro.setNumberAttribute(attributeName8, value8);
        tavro.setNumberAttribute(attributeName9, value9);
        tavro.setNumberAttribute(attributeName10, value10);
        keepCol.add(attributeName5);
        keepCol.add(attributeName6);
        keepCol.add(attributeName7);
        keepCol.add(attributeName8);
        keepCol.add(attributeName9);
        keepCol.add(attributeName10);
        System.out.println(tavro);
        startTime = System.nanoTime();
        tavro.keepOnly(keepCol);
        stopTime = System.nanoTime();
        System.out.println(tavro);
        System.out.println("############Runtime keepOnly: " + (stopTime-startTime)/1000 + " μs\n");
// getFieldNames
        List<String> fieldNames;
        startTime = System.nanoTime();
        fieldNames = tavro.getFieldNames();
        stopTime = System.nanoTime();
        System.out.println("############Runtime getFieldNames: " + (stopTime-startTime)/1000 + " μs\n");
        System.out.println(fieldNames);
// hasField
        startTime = System.nanoTime();
        tavro.hasField(attributeName8);
        stopTime = System.nanoTime();
        System.out.println("############Runtime hasField: " + (stopTime-startTime)/1000 + " μs\n");
        System.out.println(attributeName8);
        System.out.println(tavro.hasField(attributeName2));// true
        System.out.println(attributeName1);
        System.out.println(tavro.hasField(attributeName8));// false
// renameAttribute
        startTime = System.nanoTime();
        tavro.renameAttribute(attributeName5, "longnumX");
        stopTime = System.nanoTime();
        System.out.println("############Runtime renameAttribute: " + (stopTime - startTime) / 1000 + " μs\n");
        System.out.println("tuple avro: " + tavro + "\n");
//getGenericAttribute
        startTime = System.nanoTime();
        tavro.getGenericAttribute("doublenum2");
        stopTime = System.nanoTime();
        System.out.println("############Runtime getGenericAttribute: " + (stopTime - startTime) / 1000 + " μs\n");
        System.out.println(tavro.getGenericAttribute("doublenum2"));
// renameAttributes
        Map<String, List<String>> toRename = new HashMap<>();
        List<String> lst8 = new ArrayList<>();
        lst8.add(attributeName8 + "X");//longnum2X
        lst8.add(attributeName8+"Y");//longnum2Y
        List<String> lst9 = new ArrayList<>();
        lst9.add(attributeName9 + "X");//doublenum1X
        lst9.add(attributeName9 + "Y");//doublenum1Y
        List<String> lst10 = new ArrayList<>();
        lst10.add(attributeName10 + "X");//doublenum2X
        lst10.add(attributeName10 + "Y");//doublenum2Y
        toRename.put("longnumX", lst8);
        toRename.put(attributeName9,lst9);
        toRename.put(attributeName10,lst10);
        startTime = System.nanoTime();
        tavro.renameAttributes(toRename);
        stopTime = System.nanoTime();
        System.out.println("############Runtime renameAttributes: " + (stopTime-startTime)/1000000 + " ns\n");
        System.out.println(tavro);

        System.exit(0);
    }
}