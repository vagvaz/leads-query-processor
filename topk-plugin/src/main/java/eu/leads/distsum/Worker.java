package eu.leads.distsum;

import eu.leads.distsum.Utils.LocalView;
import eu.leads.distsum.Utils.Set_Map;
import eu.leads.distsum.Utils.ViolationObject;

import java.util.*;

/**
 *
 * @author vagvaz
 * @author otrack
 *
 * Created by vagvaz on 7/5/14.
 * The worker node tracks updates on a stream
 * and maintains a local sum of the updates
 * when an update violates the its localDeltas,
 * the worker informs the coodinator.
 */
public class Worker extends Node {

    private String id;     //node id
    private HashMap<Object, Double> localValues;
    private HashMap<Object, Double> localDeltas; //worker localDeltas
    private PriorityQueue<Map.Entry<Object, Double>> topk_adjusted, rest_adjusted;
    Set<Object> topk_set;

    long lastTsReceived;

    public Worker(String ID, ComChannel com/*, Object firstObject, double firstValue*/) {

        super(ID,com);
        this.id = ID;

        topk_set = new HashSet<Object>();

        localValues = new HashMap<Object, Double>();

        localDeltas =  new HashMap<Object, Double>();

        topk_adjusted = new PriorityQueue<Map.Entry<Object, Double>>(10,
                new Comparator<Map.Entry<Object, Double>>() {
                    @Override
                    public int compare(Map.Entry<Object, Double> left, Map.Entry<Object, Double> right) {
                        return left.getValue().compareTo(right.getValue());
                    }
                } );

        rest_adjusted = new PriorityQueue<Map.Entry<Object, Double>>(10,
                new Comparator<Map.Entry<Object, Double>>() {
                    @Override
                    public int compare(Map.Entry<Object, Double> left, Map.Entry<Object, Double> right) {
                        return right.getValue().compareTo(left.getValue());
                    }
                } );

        send(Node.COORDINATOR, new Message(id,"join",null));
        // System.out.println("Worker "+id+" sent join request");

        lastTsReceived = -1;
    }

    private synchronized boolean updateAndCheckTs(long ts){

        if (lastTsReceived!=-1){

            if (ts > lastTsReceived)
                lastTsReceived = ts;
            else
                return true;
        }
        else
            lastTsReceived = ts;
        return false;
    }

    @Override
    public synchronized void receiveMessage(Message msg) {

        if (msg.getType().equals(""))
            return;

        if ( updateAndCheckTs(msg.getTs()) )
            return;

        if (msg.getType().equals("joinAck")){

            // System.out.println("Worker "+id+": Received join ACK from coordinator");

            topk_set.clear();
            for (Object o: (HashSet) msg.getBody()){
                topk_set.add(o);
            }

            if (!topk_set.isEmpty()){
                keepFactorsValuesConsistent();
                repartitionHeaps();
                if (constraintViolationCheck())
                    triggerViolation();
            }

        }
        else if(msg.getType().equals("newSetAndFactors")){

            Set_Map sm = (Set_Map) msg.getBody();
            localDeltas.clear();
            localDeltas.putAll(sm.getDeltas());
            topk_set.clear();
            topk_set.addAll(sm.getTopkset());

            keepFactorsValuesConsistent();
            repartitionHeaps();
            if (constraintViolationCheck())
                triggerViolation();
        }
        else if(msg.getType().equals("reqPhase3")){

            HashSet<Object> resolutionSet = (HashSet<Object>) (msg.getBody());
            LocalView lv = new LocalView(new HashMap<Object, Double>(), 0);

            // encapsulate the request local view's values to the msg for the coordinator
            for (Object o: resolutionSet){
                if ( localValues.containsKey(o) )
                    lv.getPartialValues().put(o, localValues.get(o));
            }

            if (!localValues.isEmpty()) {

                PriorityQueue<Map.Entry<Object, Double>> rest_adjusted1 =
                        new PriorityQueue<Map.Entry<Object, Double>>(rest_adjusted);
                //TODO: NULL POINTER HERE
                while ( (!rest_adjusted1.isEmpty()) && topk_adjusted.peek().getValue() < rest_adjusted1.peek().getValue()){
                    rest_adjusted1.poll();
                }

                if (topk_set.isEmpty()){
                    assert(rest_adjusted.isEmpty() && topk_adjusted.isEmpty());
                    lv.setBorder(0);
                }
                else{
                    if (!rest_adjusted1.isEmpty())
                        lv.setBorder( Math.min(topk_adjusted.peek().getValue(), rest_adjusted1.peek().getValue()) );
                    else
                        lv.setBorder( topk_adjusted.peek().getValue() );
                }
            }

            send(Node.COORDINATOR, new Message(id,"replyPhase3", lv));
            // System.out.println("Worker "+id+": sent local view for phase 3");

        }
        else if (msg.getType().equals("phase2")){

            HashMap<Object, Double> h = (HashMap) msg.getBody();
            localDeltas.clear();
            localDeltas.putAll(h);

            keepFactorsValuesConsistent();
            repartitionHeaps();
            if (constraintViolationCheck())
                triggerViolation();
        }

    }

    private synchronized void keepFactorsValuesConsistent(){
        assert(!topk_set.isEmpty());

        for (Object o: topk_set){
            if (!localValues.containsKey(o))
                localValues.put(o, 0d);
        }

        for (Map.Entry<Object, Double> entry:localDeltas.entrySet()){
            if (!localValues.containsKey(entry.getKey()))
                localValues.put(entry.getKey(), 0d);
        }
    }

    /**
     * Re-partition adjusted heaps according to the received top-k set
     */
    private synchronized void repartitionHeaps(){
        topk_adjusted.clear();
        rest_adjusted.clear();

        for (Map.Entry<Object, Double> entry: localValues.entrySet()) {
            if (topk_set.contains(entry.getKey()))
                insertAfterAddingDelta(entry.getKey(), topk_adjusted);
            else
                insertAfterAddingDelta(entry.getKey(), rest_adjusted);
        }
    }

    private synchronized void insertAfterAddingDelta(Object objUpdated, PriorityQueue<Map.Entry<Object, Double>> prQ){
        if (localDeltas.containsKey(objUpdated))
            prQ.add( new AbstractMap.SimpleEntry<Object, Double>(objUpdated, localValues.get(objUpdated) + localDeltas.get(objUpdated) )  );
        else
            prQ.add( new AbstractMap.SimpleEntry<Object, Double>(objUpdated, localValues.get(objUpdated) )  );
    }

    private synchronized boolean constraintViolationCheck(){
        assert(!topk_adjusted.isEmpty());
        if ( (!rest_adjusted.isEmpty()) &&  (topk_adjusted.peek().getValue() < rest_adjusted.peek().getValue()) )
            return true;
        return false;
    }

    /**
     * Function running for each new update
     * After updating local view,
     * it checks if this node has received a topk set - if not it initiates violation
     * in order to have the coordinator initialize one.
     *
     * If a topk set has already been received,
     * the node updates its heaps and checks for constraint violation.
     * @param objToUpd
     * @param drift
     * @return
     */
    public synchronized void update(Object objToUpd, double drift){

        double oldLocalValue = 0;
        if (localValues.containsKey(objToUpd))
            oldLocalValue += localValues.get(objToUpd);
        localValues.put(objToUpd, oldLocalValue + drift);

        if (topk_set.isEmpty())
            triggerViolation();
        else{
            heapsAdjAfterSingleValueUpdate(objToUpd, oldLocalValue);
            if (constraintViolationCheck())
                triggerViolation();
        }
    }

    private synchronized void triggerViolation(){

        ViolationObject vo = new ViolationObject(new HashMap<Object, Double>(), new HashSet<Object>(),
                new HashSet<Object>(), 0, topk_set.isEmpty() );

        if (topk_set.isEmpty()){

            vo.getPartialDataValues().putAll(localValues);

            ArrayList<Double> tempVals = new ArrayList<Double>();
            for (Map.Entry<Object, Double> entry: localValues.entrySet()) {
                vo.getViolatedRest().add(entry.getKey());
                tempVals.add(entry.getValue());
            }
            vo.setBorderValue(/*Collections.max(tempVals)*/0);
        }
        else{

            // send set of objects involved in violated constraints
            PriorityQueue<Map.Entry<Object, Double>> topk_adjusted1, rest_adjusted1;
            topk_adjusted1 = new PriorityQueue<Map.Entry<Object, Double>>(topk_adjusted);
            rest_adjusted1 = new PriorityQueue<Map.Entry<Object, Double>>(rest_adjusted);

            assert( (!rest_adjusted1.isEmpty()) && (!topk_adjusted1.isEmpty()) );

            double max_of_rest_adjusted = rest_adjusted1.peek().getValue();
            while ( (!rest_adjusted1.isEmpty()) && topk_adjusted1.peek().getValue() < rest_adjusted1.peek().getValue()){
                vo.getViolatedRest().add(rest_adjusted1.poll().getKey());
            }
            while ( (!topk_adjusted1.isEmpty()) && topk_adjusted1.peek().getValue() < max_of_rest_adjusted){
                vo.getViolatedTopk().add(topk_adjusted1.poll().getKey());
            }

            // & send all partial data values for objects in resolution set : T union F
            for (Object o: topk_set){
                if (localValues.containsKey(o))
                    vo.getPartialDataValues().put(o, localValues.get(o));
            }
            for (Object o: vo.getViolatedTopk()){
                if (localValues.containsKey(o))
                    vo.getPartialDataValues().put(o, localValues.get(o));
            }
            for (Object o: vo.getViolatedRest()){
                if (localValues.containsKey(o))
                    vo.getPartialDataValues().put(o, localValues.get(o));
            }

            // & send border value
            if (!rest_adjusted1.isEmpty())
                vo.setBorderValue(Math.min(topk_adjusted.peek().getValue(), rest_adjusted1.peek().getValue()));
            else
                vo.setBorderValue( topk_adjusted.peek().getValue() );

        }

        send(Node.COORDINATOR, new Message(id,"violation",vo));
        // System.out.println("Worker "+id+": Sent violation msg to coordinator and topk_set is empty?"+topk_set.isEmpty());
    }

    private synchronized void heapsAdjAfterSingleValueUpdate(Object objUpdated, double oldLocalValue){

        assert(localValues.containsKey(objUpdated));

        double adjustedValue = oldLocalValue;
        if (localDeltas.containsKey(objUpdated))
            adjustedValue += localDeltas.get(objUpdated);
        Map.Entry<Object, Double> entry = new AbstractMap.SimpleEntry<Object, Double>(objUpdated, adjustedValue);

        //first search in the heap containing adjusted top-k set elements
        if (topk_adjusted.remove(entry)){
            insertAfterAddingDelta(objUpdated, topk_adjusted);
            return;
        }
        rest_adjusted.remove(entry); //dont have to test if exists, in any case it will be placed in rest_adjusted
        insertAfterAddingDelta(objUpdated, rest_adjusted);
    }

    /**
     * Getter for property 'id'.
     *
     * @return Value for property 'id'.
     */
    public synchronized String getId() {
        return id;
    }

    /**
     * Setter for property 'id'.
     *
     * @param id Value to set for property 'id'.
     */
    public synchronized void setId(String id) {
        this.id = id;
    }
}
