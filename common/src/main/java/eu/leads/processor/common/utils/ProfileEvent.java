package eu.leads.processor.common.utils;

import org.slf4j.Logger;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by trs on 5/4/2015.
 */
public class ProfileEvent implements Serializable {
  long start;
  String id;
  String profileName;
  Logger profileLogger=null;
  public ProfileEvent(String logName, Logger logger) {
    profileLogger= logger;
    id = UUID.randomUUID().toString().substring(0,5);
    start(logName);
  }
  public void start(String logName){
    profileName= logName;
    start = System.currentTimeMillis();
  }
  public void end(){
    profileLogger.info("#PROF"+id+" " + profileName + "\t"+ (System.currentTimeMillis()-start) + " ms");
  }

  public void end(String endString){
    profileLogger.info("#PROF"+id+" " + profileName + " -> "+endString+ " " +(System.currentTimeMillis()-start) + " ms");
  }
}
