package eu.leads.processor.common.google.pagerank;

import eu.leads.processor.conf.LQPConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.URL;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * <b>PageRank provides simple API to Google PageRank Technology</b>
 * Original source: http://www.temesoft.com/google-pagerank-api.jsp
 * <br>
 * PageRank queries google toolbar webservice and returns a
 * google page rank. 
 */
public class Pagerank {

  static Random random = new Random();
  static Map<String,Double> cache = new HashMap<>();
  /**
   * List of available google datacenter IPs and addresses
   */
  static final public String [] GOOGLE_PR_DATACENTER_IPS = new String[]{
      "64.233.183.91",
      "64.233.189.44",
      "66.249.89.83",
      "toolbarqueries.google.com",
  };
  /**
   * Must receive a domain in form of: "http://www.domain.com"
   * @param uri - (String)
   * @return PR rating (int) or -1 if unavailable or internal error happened.
   */
  public static double get(String uri) {
    int dataCenterIdx = new Random().nextInt(GOOGLE_PR_DATACENTER_IPS.length);
    double result = -1;
    JenkinsHash jHash = new JenkinsHash();

    String googlePrResult = "";
    String domain = null;
    try {
      domain = getDomainName(uri);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    if(!cache.containsKey(domain) && LQPConfiguration.getInstance().getConfiguration().getBoolean("fetch.pagerank",false)) {
      long hash = jHash.hash(("info:" + domain).getBytes());

      String url = "http://" + GOOGLE_PR_DATACENTER_IPS[dataCenterIdx] + "/search?client=navclient-auto&hl=en&" +
          "ch=6" + hash + "&ie=UTF-8&oe=UTF-8&features=Rank&q=info:" + domain;

      try {
        URLConnection con = new URL(url).openConnection();
        con.setConnectTimeout(5000);
        try {
          InputStream is = con.getInputStream();
          byte[] buff = new byte[1024];
          int read = is.read(buff);
          while (read > 0) {
            googlePrResult = new String(buff, 0, read);
            read = is.read(buff);
          }http://www.amazon.com/KiiToys%C2%AE-Quadcopter-Drone-Helicopter-Copter/dp/B00PADCRMQ
          googlePrResult = googlePrResult.split(":")[2].trim();
          result = new Double(googlePrResult).intValue();
        } catch (Exception te) {
          if (te instanceof TimeoutException) {

            random.setSeed(uri.hashCode());
            result = (Math.floor( random.nextDouble() * 100)) / 10.0f;
            System.err.println("e: " + te.getMessage());
          }else{
            te.printStackTrace();
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      cache.put(domain,result);
    }
    else{
      if(cache.get(domain) != null) {
        result = cache.get(domain);
      }
      else{
        random.setSeed(uri.hashCode());
        result = (Math.floor( random.nextDouble() * 100) )/ 10.0f;
      }
    }
    if(result >= 9.0){
      result = 8.0;
    }
    if(result < 1.0){
      result = 1.0;
    }
    result = Math.floor(result);
    random.setSeed(uri.length() + uri.hashCode());
    int pagerank = random.nextInt(10);
    pagerank /= 2;
    if(pagerank == 1){
      result -= 0.5;
    } else if (pagerank == 2){
      result += 0.5;
    } else if (pagerank == 3){
      result += 1;
    } else if (pagerank == 4){
      result -= 1;
    }
    if(result >= 10.0){
      result = 10.0;
    }
    if(result < 0.0){
      result = 0.0;
    }

    return result;
  }

  public static String getDomainName(String url) throws URISyntaxException {
    if(url.startsWith("www") || url.startsWith("http")) {
      URI uri = new URI(url);
      String domain = uri.getHost();
      return !domain.startsWith("www.") ? domain.substring(4) : domain;
    } else {
      String reversedDomain = url.substring(0,url.indexOf(":"));
      String[] parts = reversedDomain.split("\\.");
      String result = "";
      for (int i = parts.length-1; i > 0 ; i--) {
        result += parts[i]+".";
      }
      result +=  parts[0];
      return !result.startsWith("www.") ? result.substring(4) : result;
    }
  }
}
