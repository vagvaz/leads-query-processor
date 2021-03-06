package eu.leads.infext.python;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

public class JZC2 {
	private static final int [] timeouts = new int[] {1000,100000};
	
    private final int REQUEST_TIMEOUT;
    private final int MAX_RETRIES = 3;       //  Before we abandon
    
    private String [] endpoints;
    
    private void init(List<String> list) {
		this.endpoints = list.toArray(new String[list.size()]);
    }
    
    public JZC2(List<String> list) {
		init(list);
		this.REQUEST_TIMEOUT 			= timeouts[0];
	}
    
    public JZC2(List<String> list, boolean longTimeout) {
		init(list);
		if(longTimeout) this.REQUEST_TIMEOUT 	= timeouts[1];
		else this.REQUEST_TIMEOUT 		= timeouts[0];
	}

    private JSONArray tryRequest (ZContext ctx, String endpoint, ZMsg request)
    {
        Socket client = ctx.createSocket(ZMQ.REQ);
        client.connect(endpoint);

        //  Send request, wait safely for reply
        ZMsg msg = request.duplicate();
        msg.send(client);
        PollItem[] items = { new PollItem(client, ZMQ.Poller.POLLIN) };
        ZMQ.poll(items, REQUEST_TIMEOUT);

        ZMsg reply = null;
        if (items[0].isReadable()) {
            reply = ZMsg.recvMsg(client);
        	System.out.println("PollItem is readable");
        }
        JSONArray jsonReply = null;
        try{
        if(reply != null) {
	        String strReply = reply.popString();
	        if(strReply != null) {
		        System.out.println("Python reply: "+strReply);
	        	jsonReply = new JSONArray(strReply);
	        }
	        reply.destroy();
        }
        }catch(JSONException e){
            System.err.println("Error parsing Python response: "+e.getMessage());
        }
        //  Close socket in any case, we're done with it now
        ctx.destroySocket(client);
        return jsonReply;
    }
    
    private String[] shuffle(String [] endpoints) {
        ArrayList<String> endpointsList = new ArrayList<>(Arrays.asList(endpoints));
        Collections.shuffle(endpointsList);
		System.out.println("Endpoints queue: "+endpointsList);
        return endpointsList.toArray(this.endpoints);	
    }
    
    //  The client uses a Lazy Pirate strategy if it only has one server to talk
    //  to. If it has two or more servers to talk to, it will try each server just
    //  once:

    public List<Object> send(List<Object> argsArray)
    {
    	List<Object> returnList = new ArrayList<Object>();
    	
        ZContext ctx = new ZContext();
        ZMsg request = new ZMsg();
        for(Object arg : argsArray) {
        	if(arg instanceof java.lang.String)
        		request.add((String)arg);
        	else if(arg instanceof byte[])
        		request.add((byte[])arg);
        }
        JSONArray reply = null;

        int endpointsNo = this.endpoints.length;
        this.endpoints = shuffle(this.endpoints);
        if (endpointsNo == 0)
            System.out.println("I: syntax: jzc <endpoint> …\n");
        else
        if (endpointsNo == 1) {
            //  For one endpoint, we retry N times
            int retries;
            for (retries = 0; retries < MAX_RETRIES; retries++) {
                String endpoint = this.endpoints[0];
                reply = tryRequest(ctx, endpoint, request);
                if (reply != null)
                    break;          //  Successful
                System.out.printf("W: no response from %s, retrying…\n", new Object[]{endpoint});
            }
        }
        else {
            //  For multiple endpoints, try each at most once
            int endpointNbr;
            for (endpointNbr = 0; endpointNbr < endpointsNo; endpointNbr++) {
                String endpoint = endpoints[endpointNbr];
                reply = tryRequest (ctx, endpoint, request);
                if (reply != null)
                    break;          //  Successful
                System.out.printf ("W: no response from %s\n", new Object[]{endpoint});
            }
        }
        if (reply != null) {
            System.out.println ("Service is running OK\n");
//            Iterator<ZFrame> itr = reply.iterator();
//            while(itr.hasNext()) {
//               Object element = itr.next();
//               returnList.add(element.toString());
//               System.out.print(element + " ");
//            }
            System.out.println("Received: <<"+reply+">>");
            JSONArray json = reply;
            try{
            for (int i=0; i<json.length(); i++)
                returnList.add( json.get(i) );
            }catch(JSONException e){
                System.err.println(e.getMessage());
            }
        }
//        else
//        	returnList = null;
        request.destroy();
        ctx.destroy();
        
        return returnList;
    }

}
