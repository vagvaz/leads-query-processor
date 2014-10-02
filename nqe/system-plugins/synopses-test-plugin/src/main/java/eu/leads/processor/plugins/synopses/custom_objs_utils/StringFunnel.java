package eu.leads.processor.plugins.synopses.custom_objs_utils;

import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

import java.nio.charset.Charset;

/**
 * The Funnel interface accepts objects of a
 * certain type and sends data to a PrimitiveSink
 * instance. PrimitiveSink is an object receiving primitive
 * values. A PrimitiveSink instance will extract bytes needed
 * for hashing. Note that we are implementing Funnel as an enum,
 * which helps maintain the serialization of BloomFilter,
 * which also needs the Funnel instance to be serializable.
 * When possible, it is recommended that funnels be implemented
 * as a single-element enum to maintain serialization guarantees.
 * From the 'Getting Started with Google Guava' book
 * and the documentation of Google's Guava library.
 *
 */
public enum StringFunnel implements Funnel<String> {
    INSTANCE;
    public void funnel(String arg0, PrimitiveSink into) {
        into.putString(arg0, Charset.defaultCharset());
    }
}