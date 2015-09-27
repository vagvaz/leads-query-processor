import org.hibernate.search.annotations.*;

import java.io.Serializable;

/**
 * Created by trs on 30/8/2015.
 */
@Indexed
class Element2 implements Serializable {

    public Element2(float attributeValue) {
        this.attributeValue = attributeValue;
    }
    @Field(index= Index.NO, analyze= Analyze.NO, store= Store.YES)
    private String keyName;
    @Field(index = Index.YES, analyze = Analyze.NO, store = Store.YES) @NumericField
    private float attributeValue;
    public String toString(){
        return Float.toString(attributeValue);
    }
}
