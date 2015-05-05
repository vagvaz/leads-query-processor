package eu.leads.processor.common.utils;

import org.slf4j.Logger;

import java.io.Serializable;

/**
 * Created by trs on 5/4/2015.
 */
public class ProfileEvent implements Serializable {
  long start;
  String profileName;
  Logger profileLogger=null;
  public ProfileEvent(String logName, Logger logger) {
    profileLogger= logger;
    start(logName);
  }
  public void start(String logName){
    profileName=logName;
    start = System.currentTimeMillis();
  }
  public void end(){
    profileLogger.info("#PROF# " + profileName + "\t"+ (System.currentTimeMillis()-start) + " ms");
  }
}
