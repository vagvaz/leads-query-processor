import org.hibernate.search.annotations.*;

import java.io.Serializable;

/**
 * Created by trs on 30/8/2015.
 */
@Indexed
class Element implements Serializable {

    public Element(String attributeValue) {
        this.attributeValue = attributeValue;
    }
    @Field(index= Index.NO, analyze= Analyze.NO, store= Store.YES)
    private String keyName;
    @Field(index = Index.YES, analyze = Analyze.NO, store = Store.YES)
    private String attributeValue;
}
