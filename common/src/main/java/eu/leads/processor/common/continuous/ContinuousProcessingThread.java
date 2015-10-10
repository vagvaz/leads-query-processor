package eu.leads.processor.common.continuous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

/**
 * Created by vagvaz on 10/5/15.
 */
public class ContinuousProcessingThread extends Thread{
  protected BasicContinuousListener owner;
  protected Queue eventQueue;
  protected InputBuffer buffer;
  protected volatile Object ownerMutex;
  Logger log = LoggerFactory.getLogger(ContinuousProcessingThread.class);
//  protected volatile Object mutex = new Object();
  public ContinuousProcessingThread(BasicContinuousListener owner){
    this.owner = owner;
    eventQueue = owner.getEventQueue();
    buffer = owner.getBuffer();
    ownerMutex = owner.getMutex();
  }

  @Override
  public void run(){
    log.error("Started CONTINUOUSTHREAD");
    while(!owner.getIsFlushed() || !eventQueue.isEmpty()){
      System.out.println("Start thread processing");
      EventTriplet triplet = (EventTriplet) eventQueue.poll();
      processEvent(triplet);
      if(owner.getIsFlushed()){
        if(eventQueue.isEmpty()) {
          owner.processBuffer();
          buffer.clear();
          owner.signal();
          return;
        }
      }
      else{
        if(eventQueue.isEmpty()){
          synchronized (ownerMutex){
            try {
              ownerMutex.wait();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }

  private void processEvent(EventTriplet triplet) {
    System.err.println("PROCESSING triplet");
    log.error("PROCESSING triplet");
    boolean processBuffer = false;
    switch (triplet.getType()) {
      case CREATED:
        processBuffer = buffer.add(triplet.getKey(),triplet.getValue());
        break;
      case MODIFIED:
        processBuffer = buffer.modify(triplet.getKey(),triplet.getValue());
        break;
      case REMOVED:
        processBuffer =buffer.remove(triplet.getKey());
        break;
    }
    if(processBuffer){
      System.out.println("Processing buffer");
      owner.processBuffer();
      buffer.clear();
    }
  }
}
