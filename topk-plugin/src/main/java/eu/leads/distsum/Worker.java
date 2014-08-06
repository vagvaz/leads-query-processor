package eu.leads.distsum;

import java.util.*;

/**
 *
 * @author vagvaz
 * @author otrack
 *
 * Created by vagvaz on 7/5/14.
 * The worker node tracks updates on a stream
 * and maintains a local sum of the updates
 * when an update violates the its constrain,
 * the worker informs the coodinator.
 */
public class Worker extends Node {

    private String id;     //node id
    private HashMap<Object, Double> localValues;

    private PriorityQueue<Map.Entry<Object, Double>> topk_adjusted, rest_adjusted;

    private HashMap<Object, Constrain> constrain; //worker constrain
    private ComChannel channel;  //communication channel.

    public Worker(String ID, Map.Entry<Object, Double> initialTopEntry, ComChannel com) {
        super(ID,com);
        this.id = ID;

        localValues = new HashMap<Object, Double>();

        constrain =  new HashMap<Object, Constrain>();
        this.channel = com;

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

        /*topk_adjusted.add(new AbstractMap.SimpleEntry<Object, Double>(2, 2d));
        topk_adjusted.add(new AbstractMap.SimpleEntry<Object, Double>(3, 3d));

        rest_adjusted.add(new AbstractMap.SimpleEntry<Object, Double>(2, 2d));
        rest_adjusted.add(new AbstractMap.SimpleEntry<Object, Double>(3, 3d));

        System.out.println(topk_adjusted.poll());
        System.out.println(rest_adjusted.poll());*/

    }


    public boolean update(Object objToUpd, double newvalue){

        double localValue = localValues.get(objToUpd) + newvalue;

        if(constrain.get(objToUpd).violates(localValue))
        {
            channel.sentTo(Node.COORDINATOR, new Message(id,"violation",new AbstractMap.SimpleEntry<Object, Double>(objToUpd, localValue) ));
            return true;
        }
        return false;
    }


    /**
     * Getter for property 'id'.
     *
     * @return Value for property 'id'.
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for property 'id'.
     *
     * @param id Value to set for property 'id'.
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void receiveMessage(Message msg) {

        /*Message reply = new Message(id,"reply");

        //If the message is a get local values then set the local sum as body to the reply
        if(msg.getType().equals("get")){
            reply.setBody(localValue);
            channel.sentTo(COORDINATOR,reply);
        }
        //if the message is a new constrain just update the local constrain.
        else if (msg.getType().equals("constrain")){
            this.constrain = (Constrain) msg.getBody();
        }*/
    }
}
