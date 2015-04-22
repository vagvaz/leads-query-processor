package eu.leads.processor.core;

import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.commons.io.ByteBufferImpl;
import org.infinispan.commons.marshall.BufferSizePredictor;
import org.infinispan.commons.marshall.Marshaller;

import java.io.*;
import java.util.Arrays;

/**
 * Created by tr on 22/4/2015.
 */
public class TupleMarshaller implements Marshaller {
    @Override
    public byte[] objectToByteBuffer(Object obj, int estimatedSize) throws IOException, InterruptedException {
        return objectToByteBuffer(obj);
    }

    @Override
    public byte[] objectToByteBuffer(Object obj) throws IOException, InterruptedException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        return baos.toByteArray();
    }

    @Override
    public Object objectFromByteBuffer(byte[] buf) throws IOException, ClassNotFoundException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IOException("cannot unmarshall");
    }

    @Override
    public Object objectFromByteBuffer(byte[] buf, int offset, int length) throws IOException, ClassNotFoundException {
        if (offset!=0||length!=buf.length) {
            objectFromByteBuffer(Arrays.copyOfRange(buf, offset, length));
        }
        return objectFromByteBuffer(buf);
    }

    @Override
    public ByteBuffer objectToBuffer(Object o) throws IOException, InterruptedException {
        byte[] buf = objectToByteBuffer(o);
        return new ByteBufferImpl(buf, 0, buf.length);
    }

    @Override
    public boolean isMarshallable(Object o) throws Exception {
        return (o instanceof Tuple) ||  (o instanceof Serializable);
    }

    @Override
    public BufferSizePredictor getBufferSizePredictor(Object o) {
        return null;
    }
}
