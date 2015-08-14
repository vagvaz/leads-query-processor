package eu.leads.processor.core;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import org.bson.BSONDecoder;
import org.bson.BSONEncoder;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;

import java.io.IOException;

/**
 * Created by vagvaz on 8/14/15.
 */
public class TupleWrapperBinding extends TupleBinding{
    BSONDecoder decoder = new BasicBSONDecoder();
    BSONEncoder bsonEncoder = new BasicBSONEncoder();
    @Override public Object entryToObject(TupleInput tupleInput) {
        TupleWrapper result = new TupleWrapper();
        result.setKey(tupleInput.readString());
        int size = tupleInput.readInt();
        byte[] tupleBytes = new byte[size];
        try {
            tupleInput.read(tupleBytes);
            result.setTuple((Tuple) decoder.readObject(tupleBytes));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override public void objectToEntry(Object o, TupleOutput tupleOutput) {
        TupleWrapper wrapper = (TupleWrapper)o;
        tupleOutput.writeString(wrapper.getKey());
        byte[] tupleBytes = bsonEncoder.encode(wrapper.getTuple().asBsonObject());
        tupleOutput.writeInt(tupleBytes.length);
        try {
            tupleOutput.write(tupleBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
