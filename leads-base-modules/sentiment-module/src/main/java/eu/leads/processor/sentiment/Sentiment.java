package eu.leads.processor.sentiment;

public class Sentiment {
    String tag;
    double value;
    
    public Sentiment() {
		// TODO Auto-generated constructor stub
	}
    
    public Sentiment(double value) {
    	setValue(value);
	}

    public String getTag() {
        return tag;
    }

    protected void setTag(String tag) {
        this.tag = tag;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
		this.value = value;
        if(value < - 0.2)
        	setTag("Negative");
        else if(value > 0.2)
        	setTag("Positive");
        else
        	setTag("Neutral");
    }

    @Override
    public String toString() {
        return "Sentiment [tag=" + tag + ", value=" + value + "]";
    }
}
