package eu.leads.processor.common;

import org.infinispan.distexec.mapreduce.Mapper;

import java.util.Properties;
import java.util.Timer;

/**
 * Created with IntelliJ IDEA.
 * User: vagvaz
 * Date: 11/4/13
 * Time: 5:58 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class LeadsMapper<kIN, vIN, kOut, vOut> implements Mapper<kIN, vIN, kOut, vOut> {
    /**
     *
     */
    private static final long serialVersionUID = -1040739216725664106L;
    protected final Properties conf;
    protected boolean isInitialized = false;

    //    protected Cache<String,String> cache;
    protected long overall;
    protected Timer timer;
    protected ProgressReport report;

    public LeadsMapper(Properties configuration) {
        this.conf = configuration;
    }

    public void initialize() {
        overall = Long.parseLong(this.conf.getProperty("workload"));
        timer = new Timer();
        report = new ProgressReport(this.getClass().toString(), 0, overall);
        timer.scheduleAtFixedRate(report, 0, 2000);

    }


    @Override
    protected void finalize() {
        report.printReport(report.getReport());
        //////////////StdOutputWriter.getInstance().println("");
        report.cancel();
        timer.cancel();
    }

    protected void progress() {
        report.tick();
    }

    protected void progress(long n) {
        report.tick(n);
    }

    protected double getProgress() {
        return report.getReport();
    }

}
