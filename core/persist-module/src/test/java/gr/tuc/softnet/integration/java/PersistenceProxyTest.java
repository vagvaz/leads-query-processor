package gr.tuc.softnet.integration.java;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.core.PersistenceProxy;
import eu.leads.processor.core.ServiceCommand;
import eu.leads.processor.core.comp.LeadsMessageHandler;
import eu.leads.processor.core.comp.LogProxy;
import eu.leads.processor.core.comp.ServiceStatus;
import eu.leads.processor.core.net.DefaultNode;
import eu.leads.processor.core.net.MessageTypeConstants;
import eu.leads.processor.core.net.MessageUtils;
import eu.leads.processor.core.net.Node;
import eu.leads.processor.core.plan.SQLQuery;
import org.junit.Test;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import java.util.UUID;

import static org.vertx.testtools.VertxAssert.*;

public class PersistenceProxyTest extends TestVerticle implements LeadsMessageHandler {
    String persistenceId;
    JsonObject persistConfig = new JsonObject();
    String logId;
    JsonObject logConfig = new JsonObject();
    String id = "testVerticle";
    String group = "testGroup";
    String logAddress = "testVerticle.log";
    String persistenceAddress = UUID.randomUUID().toString() + ".persist";
    String internalGroup = "test.internalGroup";
    String componentType = "mockComponent";
    Node com;
    Node com2;
    PersistenceProxy persist;
    PersistenceProxy persist2;
    LogProxy log;

    public void setUp() throws Exception {
        logConfig = new JsonObject();
        logConfig.putString("id", logAddress);
        logConfig.putString("group", internalGroup);
        logConfig.putString("log", logAddress);
        logConfig.putString("persistence", persistenceAddress);
        logConfig.putString("parent", id + ".serviceMonitor");
        logConfig.putString("componentType", componentType);
        logConfig.putString("componentId", id);


        persistConfig = new JsonObject();
        persistConfig.putString("id", persistenceAddress);
        persistConfig.putString("group", internalGroup);
        persistConfig.putString("log", logAddress);
        persistConfig.putString("persistence", persistenceAddress);
        persistConfig.putString("parent", id + ".serviceMonitor");
        persistConfig.putString("componentType", componentType);
        persistConfig.putString("componentId", id);

        com = new DefaultNode();

        com.initialize(id, group, null, this, this, vertx);
        com.subscribe(id + ".serviceMonitor", this);
        com2 = new DefaultNode();
        com2.initialize(id + "@2", group, null, this, this, vertx);
        log = new LogProxy(logAddress, com);
        persist = new PersistenceProxy(persistenceAddress, com, getVertx());
        persist2 = new PersistenceProxy(persistenceAddress, com2, getVertx());
        persist.start();
        persist2.start();

    }

    @Test
    public void allTests() {
        try {
            System.out.println("put1");
            testPut();
            System.out.println("put2");
            testPut();
            System.out.println("put3");
            testPut();
            System.out.println("put4");
            testPut();
            System.out.println("get");
            testGet();
            System.out.println("put");
            testPut();
            System.out.println("get---");
            testGet();
            System.out.println("put");
            testPut();
            System.out.println("get");
            testGet();
            System.out.println("put----");
            testPut();

            System.out.println("store");
            testStore();
            System.out.println("Read");
            testRead();

            System.out.println("put1");
            testPut();
            System.out.println("put2");
            testPut();
            System.out.println("put3");
            testPut();
            System.out.println("put4");
            testPut();
            System.out.println("get");
            testGet();
            System.out.println("put");
            testPut();
            System.out.println("get---");
            testGet();
            System.out.println("put");
            testPut();
            System.out.println("get");
            testGet();
            System.out.println("put----");
            testPut();

            System.out.println("store");
            testStore();
            System.out.println("Read");
            testRead();
            System.out.println("put1");
            testPut();
            System.out.println("put2");
            testPut();
            System.out.println("put3");
            testPut();
            System.out.println("put4");
            testPut();
            System.out.println("get");
            testGet();
            System.out.println("put");
            testPut();
            System.out.println("get---");
            testGet();
            System.out.println("put");
            testPut();
            System.out.println("get");
            testGet();
            System.out.println("put----");
            testPut();

            System.out.println("store");
            testStore();
            System.out.println("Read");
            testRead();
            System.out.println("put1");
            testPut();
            System.out.println("put2");
            testPut();
            System.out.println("put3");
            testPut();
            System.out.println("put4");
            testPut();
            System.out.println("get");
            testGet();
            System.out.println("put");
            testPut();
            System.out.println("get---");
            testGet();
            System.out.println("put");
            testPut();
            System.out.println("get");
            testGet();
            System.out.println("put----");
            testPut();

            System.out.println("store");
            testStore();
            System.out.println("Read");
            testRead();
            System.out.println("put1");
            testPut();
            System.out.println("put2");
            testPut();
            System.out.println("put3");
            testPut();
            System.out.println("put4");
            testPut();
            System.out.println("get");
            testGet();
            System.out.println("put");
            testPut();
            System.out.println("get---");
            testGet();
            System.out.println("put");
            testPut();
            System.out.println("get");
            testGet();
            System.out.println("put----");
            testPut();

            System.out.println("store");
            testStore();
            System.out.println("Read");
            testRead();
            System.out.println("put1");
            testPut();
            System.out.println("put2");
            testPut();
            System.out.println("put3");
            testPut();
            System.out.println("put4");
            testPut();
            System.out.println("get");
            testGet();
            System.out.println("put");
            testPut();
            System.out.println("get---");
            testGet();
            System.out.println("put");
            testPut();
            System.out.println("get");
            testGet();
            System.out.println("put----");
            testPut();

            System.out.println("store");
            testStore();
            System.out.println("Read");
            testRead();
            System.out.println("put1");
            testPut();
            System.out.println("put2");
            testPut();
            System.out.println("put3");
            testPut();
            System.out.println("put4");
            testPut();
            System.out.println("get");
            testGet();
            System.out.println("put");
            testPut();
            System.out.println("get---");
            testGet();
            System.out.println("put");
            testPut();
            System.out.println("get");
            testGet();
            System.out.println("put----");
            testPut();

            System.out.println("store");
            testStore();
            System.out.println("Read++++++++++++++++++++++++++++++++++++++");
            testRead();
            System.out.println("contains_------------------_*************************");
            testContains();
            System.out.println("Test BatchGets");
            testBatchGet();
            testBatchGet1();
            System.out.println("THIS IS THE EEEEENDDDDDD+_____+++++");
        } catch (Exception e) {
            e.printStackTrace();
        }
        testComplete();
    }

    public void tearDown() throws Exception {
        com.sendTo(persistenceAddress, MessageUtils.createServiceCommand(ServiceCommand.STOP));
        container.undeployModule(logId);
        container.undeployModule(persistenceId);
        persist.cleanup();
    }

    public void testGet() throws Exception {
        JsonObject initial = new JsonObject();
        SQLQuery sql = new SQLQuery("testuser", "SELECT * from testCache");
        sql.setId("1");
        persist.put("testCache", sql.getId(), sql.asJsonObject());
        persist2.put("testCache", sql.getId(), sql.asJsonObject());
        JsonObject ob = persist.get("testCache", sql.getId());
        JsonObject ob2 = persist2.get("testCache", sql.getId());
        if (ob.getString("status").equals("ok")) {
            assertEquals(sql.asJsonObject().toString(), ob.getObject("result").toString());
            assertEquals(sql.asJsonObject().toString(), ob2.getObject("result").toString());
            System.out.println("get successful");
        }
        //      tearDown();
        //      testComplete();
    }

    public void testRead() throws Exception {
        JsonObject initial = new JsonObject();
        SQLQuery sql = new SQLQuery("testuser", "SELECT * from testCache");
        sql.setId("1");
        persist.store(sql.getId(), sql.asJsonObject());
        JsonObject ob = persist.read(sql.getId());
        if (ob.getString("status").equals("ok")) {
            assertEquals(sql.asJsonObject().toString(), ob.getObject("result").toString());
            System.out.println("read successful");
        }
    }

    public void testPut() throws Exception {
        JsonObject initial = new JsonObject();
        SQLQuery sql = new SQLQuery("testuser", "SELECT * from testCache");
        sql.setId("2");
        persist.put("testCache", sql.getId(), sql.asJsonObject());
        persist2.put("testCache", sql.getId(), sql.asJsonObject());
        JsonObject ob = persist.get("testCache", sql.getId());
        JsonObject ob2 = persist2.get("testCache", sql.getId());
        if (ob.getString("status").equals("ok")) {
            assertEquals(sql.asJsonObject().toString(), ob.getObject("result").toString());
            System.out.println("put successful");
        }
        testComplete();
    }

    public void testStore() throws Exception {
        JsonObject initial = new JsonObject();
        SQLQuery sql = new SQLQuery("testuser", "SELECT * from testCache");
        sql.setId("2");
        persist.store(sql.getId(), sql.asJsonObject());
        persist2.store(sql.getId(), sql.asJsonObject());
        JsonObject ob = persist.read(sql.getId());
        JsonObject ob2 = persist2.read(sql.getId());
        if (ob.getString("status").equals("ok")) {
            assertEquals(sql.asJsonObject().toString(), ob.getObject("result").toString());
            System.out.println("store successful");
        }
    }

    public void testBatchGet() throws Exception {
        JsonObject tmpObject = new JsonObject();
        int iterations = 5;
        int key = 0;
        String value = "aaa";
        JsonObject[] data = new JsonObject[iterations];
        for (int i = 0; i < iterations; i++) {

            tmpObject.putString("keyValue", Integer.toString(i));
            tmpObject.putString("valueValue", value + Integer.toString(i));
            data[i] = tmpObject;
            persist.put("resultCache", Integer.toString(i), tmpObject);
            persist2.put("resultCache", Integer.toString(i), tmpObject);
            tmpObject = new JsonObject();
        }

        long min = 1;
        long max = 4;
        JsonArray testArray = persist.batchGet("resultCache", min, max);
        JsonArray testArray2 = persist2.batchGet("resultCache", min, max);
        for (int i = (int) min; i <= max; i++) {
            assertEquals(testArray.get(i - 1).toString(), data[i].toString());
        }
        System.out.println("BATCH MIN-MAX successfull");
    }

    public void testBatchGet1() throws Exception {
        JsonObject tmpObject = new JsonObject();
        int iterations = 5;
        int key = 0;
        String value = "aaa";
        JsonObject[] data = new JsonObject[iterations];
        for (int i = 0; i < iterations; i++) {

            tmpObject.putString("keyValue", Integer.toString(i));
            tmpObject.putString("valueValue", value + Integer.toString(i));
            data[i] = tmpObject;
            persist.put("resultCache", Integer.toString(i), tmpObject);
            persist2.put("resultCache", Integer.toString(i), tmpObject);
            tmpObject = new JsonObject();
        }

        long min = 1;
        long max = iterations;
        JsonArray testArray = persist.batchGet("resultCache", min);
        JsonArray testArray2 = persist2.batchGet("resultCache", min);
        for (int i = (int) min; i < max; i++) {
            assertEquals(testArray.get(i - 1).toString(), data[i].toString());
        }
        System.out.println("BATCH MIN- successfull");
    }

    public void testContains() throws Exception {
        JsonObject initial = new JsonObject();
        SQLQuery sql = new SQLQuery("testuser", "SELECT * from testCache");
        sql.setId("1");
        persist.put("testCache", sql.getId(), sql.asJsonObject());
        persist2.put("testCache", sql.getId(), sql.asJsonObject());
        boolean ob = persist.contains("testCache", sql.getId());
        boolean ob2 = persist2.contains("testCache", sql.getId());
        assertEquals(ob, true);
        System.out.println("contains successful");
    }


    @Override
    public void start() {
        super.start();
    }

    @Override
    protected void initialize() {
        super.initialize();
        try {
            this.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //deploy log module
        //      container.deployModule(StringConstants.LOG_MOD_NAME, logConfig, new Handler<AsyncResult<String>>() {
        //
        //         @Override
        //         public void handle(AsyncResult<String> asyncResult) {
        //            if (asyncResult.succeeded()) {
        //               container.logger().info("Log Module has been deployed ID " + asyncResult.result());
        //               logId = asyncResult.result();
        //               assertNotNull("deployment should not be null ", asyncResult.result());
        //               com.sendTo(internalGroup, MessageUtils.createServiceCommand(ServiceCommand.START));
        //            } else {
        //               container.logger().fatal("Log Module failed to deploy");
        //            }
        //         }
        //      });

        //deploy persistence module
        container.deployModule(StringConstants.PERSIST_MOD_NAME, persistConfig, 1,
                                  new Handler<AsyncResult<String>>() {

                                      @Override
                                      public void handle(AsyncResult<String> asyncResult) {
                                          if (asyncResult.succeeded()) {
                                              container.logger()
                                                  .info("Persistence Module has been deployed ID "
                                                            + asyncResult.result());
                                              persistenceId = asyncResult.result();
                                              assertNotNull("deployment should not be null ",
                                                               asyncResult.result());
                                              com.sendTo(internalGroup, MessageUtils
                                                                            .createServiceCommand(ServiceCommand.START));
                                          } else {
                                              container.logger()
                                                  .fatal("Persistence Module failed to deploy");
                                          }
                                      }
                                  });
    }

    @Override
    protected void startTests() {
        if (persistenceId == null)
            return;
        super.startTests();

    }

    @Override
    public void handle(JsonObject msg) {
        if (msg.getString("type").equals(MessageTypeConstants.SERVICE_STATUS_REPLY))
            if (msg.getString("status").equals(ServiceStatus.RUNNING.toString()))
                ;
        startTests();
    }


}
