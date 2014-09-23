package eu.leads.processor.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.infinispan.jmx.annotations.DataType;
import org.vertx.java.core.json.JsonObject;

import java.io.IOException;
import java.util.*;

public class Tuple extends DataType {

    public Tuple(String value) {
            this.data = new JsonObject(value);

    }

    public Tuple(Tuple tl, Tuple tr, ArrayList<String> ignoreColumns) {

        try {
            JsonNode foo = mapper.readTree(tl.asString());
            this.root = (ObjectNode) foo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String field : ignoreColumns) {
            if (root.has(field))
                root.remove(field);
        }
        Set<String> fields = tr.getFieldSet();
        for (String field : fields) {
            if (!ignoreColumns.contains(field)) {
                this.setAttribute(field, tr.getAttribute(field));
            }
        }
        fields.clear();
    }

    public String asString() {
        return root.toString();
    }

    /**
     * Getter for property 'fieldSet'.
     *
     * @return Value for property 'fieldSet'.
     */
    public Set<String> getFieldSet() {
        HashSet<String> result = new HashSet<String>();
        Iterator<String> iterator = root.fieldNames();

        while (iterator.hasNext()) {
            String field = iterator.next();
            result.add(field);
        }
        return result;
    }

    public void setAttribute(String attributeName, String value) {
        root.put(attributeName, value);
    }

    public String getAttribute(String column) {
        return root.path(column).asText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return root.toString();
    }

    public void keepOnly(List<String> columns) {
        Iterator<String> fields = root.fieldNames();

        ArrayList<String> toRemove = new ArrayList<String>();
        while (fields.hasNext()) {
            String field = fields.next();
            if (!columns.contains(field)) {
                toRemove.add(field);
            }
        }
        for (String f : toRemove)
            root.remove(f);
        toRemove.clear();
        toRemove = null;
    }

    public void removeAtrributes(List<String> columns) {
        root.remove(columns);
    }

    public String toPresentString() {
        Iterator<String> iterator = root.fieldNames();
        StringBuilder builder = new StringBuilder();
        while (iterator.hasNext()) {
            String field = iterator.next();
            builder.append(getAttribute(field) + ",");
        }
        String result = builder.toString();
        builder = null;
        return result.substring(0, result.length() - 1);
    }

    /**
     * Getter for property 'fieldNames'.
     *
     * @return Value for property 'fieldNames'.
     */
    public String getFieldNames() {
        Iterator<String> iterator = root.fieldNames();
        StringBuilder builder = new StringBuilder();
        while (iterator.hasNext()) {
            String field = iterator.next();
            builder.append(field + ",");
        }
        String result = builder.toString();
        builder = null;
        return result.substring(0, result.length() - 1);
    }

    public boolean hasField(String attribute) {
        return this.root.has(attribute);
    }
}
