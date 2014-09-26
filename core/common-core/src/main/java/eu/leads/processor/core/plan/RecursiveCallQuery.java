package eu.leads.processor.core.plan;

/**
 * Created by vagvaz on 8/4/14.
 */
public class RecursiveCallQuery extends SpecialQuery {


    public RecursiveCallQuery(String user, String url, int depth) {
        super();
        setSpecialQueryType("rec_call");
        setUrl(url);
        setDepth(depth);
    }

    public RecursiveCallQuery(SpecialQuery specialQuery) {
        super(specialQuery.asJsonObject());
        generatePlan();
    }

    private void generatePlan() {

    }

    @Override
    public Plan getPlan() {
        return null;
    }

    @Override
    public void setPlan(Plan plan) {

    }

    public String getUrl() {
        return data.getObject("query").getString("url");
    }

    public void setUrl(String url) {
        data.getObject("query").putString("url", url);
    }

    public int getDepth() {
        return data.getObject("query").getInteger("depth");
    }

    public void setDepth(int depth) {
        data.getObject("query").putNumber("depth", depth);
    }
}
