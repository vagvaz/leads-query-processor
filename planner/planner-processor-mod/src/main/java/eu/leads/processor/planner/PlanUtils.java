package eu.leads.processor.planner;

import eu.leads.processor.core.plan.LeadsNodeType;
import org.apache.tajo.algebra.OpType;
import org.infinispan.commons.api.BasicCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by vagvaz on 4/15/15.
 */

public class PlanUtils {
  public static Logger log = LoggerFactory.getLogger(PlanUtils.class);

  public static JsonObject updateKeyspaceParameter(JsonObject plan) {

    for (String nodeId : plan.getFieldNames()) {
      JsonObject node = plan.getObject(nodeId);
      if (node.getString("nodetype").equals(LeadsNodeType.ROOT.toString())) {
        if(node.getObject("configuration").containsField("mapreduce")){
          if(!node.getObject("configuration").getObject("mapreduce").containsField("after")){
            node.putArray("keyspace",node.getArray("inputs"));
          }
          else{

          }
        }
      } else if (node.getString("nodetype").equals(LeadsNodeType.SCAN.toString())) {

      } else if (node.containsField("inputs")) {
        node.putArray("keyspace", node.getArray("inputs"));
      } else {
        log.error("Could not handle keyspaces in updateKeyspaceParameter");
      }
    }


    return plan;
  }

  public static JsonObject handleRootOutputNodes(JsonObject input) {
    // if mr keep root otherwise remove it
    Set<String> fields = new HashSet<>();
    fields.addAll(input.getFieldNames());
    for (String nodeId : fields) {
      JsonObject node = input.getObject(nodeId);
      if (node.getString("nodetype").equals(LeadsNodeType.ROOT.toString())) {
        if (node.getObject("configuration").containsField("mapreduce")) {
          JsonObject conf = node.getObject("configuration").getObject("mapreduce");
          if (conf.containsField("after")) {
            JsonObject mrInput = node.copy();
            JsonArray inputs = node.getArray("inputs");
            String inputId = inputs.get(0);
            JsonObject inputNode = input.getObject(inputId);
            JsonObject outputNode = input.getObject(node.getString("output"));
            inputNode.putString("output", outputNode.getString("id"));
            outputNode.putArray("inputs", node.getArray("inputs"));
            //find node Id that has as input the tmpTable
            String tmpTable = conf.getObject("after").getString("outputOnTempTable");
            String outputNodeId = "";
            Set<String> ifields = new HashSet<>();
            ifields.addAll(input.getFieldNames());

            for (String tmpId : ifields) {
              JsonObject tmp = input.getObject(nodeId);
              if (node.getString("nodetype").equals(LeadsNodeType.SCAN.toString())) {
                if (node.getArray("inputs").get(0).equals(tmpTable)) {
                  outputNodeId = node.getString("id");
                  JsonArray inArr = new JsonArray();
                  inArr.add(node.getString("id"));
                  tmp.putArray("inputs", inArr);
                  break;
                }
              }
            }
            mrInput.putString("output", outputNodeId);
          } else {
            //Evertying is fine it will be executed  normally
          }

        } else { // root will not be executed
          JsonArray inputs = node.getArray("inputs");
          String inputId = inputs.get(0);
          JsonObject inputNode = input.getObject(inputId);
          JsonObject outputNode = input.getObject(node.getString("output"));
          inputNode.putString("output", outputNode.getString("id"));
          outputNode.putArray("inputs", node.getArray("inputs"));
          input.removeField(node.getString("id"));
        }
      } else if (node.getString("nodetype").equals(LeadsNodeType.OUTPUT_NODE)) {
        node.putString("output", null);
      }

    }
    // if mr at the beginning remove and add it before the correct table
    // output should be fine
    return input;
  }

  public static JsonObject numberStages(JsonObject input) {
    //findAll nodes with empty input or starting mr
    //start from there a bfs and number
    ArrayList<JsonObject> current = new ArrayList<>();
    ArrayList<JsonObject> next = new ArrayList<>();

    //find initial set
    //first find whether there is a mr in the beginning
    Set<String> fields = new HashSet<>();
    fields.addAll(input.getFieldNames());
    for (String nodeId : fields) {
      JsonObject node = input.getObject(nodeId);
      if (node.getString("nodetype").equals(LeadsNodeType.SCAN.toString())) {
        if (input.containsField((String) node.getArray("inputs").get(0))) {
          current.add(input.getObject((String) node.getArray("inputs").get(0)));
        }
        current.add(node);
      }
    }
    int stageCounter = 1;
    while (current.size() > 0) {
      for (JsonObject node : current) {
        input.getObject(node.getString("id")).putString("stage", Integer.toString(stageCounter));
        stageCounter++;
        if (node.getString("output") != null && !node.getString("output").equals("")) {
          next.add(input.getObject(node.getString("output")));
        }
      }
      current = next;
      next = new ArrayList<>();
    }
    return input;
  }

  public static JsonObject getSchedulerRep(JsonObject input, String destination) {
    JsonObject result = new JsonObject();
    result.putString("destination", destination);
    result.putObject("stages", input);
    return result;
  }


  public static JsonObject annotatePlan(BasicCache statisticsCache, JsonObject input) {
    Set<String> fields = new HashSet<>();
    fields.addAll(input.getFieldNames());
    for (String nodeId : fields) {
      JsonObject node = input.getObject(nodeId);
      float k = calculateK(statisticsCache, node);
      float q = calculateQ(statisticsCache, node);
      node.putNumber("k", k);
      node.putNumber("q", q);
    }
    return input;
  }

  /*
  * This function should calculate from the collected statistics the k value
  * required for scheduling
  * now its random*/
  private static float calculateK(BasicCache statisticsCache, JsonObject node) {

    float result = 0.0f;
    Random random = new Random();
    result = random.nextInt(10);
    result /= 10.0f;
    return result;
  }

  /*
  * This function should calculate from the collected statistics the q value
  * required for scheduling
  * now its random*/
  private static float calculateQ(BasicCache statisticsCache, JsonObject node) {

    float result = 0.0f;
    Random random = new Random();
    result = random.nextInt(10);
    result /= 10.0f;
    return result;
  }

  public static JsonObject updateInformation(JsonObject plan, JsonObject stages, JsonObject information) {

    Set<String> fields = new HashSet<>();
    fields.addAll(stages.getFieldNames());
    for (String stageId : fields) {
      JsonObject stage = stages.getObject(stageId);
      plan = updateNodeWitInfo(plan, stage, information);
    }
    return plan;
  }

  private static JsonObject updateNodeWitInfo(JsonObject plan, JsonObject stage, JsonObject info) {
    //Hard Coded hacks. //For SCAN,WGS
    JsonObject resulting = new JsonObject();
    JsonObject node = plan.getObject(stage.getString("id"));
    if( (node.getString("nodetype").equals(LeadsNodeType.SCAN.toString()) ) ||
        ( node.getString("nodetype").equals(LeadsNodeType.WGS_URL.toString()))
        || ( node.getString("nodetype").equals(LeadsNodeType.GROUP_BY.toString()))  ||
        ( node.getString("nodetype").equals(LeadsNodeType.JOIN.toString()))  ||
        ( node.getString("nodetype").equals(LeadsNodeType.PROJECTION.toString()))  ||
        ( node.getString("nodetype").equals(LeadsNodeType.HAVING.toString()))  ||
        ( node.getString("nodetype").equals(LeadsNodeType.SELECTION.toString())) ||
      (node.getString("nodetype").equals(LeadsNodeType.EXPRS.toString()) && node.getObject("configuration")
          .getObject("body").getString("operationType").equals(OpType.CreateIndex.toString()))
        ){
      Set<String> allSites = new HashSet<>();
      allSites.addAll(info.getObject("microclouds").getFieldNames());
      for(String site : allSites){

        String name = site;
        JsonArray webServices = new JsonArray();
        if (info.getObject("webserviceAddrs").containsField(name)) {
          for (int j = 0; j < info.getObject("webserviceAddrs").getArray(name).size(); j++) {
            String endpoint = info.getObject("webserviceAddrs").getArray(name).get(j);
            if (endpoint.startsWith("http:")) {
              webServices.add(endpoint);
            } else {
              webServices.add("http://" + endpoint + ":8080");
            }
          }
        } else {

          webServices.add("http://localhost:8080");
        }
        resulting.putArray(name, webServices);
      }
    }
    else {
      JsonArray array = stage.getArray("scheduling");

      for (int i = 0; i < array.size(); i++) {
        JsonObject object = array.get(i);
        String name = object.getString("name");
        JsonArray webServices = new JsonArray();
        if (info.getObject("webserviceAddrs").containsField(name)) {
          for (int j = 0; j < info.getObject("webserviceAddrs").getArray(name).size(); j++) {
            String endpoint = info.getObject("webserviceAddrs").getArray(name).get(j);
            if (endpoint.startsWith("http:")) {
              webServices.add(endpoint);
            } else {
              webServices.add("http://" + endpoint + ":8080");
            }
          }
        } else {

          webServices.add("http://localhost:8080");
        }
        resulting.putArray(name, webServices);
      }
    }
    node.putObject("scheduling", resulting);
    return plan;
  }

  public static JsonObject updateTargetEndpoints(JsonObject plan) {
    Set<String> fields = new HashSet<>();
    fields.addAll(plan.getFieldNames());
    for (String nodeId : fields) {
      JsonObject node = plan.getObject(nodeId);
      JsonObject outputNode = null;
      if (node.containsField("output") && node.containsField("scheduling")) {
        outputNode = plan.getObject(node.getString("output"));
        while (!isNodeExecutable(outputNode)) {
          if (outputNode.containsField("output")) {
            outputNode = plan.getObject(outputNode.getString("output"));
          }
          else{
            outputNode = null;
          }
        }
      }
      if (outputNode != null)
        node.putObject("targetEndpoints", outputNode.getObject("scheduling"));
    }
    return plan;
  }



  private static boolean isNodeExecutable(JsonObject node) {
    if (node == null) {
      return true;
    } else if (node.getString("nodetype").equals(LeadsNodeType.ROOT.toString())) {
      if (node.getObject("configuration").containsField("mapreduce")) {
        if (node.getObject("configuration").getObject("mapreduce").containsField("after")) {
          return false;
        } else {
          return true;
        }

      }
      else{
        return false;
      }
    }
    return true;
  }

  public static JsonObject emulateScheduler(JsonObject schedulerRep, JsonObject globalInformation) {
    JsonObject result = new JsonObject();
    Random random = new Random();
    ArrayList<String> microclods = new ArrayList<>();
    if(globalInformation.containsField("active_microclouds")){
      microclods.addAll(globalInformation.getObject("active_microclouds").getFieldNames());
    }else {
      microclods.addAll(globalInformation.getObject("microclouds").getFieldNames());
    }
    String destination = schedulerRep.getString("destination");
    Set<String> ops = new HashSet<>();
    ops.addAll(schedulerRep.getObject("stages").getFieldNames());
    JsonObject stages = schedulerRep.getObject("stages");
    for(String op : ops){
      JsonObject node = stages.getObject(op);
      JsonArray scheduling = null;
      if(node.getString("nodetype").equals(LeadsNodeType.OUTPUT_NODE.toString())){
        scheduling = getSchedulingFor(destination,globalInformation);
      }
      else{
        int index = random.nextInt(microclods.size());
        scheduling = getSchedulingFor(microclods.get(index),globalInformation);
      }
      node.putArray("scheduling", scheduling);
      result.putObject(op,node);
    }

    JsonObject realResult = new JsonObject();
    realResult.putObject("stages",result);
    return realResult;
  }

  private static JsonArray getSchedulingFor(String destination,JsonObject global) {
    JsonArray result = new JsonArray();
    JsonObject ob = new JsonObject();
    ob.putString("name", destination);
    ob.putNumber("t", 0.1);
    ob.putNumber("v", 0.2);
    JsonArray end  = new JsonArray();
    end = global.getObject("webserviceAddrs").getArray(destination);
    ob.putArray("endpoints",end);
    result.add(ob);
    return result;
  }
}
