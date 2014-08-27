package eu.leads.distsum;

import com.google.common.collect.Ordering;
import eu.leads.distsum.Utils.LocalView;
import eu.leads.distsum.Utils.Set_Map;
import eu.leads.distsum.Utils.ViolationObject;

import java.util.*;
public class Coordinator extends Node {

    public static final double epsilon = 0.1, EPS = 2.2204460492503131e-06, coordLeeway = 0.5;
    public static final int k = 2;

    int workers_num;
    ArrayList<Object> worker_ids;

    Map<Object, Map<Object, Double>> all_values;  //some of the local values of all nodes necessary during reallocation
    Map<Object, Double> border_values; //border values necessary during reallocation
    Map<Object, Map<Object, Double>> all_deltas;  //the local all_deltas of all worker nodes
    // each node is represented by its key (String)

    Set<Object> topk_set;

    boolean phase3InProgress, initPhase3;
    // long epoch_altering_topk;

    Map<Object, Long> lastTsReceived;

    public Coordinator(ComChannel com) {
        super(Node.COORDINATOR, com);
        all_values = new HashMap<Object, Map<Object, Double>>();
        all_deltas = new HashMap<Object, Map<Object, Double>>();
        border_values = new HashMap<Object, Double>();

        worker_ids = new ArrayList<Object>();
        workers_num = 0;

        topk_set = new HashSet<Object>();
        phase3InProgress = false;

        // epoch_altering_topk = 0;

        lastTsReceived = new HashMap<Object, Long>();
    }

    private synchronized boolean updateAndCheckTs(Object sender, long ts) {

        if (lastTsReceived.containsKey(sender)) {

            if (ts > lastTsReceived.get(sender))
                lastTsReceived.put(sender, ts);
            else
                return true;
        } else
            lastTsReceived.put(sender, ts);
        return false;
    }

    //Receive message from worker.
    @Override
    public synchronized void receiveMessage(Message msg) {

        String sender = msg.getFrom();

        if (updateAndCheckTs(sender, msg.getTs()))
            return;

        // this msg is received when a new worker joins
        // the computation: I have to send him the top-k set
        if (msg.getType().equals("join")) {

            worker_ids.add(sender); //add worker to my list of ids
            workers_num++;

            // send the current top-k set to the new worker
            send(sender, new Message(Node.COORDINATOR, "joinAck", topk_set));
            // System.out.println("Coordinator received join request from node " + sender);
        }else if (msg.getType().equals("violation")) {

            assert (worker_ids.contains(sender));
            ViolationObject vo = (ViolationObject) msg.getBody();

            // ignore them if a resolution in phase 3 has initiated
            if (/*epoch_altering_topk > vo.getEpoch() || */phase3InProgress) {
                // System.out.println("Coordinator aborted violation msg from " + sender + " and phase3 is in progress?" + phase3InProgress);
                return;
            }

            if (vo.isEmptyTopk())
                initPhase3 = true;

            // System.out.println("Coordinator will process violation msg from " + sender);

            // jump to 3rd phase requesting values according to the sender's resolution set
            if (topk_set.isEmpty()) {
                requestPhase3info(sender, new HashSet<Object>(vo.getPartialDataValues().keySet()));
                processPhase3msg(sender, vo.getPartialDataValues(), vo.getBorderValue());

            } else {

                // resolution: 2nd phase
                if (checkIfPhase3Required(sender, vo)){
                    requestPhase3info(sender, new HashSet<Object>(vo.getPartialDataValues().keySet()));
                    processPhase3msg(sender, vo.getPartialDataValues(), vo.getBorderValue());
                }
                else{

                    // attempt to adjust factors through phase 2
                    phase2attempt(sender, vo);

                    if (!assertInvariants()){
                        requestPhase3info(sender, new HashSet<Object>(vo.getPartialDataValues().keySet()));
                        processPhase3msg(sender, vo.getPartialDataValues(), vo.getBorderValue());
                    }
                    else{

                        // send msg to sender with new factors
                        send(sender, new Message(Node.COORDINATOR, "phase2", all_deltas.get(sender)));
                    }

                }
            }

        }else if (msg.getType().equals("replyPhase3")) {

            // System.out.println("Coordinator received partial data values requested for phase 3 from " + sender);

            LocalView lv = (LocalView) msg.getBody();
            processPhase3msg(sender, lv.getPartialValues(), lv.getBorder());
        }else {
            throw new RuntimeException("Invalid message");
        }
    }

    private synchronized void phase2attempt(Object sender, ViolationObject vo){

        Map<Object, Double> max_distance_per_topk_item = new HashMap<Object, Double>();
        for (Object o: vo.getViolatedTopk())
            max_distance_per_topk_item.put( o, Double.MIN_VALUE);

        double deltatf, deltarf, distance;
        for (Object t: vo.getViolatedTopk()){

            deltatf = all_deltas.get(sender).containsKey(t) ? all_deltas.get(sender).get(t) : 0;
            for (Object r: vo.getViolatedRest()){

                deltarf = all_deltas.get(sender).containsKey(r) ? all_deltas.get(sender).get(r) : 0;
                distance = vo.getPartialDataValues().get(r) + deltarf - vo.getPartialDataValues().get(t) - deltatf;

                if (distance > max_distance_per_topk_item.get(t))
                    max_distance_per_topk_item.put(t, distance);

            }
        }

        double d0, df;
        for ( Map.Entry<Object, Double> entry: max_distance_per_topk_item.entrySet() ){

            d0 = all_deltas.get(Node.COORDINATOR).containsKey(entry.getKey()) ? all_deltas.get(Node.COORDINATOR).get(entry.getKey()) - entry.getValue() : - entry.getValue();
            df = all_deltas.get(sender).containsKey(entry.getKey()) ? all_deltas.get(sender).get(entry.getKey()) + entry.getValue() : entry.getValue();

            all_deltas.get(Node.COORDINATOR).put(entry.getKey(), d0);
            all_deltas.get(sender).put(entry.getKey(), df);
        }

    }

    private synchronized boolean checkIfPhase3Required(Object sender, ViolationObject vo){
        double deltat0, deltatf, deltar0, deltarf;
        for (Object t: vo.getViolatedTopk()){

            deltat0 = all_deltas.get(Node.COORDINATOR).containsKey(t) ? all_deltas.get(Node.COORDINATOR).get(t) : 0;
            deltatf = all_deltas.get(sender).containsKey(t) ? all_deltas.get(sender).get(t) : 0;
            for (Object r: vo.getViolatedRest()){

                deltar0 = all_deltas.get(Node.COORDINATOR).containsKey(r) ? all_deltas.get(Node.COORDINATOR).get(r) : 0;
                deltarf = all_deltas.get(sender).containsKey(r) ? all_deltas.get(sender).get(r) : 0;
                if ( vo.getPartialDataValues().get(t) + deltat0 + deltatf < vo.getPartialDataValues().get(r) + deltar0 + deltarf )
                    return true;

            }
        }
        return false;

    }

    /**
     * This function serves as a coordinator's request from all nodes
     * but the sender of the necessary info for the 3rd phase of resolution
     *
     * @param sender        sender to be excluded from the broadcasting msg
     * @param resolutionSet the set of keys for which the coordinator requests the data values
     */
    private synchronized void requestPhase3info(Object sender, HashSet<Object> resolutionSet) {

        phase3InProgress = true;

        for (Object o : worker_ids) {
            if (!o.equals(sender)) {
                send((String) o, new Message(Node.COORDINATOR, "reqPhase3", resolutionSet));
                // System.out.println("Coordinator requested local view from node " + o);
            }
        }

    }

    private synchronized void processPhase3msg(Object sender, HashMap<Object, Double> partialDataValues, double border) {
        assert (phase3InProgress);

        all_values.put(sender, partialDataValues);
        border_values.put(sender, border);

        // check if I have received the requested info from all nodes
        // if so, I ll have to proceed with the resolution phase
        if (all_values.size() == worker_ids.size()) {

            Map<Object, Double> global_values = new HashMap<Object, Double>();
            for (Map.Entry<Object, Map<Object, Double>> outerEntry : all_values.entrySet()) {
                for (Map.Entry<Object, Double> innerEntry : outerEntry.getValue().entrySet()) {
                    if (global_values.containsKey(innerEntry.getKey()))
                        global_values.put(innerEntry.getKey(), global_values.get(innerEntry.getKey()) + innerEntry.getValue());
                    else
                        global_values.put(innerEntry.getKey(), innerEntry.getValue());
                }
            }
            // compute new top-k set
            compute_new_topk(global_values);

            // reallocate new adjustment factors
            allocate_new_factors(global_values);

            // assert that invariants hold !
            assert(assertInvariants());

            // send msgs containing top-k set & factors to each corresponding node
            for (Object o : worker_ids) {
                Set_Map sm = new Set_Map(all_deltas.get(o), topk_set);
                send((String) o, new Message(Node.COORDINATOR, "newSetAndFactors", sm));
            }

            // clear all temporary structures & set the variable blocking violations to false
            all_values.clear();
            border_values.clear();
            initPhase3 = false;
            phase3InProgress = false;
        }
    }

    private synchronized void compute_new_topk(Map<Object, Double> global_values){

        List<Map.Entry<Object, Double>> dupList = new ArrayList<Map.Entry<Object, Double>>(global_values.entrySet());
        Ordering<Map.Entry<Object, Double>> byMapValues = new Ordering<Map.Entry<Object, Double>>() {
            @Override
            public int compare(Map.Entry<Object, Double> left, Map.Entry<Object, Double> right) {
                return right.getValue().compareTo(left.getValue());
            }
        };
        Collections.sort(dupList, byMapValues);

        topk_set.clear();
        int i = 0;
        for (Map.Entry<Object, Double> entry : dupList) {
            if (i < k)
                topk_set.add(entry.getKey());
            else
                break;
            i++;
        }
    }

    private synchronized void allocate_new_factors(Map<Object, Double> global_values){

        all_deltas.clear();
        for (Object o: worker_ids)
            all_deltas.put(o, new HashMap<Object, Double>());
        all_deltas.put(Node.COORDINATOR, new HashMap<Object, Double>());

        if (!initPhase3){

            // 1) calculate sum of border values
            double global_border = 0;
            for (Map.Entry<Object, Double> entry : border_values.entrySet())
                global_border += entry.getValue();

            // 2) calculate "leeway" for each object
            Map<Object, Double> obj_leeways = new HashMap<Object, Double>();
            double tmp;
            for (Map.Entry<Object, Double> entry : global_values.entrySet()) {

                tmp = entry.getValue() - global_border;
                if (topk_set.contains(entry.getKey()))
                    tmp += epsilon;

                obj_leeways.put(entry.getKey(), tmp);
            }

            //3) distribute "leeway" among participating nodes
            // follow strategy of providing the coordinator with half of the total leeway
            // and the rest is evenly divided to every other node, so:
            double workerLeeway = (1 - coordLeeway) / (double) worker_ids.size();

            for (Map.Entry<Object, Double> entry: obj_leeways.entrySet()){

                int j = 0;
                for (Object o: worker_ids){

                    if (all_values.get(o).containsKey(entry.getKey()))
                        all_deltas.get(o).put(entry.getKey(), border_values.get(o) - all_values.get(o).get(entry.getKey()) + workerLeeway*entry.getValue() ) ;
                    else
                        all_deltas.get(o).put(entry.getKey(), border_values.get(o) + workerLeeway*entry.getValue() ) ;

                }
            }

            // computation of coordinator's deltas
            double tmp2;
            for (Map.Entry<Object, Double> entry: obj_leeways.entrySet()) {

                tmp2 = coordLeeway*entry.getValue();
                if ( topk_set.contains(entry.getKey()) )
                    tmp2 -= epsilon;
                all_deltas.get(Node.COORDINATOR).put(entry.getKey(), tmp2);
            }

        }

    }

    private synchronized boolean assertInvariants(){

        // invariant 1
        double sum;
        for (Map.Entry<Object, Double> entry: all_deltas.get(Node.COORDINATOR).entrySet()){

            sum = entry.getValue();

            for (Object o : worker_ids)
                sum += all_deltas.get(o).get(entry.getKey());
            if (sum > EPS)
                return false;

        }

        // invariant 2
        for (Map.Entry<Object, Double> entry: all_deltas.get(Node.COORDINATOR).entrySet()){

            if ( !topk_set.contains(entry.getKey()) ){
                for (Object o: topk_set){
                    if ( all_deltas.get(Node.COORDINATOR).get(o) + epsilon < entry.getValue() )
                        return false;
                }
            }
        }
        return true;
    }

}