package eu.leads.processor.plugins;

/**
 * Created by vagvaz on 6/5/14.
 */
public enum EventType {
    CREATED(1), MODIFIED(2), REMOVED(3);


    public static final EventType[] CREATEANDMODIFY = {CREATED, MODIFIED};
    public static final EventType[] ALL = {CREATED, MODIFIED, REMOVED};
    private int value;

    private EventType(int num) {
        value = num;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + getValue() + ")";
    }
}
