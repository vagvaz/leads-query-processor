package eu.leads.processor.system;

/**
 * Created by vagvaz on 8/21/14.
 */
public class LeadsProcessorBootstrapperTest {
    public static void main(String[] args) {
        String xmlConfiguration = "/tmp/leads-processor3.xml"; //boot-configuration.xml
        LeadsProcessorBootstrapper2.main(new String[] {xmlConfiguration});
    }
}
