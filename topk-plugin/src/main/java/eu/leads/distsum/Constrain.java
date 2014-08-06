package eu.leads.distsum;

/**
 * @author vagvaz
 * @author otrack
 *
 * Created by vagvaz on 7/5/14.
 * A simple constrain class with upper and lower bound
 */
public class Constrain {
  private double lowBound;
  private double upperBound;
  public Constrain(double low, double high) {
    lowBound = low;
    upperBound = high;
  }

  public boolean violates(double value){
    if(value < lowBound
               || value > upperBound){
      return true;
    }
    return false;
  }

}
