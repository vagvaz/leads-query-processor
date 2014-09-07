package tuc.apon.googleMaps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class GoogleMapsFunctions {

	/**
	 * This method simply pins addresses on a google map
	 * displaying string "addressInfo" in an infoWindow
	 * 
	 * @param argVals holds argument values, here the lists "addresses" and "addressInfo"
	 * @param retVals holds the return values, here nothing
	 */
	public static void pinAddresses(HashMap<String, Object> argVals, HashMap<String, Object> retVals){
		
		LinkedList<String> addresses = (LinkedList<String>)argVals.get("address");
		LinkedList<String> comments = (LinkedList<String>) argVals.get("addressInfo");
		showMap(addresses,comments,false);	
	}
	
	/**
	 * This function pins every address that its driving distance
	 * from a reference address (startAddress) respects the
	 * limit imposed by range
	 * 
	 * @param argVals argVals holds argument values, here the lists "startAddress", "addressInfo" and "range"
	 * @param retVals holds the return values, here nothing
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static void pinAddressesInRange(HashMap<String, Object> argVals,HashMap<String, Object> retVals) 
		throws IOException, ParserConfigurationException, SAXException{
		
		String origin = (String)argVals.get("startAddress");
		long range = (java.lang.Long)argVals.get("range");
		LinkedList<String> addresses = (LinkedList<String>)argVals.get("address");
		LinkedList<String> comments = (LinkedList<String>)argVals.get("addressInfo");
		LinkedList<String> survivedComments = new LinkedList<String>();
		LinkedList<String> survivedAddr = new LinkedList<String>();
		
		for(int i=0;i<addresses.size();i++){
			String addr = addresses.get(i);
			if(getShortestDistance(origin, addr) < range){
				survivedAddr.add(addr);
				if(comments.size()>i)
					survivedComments.add(comments.get(i));
			}
		}
		survivedAddr.add(origin);
		
		String url = "";
		
		for(int i=0;i<survivedAddr.size();i++){
			if(survivedAddr.get(i)!=null){
				if(i==0){
					url = ((String)survivedAddr.get(i)).replace(" ", "+")+'&';
				}
				url += ((String)survivedAddr.get(i)).replace(" ", "+")+'&';
			}
		}
		showMap(survivedAddr,survivedComments,true);
		
	}
	
	/**
	 * This function, for each reference address,
	 * returns the closest of the interest addresses.
	 * Info for each address is displayed in infoWindows
	 * 
	 * @param argVals holds argument values, here the lists "referenceAddress", "address" and "addressInfo"
	 * @param retVals holds the return values, here nothing
	 */
	public static void pinClosestAddress(HashMap<String, Object> argVals,HashMap<String, Object> retVals){
		
		LinkedList<String> referenceAddresses = (LinkedList<String>)argVals.get("referenceAddress");
		LinkedList<String> interestAddresses = (LinkedList<String>)argVals.get("address");
		LinkedList<String> comments = (LinkedList<String>)argVals.get("addressInfo");
		LinkedList<String> closestAddresses  = new LinkedList<String>();
		LinkedList<String> closestComments  = new LinkedList<String>();
		
		/*
		 * for each reference address we search for the
		 * closest interestAddress
		 */
		for(int i=0;i< referenceAddresses.size();i++){	
			float shortestDst = -1;
			float tmpDst = -1;
			int addrIndx = -1;
			for(int j=0;j<interestAddresses.size();j++){
				try{
					if(j%10==0)
						try {
							/*
							 * we sleep here for 2seconds to slow down the http requests to
							 * Google so that we don't get a QUERY_LIMIT fault code as response
							 */
							Thread.currentThread().sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					if(shortestDst == -1){
						shortestDst = getShortestDistance(referenceAddresses.get(i), interestAddresses.get(j));
						addrIndx = j;
					}
					else{
						tmpDst = getShortestDistance(referenceAddresses.get(i), interestAddresses.get(j));
						if(tmpDst < shortestDst && tmpDst!=-1){
							shortestDst = tmpDst;
							addrIndx = j;
						}
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				}
			}
			
			if(addrIndx != -1){
				if(!closestAddresses.contains(interestAddresses.get(addrIndx))){	//avoid duplicates
					closestAddresses.add(interestAddresses.get(addrIndx));
					 
					if(shortestDst < 999) //transform between meters and kilometers
						closestComments.add(comments.get(addrIndx)+"<br/>"+shortestDst+"m from: "+referenceAddresses.get(i));
					else{
						shortestDst/=1000;
						closestComments.add(comments.get(addrIndx)+"<br/>"+shortestDst+"km from: "+referenceAddresses.get(i));
					}
				}else{
					int index = closestAddresses.indexOf(interestAddresses.get(addrIndx));
					if(shortestDst < 999) //transform between meters and kilometers
						closestComments.set(index,closestComments.get(index)+"<br/>"+shortestDst+"m from "+referenceAddresses.get(i));
					else{
						shortestDst /=1000;
						closestComments.set(index,closestComments.get(index)+"<br/>"+shortestDst+"km from "+referenceAddresses.get(i));
					}
				}
			}
		}
		
		showMap(closestAddresses,referenceAddresses, closestComments);
		
	}
	
	/**
	 * This method creates an HTML file with the necessary code
	 * to display a google map.
	 * 
	 * @param addresses holds the addresses to be displayed
	 * @param comments holds the information for each address
	 * @param refAddr is true if there is a reference address
	 * 		  and false otherwise
	 */
	private static void showMap(LinkedList<String> addresses, LinkedList<String> comments, boolean refAddr){
		
		String url = null;
		
			File f = new File ("plugins/connectors/googleMaps/bin/tuc/apon/googleMaps/gmaps2.html");
//			f.deleteOnExit();
			try {
				Writer output = new BufferedWriter(new FileWriter(f));
				output.write("<%@page contentType=\"text/html\" pageEncoding=\"UTF-8\"%>\n\n");
				
				output.write("<!DOCTYPE html \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n");
				output.write("\t\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n\n");
				
				output.write("\t\t<html>\n");
				output.write("\t\t\t<head>\n");
				output.write("\t\t\t\t<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>\n");
				output.write("\t\t\t\t<title>Google Maps</title>\n");
				//insert your google code API key here
				output.write("\t\t\t\t<script src=\"http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAehEtphK7nLd2bfjGnoeamRT2yXp_ZAY8_ufC3CFXhHIE1NvwkxRt67OEH9Y01Qjh2XRhCjBABPhfYg&sensor=false\"\n");
				output.write("\t\t\t\t\ttype=\"text/javascript\"></script>\n");
				output.write("\t\t\t\t<script type=\"text/javascript\">\n");
				output.write("\t\t\t\t\tvar map = null;\n");
				output.write("\t\t\t\t\tvar geocoder = null;\n");
				output.write("\t\t\t\t\tfunction initialize() {\n");
				output.write("\t\t\t\t\t\tif (GBrowserIsCompatible()) {\n");
				output.write("\t\t\t\t\t\t\tmap = new GMap2(document.getElementById(\"map_canvas\"));\n");
				output.write("\t\t\t\t\t\t\tmap.setCenter(new GLatLng(35.517321015720384, 24.02161180973053), 13)\n");
				output.write("\t\t\t\t\t\t\tmap.setUIToDefault();\n");
				output.write("\t\t\t\t\t\t\tgeocoder = new GClientGeocoder();");
				output.write("\t\t\t\t\t\t}\n\n");
				
				output.write("\t\t\t\t\t\tvar addresses=new Array(");
				for(int i=0;i<addresses.size();i++){
					String addr = addresses.get(i);
					output.write("\""+addr+"\"");
					if(i != addresses.size()-1) output.write(",");
				}
				output.write(");\n");
				
				output.write("\t\t\t\t\t\tvar comments=new Array(");
				for(int i=0;i<addresses.size();i++){	//this is done so that comments' array 
														//is the same size with addresses array
					String comnt = "";
					if(i<comments.size())
						comnt = comments.get(i);
					output.write("\""+comnt+"\"");
					if(i != addresses.size()-1) output.write(",");
				}
				output.write(");\n");
				
				output.write("\t\t\t\t\t\tfor(i = 0; i < addresses.length; i++){\n");
				if(!refAddr){
					output.write("\t\t\t\t\t\t\tshowAddress(addresses[i],i,comments[i],0);\n");
				}else{
					output.write("\t\t\t\t\t\t\tif(i==(addresses.length-1)){\n");
					output.write("\t\t\t\t\t\t\t\tshowAddress(addresses[i],i,comments[i],1);\n");
					output.write("\t\t\t\t\t\t\t}else{\n");
					output.write("\t\t\t\t\t\t\t\tshowAddress(addresses[i],i,comments[i],0);\n");
					output.write("\t\t\t\t\t\t\t}\n");
				}
//				output.write("\t\t\t\t\t\t\tshowAddress(addresses[i],i,comments[i]);\n");
				output.write("\t\t\t\t\t\t}\n\n");
				output.write("\t\t\t\t\t\tfunction showAddress(address, index,comments,blue) {\n");
				output.write("\t\t\t\t\t\t\tif (geocoder) {\n");
				output.write("\t\t\t\t\t\t\t\tgeocoder.getLatLng(\n");
				output.write("\t\t\t\t\t\t\t\t\taddress,\n");
				output.write("\t\t\t\t\t\t\t\t\tfunction(point) {\n");
				output.write("\t\t\t\t\t\t\t\t\t\tif (!point && i < addresses.length) {\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\talert(address + \" not found\");\n");
				output.write("\t\t\t\t\t\t\t\t\t\t} else {\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tmap.setCenter(point, 13);\n\n");
				
				output.write("\t\t\t\t\t\t\t\t\t\t\tvar letter = String.fromCharCode(\"A\".charCodeAt(0) + index);\n\n");
				
				output.write("\t\t\t\t\t\t\t\t\t\t\tvar baseIcon = new GIcon(G_DEFAULT_ICON);\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tbaseIcon.shadow = \"http://www.google.com/mapfiles/shadow50.png\";\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tbaseIcon.iconSize = new GSize(20, 34);\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tbaseIcon.shadowSize = new GSize(37, 34);\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tbaseIcon.iconAnchor = new GPoint(9, 34);\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tbaseIcon.infoWindowAnchor = new GPoint(9, 2);\n\n");
				
				output.write("\t\t\t\t\t\t\t\t\t\t\tvar letteredIcon = new GIcon(baseIcon);\n");
//				output.write("\t\t\t\t\t\t\t\t\t\t\tletteredIcon.image = \"http://www.google.com/mapfiles/marker\" + letter + \".png\";\n\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tif(blue==1){\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\t\tletteredIcon.image = \"markers/marker_blue.png\";\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\t}\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\telse{\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\t\tletteredIcon.image = \"markers/marker.png\";\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\t}\n\n");
				/*
				 * var letteredIcon = new GIcon(baseIcon);
  letteredIcon.image = "http://www.google.com/mapfiles/marker" + letter + ".png";
				 */
				//output.write("\t\t\t\t\t\t\t\t\t\t\tletteredIcon.image = \"markers/marker_blue.png\";\n\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tmarkerOptions = { icon:letteredIcon };\n");
				
				output.write("\t\t\t\t\t\t\t\t\t\t\tvar marker = new GMarker(point, markerOptions);\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tmap.addOverlay(marker);\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tGEvent.addListener(marker, \"click\", function(){\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\t\tmarker.openInfoWindowHtml(address.replace(/\\+/g, \" \")+\"<br>\"+comments);\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\t});\n");

				output.write("\t\t\t\t\t\t\t\t\t\t}\n");
				output.write("\t\t\t\t\t\t\t\t\t}\n");
				output.write("\t\t\t\t\t\t\t\t);\n");
				output.write("\t\t\t\t\t\t\t}\n");
				output.write("\t\t\t\t\t\t}\n");
				output.write("\t\t\t\t\t}\n");
				output.write("\t\t\t\t</script>\n");
				output.write("\t\t\t</head>\n\n");
				
				output.write("\t\t\t<body onload=\"initialize()\" onunload=\"GUnload()\">\n");
				output.write("\t\t\t<div id=\"map_canvas\" style=\"width: 800px; height: 600px\"></div>\n");
				output.write("\t\t</body>\n");
				output.write("\t</html>\n");
				
				output.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			url = "file://"+f.getAbsolutePath();
		
		
		if( !java.awt.Desktop.isDesktopSupported() ) {

            System.err.println( "Desktop is not supported (fatal)" );
            System.exit( 1 );
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {

            System.err.println( "Desktop doesn't support the browse action (fatal)" );
            System.exit( 1 );
        }

        try {
        	/*
        	 * if all ok, making a URI from the URL and feeding it
        	 * to the platform's default browser 
        	 */
        	java.net.URI uri = new java.net.URI(url);
            desktop.browse( uri );
        }
        catch ( Exception e ) {
            System.err.println( e.getMessage() );
            e.printStackTrace();
        }		
	}
	
	/**
	 * Overloaded method. This is the same as above with different arguments,
	 * @param addresses
	 * @param refAddresses
	 * @param comments
	 */
	private static void showMap(LinkedList<String> addresses, LinkedList<String> refAddresses, LinkedList<String> comments){
		
		String url = null;
		
			File f = new File ("plugins/connectors/googleMaps/bin/tuc/apon/googleMaps/gmaps2.html");
//			f.deleteOnExit();
			try {
				Writer output = new BufferedWriter(new FileWriter(f));
				output.write("<%@page contentType=\"text/html\" pageEncoding=\"UTF-8\"%>\n\n");
				
				output.write("<!DOCTYPE html \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n");
				output.write("\t\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n\n");
				
				output.write("\t\t<html>\n");
				output.write("\t\t\t<head>\n");
				output.write("\t\t\t\t<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>\n");
				output.write("\t\t\t\t<title>Google Maps</title>\n");
				output.write("\t\t\t\t<script src=\"http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAehEtphK7nLd2bfjGnoeamRT2yXp_ZAY8_ufC3CFXhHIE1NvwkxRt67OEH9Y01Qjh2XRhCjBABPhfYg&sensor=false\"\n");
				output.write("\t\t\t\t\ttype=\"text/javascript\"></script>\n");
				output.write("\t\t\t\t<script type=\"text/javascript\">\n");
				output.write("\t\t\t\t\tvar map = null;\n");
				output.write("\t\t\t\t\tvar geocoder = null;\n");
				output.write("\t\t\t\t\tfunction initialize() {\n");
				output.write("\t\t\t\t\t\tif (GBrowserIsCompatible()) {\n");
				output.write("\t\t\t\t\t\t\tmap = new GMap2(document.getElementById(\"map_canvas\"));\n");
				output.write("\t\t\t\t\t\t\tmap.setCenter(new GLatLng(35.517321015720384, 24.02161180973053), 13)\n");
				output.write("\t\t\t\t\t\t\tmap.setUIToDefault();\n");
				output.write("\t\t\t\t\t\t\tgeocoder = new GClientGeocoder();");
				output.write("\t\t\t\t\t\t}\n\n");
				
				output.write("\t\t\t\t\t\tvar addresses=new Array(");
				for(int i=0;i<addresses.size();i++){
					String addr = addresses.get(i);
					output.write("\""+addr+"\"");
					if(i != addresses.size()-1) output.write(",");
				}
				output.write(");\n");
				
				output.write("\t\t\t\t\t\tvar comments=new Array(");
				for(int i=0;i<addresses.size();i++){	//this is done so that comments' array 
														//is the same size with addresses array
					String comnt = "";
					if(i<comments.size())
						comnt = comments.get(i);
					output.write("\""+comnt+"\"");
					if(i != addresses.size()-1) output.write(",");
				}
				output.write(");\n");
				
				output.write("\t\t\t\t\t\tvar refAddresses=new Array(");
				for(int i=0;i<refAddresses.size();i++){
					String addr = refAddresses.get(i);
					output.write("\""+addr+"\"");
					if(i != refAddresses.size()-1) output.write(",");
				}
				output.write(");\n");
				
				output.write("\t\t\t\t\t\tvar refComments=new Array(");
				for(int i=0;i<refAddresses.size();i++){	//this is done so that comments' array 
														//is the same size with addresses array
					String comnt = "";
					output.write("\""+comnt+"\"");
					if(i != refAddresses.size()-1) output.write(",");
				}
				output.write(");\n");
				
				output.write("\t\t\t\t\t\tfor(i = 0; i < addresses.length; i++){\n");
				output.write("\t\t\t\t\t\t\tshowAddress(addresses[i],comments[i],0);\n");
				output.write("\t\t\t\t\t\t}\n\n");
				
				output.write("\t\t\t\t\t\tfor(i = 0; i < refAddresses.length; i++){\n");
				output.write("\t\t\t\t\t\t\tshowAddress(refAddresses[i],refComments[i],1);\n");
				output.write("\t\t\t\t\t\t}\n\n");
				
				output.write("\t\t\t\t\t\tfunction showAddress(address, comments,blue) {\n");
				output.write("\t\t\t\t\t\t\tif (geocoder) {\n");
				output.write("\t\t\t\t\t\t\t\tgeocoder.getLatLng(\n");
				output.write("\t\t\t\t\t\t\t\t\taddress,\n");
				output.write("\t\t\t\t\t\t\t\t\tfunction(point) {\n");
				output.write("\t\t\t\t\t\t\t\t\t\tif (!point && i < addresses.length) {\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\talert(address + \" not found\");\n");
				output.write("\t\t\t\t\t\t\t\t\t\t} else {\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tmap.setCenter(point, 13);\n\n");
				
				output.write("\t\t\t\t\t\t\t\t\t\t\tvar baseIcon = new GIcon(G_DEFAULT_ICON);\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tbaseIcon.shadow = \"http://www.google.com/mapfiles/shadow50.png\";\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tbaseIcon.iconSize = new GSize(20, 34);\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tbaseIcon.shadowSize = new GSize(37, 34);\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tbaseIcon.iconAnchor = new GPoint(9, 34);\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tbaseIcon.infoWindowAnchor = new GPoint(9, 2);\n\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tif(blue==1)\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\t\tbaseIcon.image=\"markers/marker_blue.png\";\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\telse\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\t\tbaseIcon.image=\"markers/marker.png\";");
				output.write("\t\t\t\t\t\t\t\t\t\t\tvar numberedIcon = new GIcon(baseIcon);\n");
				
				output.write("\t\t\t\t\t\t\t\t\t\t\tmarkerOptions = { icon:numberedIcon };\n");
				
				output.write("\t\t\t\t\t\t\t\t\t\t\tvar marker = new GMarker(point, markerOptions);\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tmap.addOverlay(marker);\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\tGEvent.addListener(marker, \"click\", function(){\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\t\tmarker.openInfoWindowHtml(address.replace(/\\+/g, \" \")+\"<br>\"+comments);\n");
				output.write("\t\t\t\t\t\t\t\t\t\t\t});\n");

				output.write("\t\t\t\t\t\t\t\t\t\t}\n");
				output.write("\t\t\t\t\t\t\t\t\t}\n");
				output.write("\t\t\t\t\t\t\t\t);\n");
				output.write("\t\t\t\t\t\t\t}\n");
				output.write("\t\t\t\t\t\t}\n");
				output.write("\t\t\t\t\t}\n");
				output.write("\t\t\t\t</script>\n");
				output.write("\t\t\t</head>\n\n");
				
				output.write("\t\t\t<body onload=\"initialize()\" onunload=\"GUnload()\">\n");
				output.write("\t\t\t<div id=\"map_canvas\" style=\"width: 800px; height: 600px\"></div>\n");
				output.write("\t\t</body>\n");
				output.write("\t</html>\n");
				
				output.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			url = "file://"+f.getAbsolutePath();
		
		
		if( !java.awt.Desktop.isDesktopSupported() ) {

            System.err.println( "Desktop is not supported (fatal)" );
            System.exit( 1 );
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {

            System.err.println( "Desktop doesn't support the browse action (fatal)" );
            System.exit( 1 );
        }

        try {
        	/*
        	 * if all ok, making a URI from the URL and feeding it
        	 * to the platform's default browser 
        	 */
        	java.net.URI uri = new java.net.URI(url);
            desktop.browse( uri );
        }
        catch ( Exception e ) {
            System.err.println( e.getMessage() );
            e.printStackTrace();
        }
		
	}
	
	/**
	 * This method returns the shortest driving distance between to locations in meters
	 * by using Google's Directions API
	 * 
	 * @param origin address
	 * @param destination address
	 * 
	 * @return distance in meters
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	private static long getShortestDistance(String origin, String destination) 
		throws IOException, ParserConfigurationException, SAXException{
				
		URL googleReq = new URL("http://maps.google.com/maps/api/directions/xml?origin="+origin.replace(" ", "+").replace(",", "")+
				"&destination="+destination.replace(" ", "+").replace(",", "")+"&sensor=false");
		
		URLConnection urlConnection = googleReq.openConnection();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		Document doc = db.parse(urlConnection.getInputStream());
		try{
			/*
			 * we write the response to an xml file
			 * just for supervision
			 */
			Source source = new DOMSource(doc);
			File file = new File("response.xml");
			Result result = new StreamResult(file);

			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
		} catch (TransformerException e) {
		}

		Node stNode = doc.getElementsByTagName("status").item(0);
		long dst = -1;
		if(stNode.getTextContent().equals("OK")){
			//extract the distance from the response
			NodeList routeLst = doc.getElementsByTagName("route");
			
			for(int rCtr=0;rCtr<routeLst.getLength();rCtr++){
				long routeDist = 0;
				Node route = routeLst.item(rCtr);
				
				NodeList distLst = ((Element)route).getElementsByTagName("distance");

				for(int dCtr=0;dCtr<distLst.getLength();dCtr++){
					Node distanceNode = distLst.item(dCtr);
					if(distanceNode.getParentNode().getNodeName().equals("leg")){
						NodeList distChild = ((Element)distanceNode).getElementsByTagName("value");
						for(int ch=0;ch<distChild.getLength();ch++){
							Node tmpChild = distChild.item(ch);
								routeDist += Long.parseLong(tmpChild.getTextContent());
						}
					}
				}
				if(routeDist<dst || dst==-1)
					dst=routeDist;
			} //end route
			System.out.println("Shortest distance: "+dst+" meters");
		}
		else{
			System.err.println("RESPONSE STATUS NOT OK!");	//probably over_query_limit status
		}
		
		return dst;
	}
}
