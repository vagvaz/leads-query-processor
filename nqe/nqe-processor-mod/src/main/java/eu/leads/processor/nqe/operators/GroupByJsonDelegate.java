package eu.leads.processor.nqe.operators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.StdConverter;

/**
 * Created with IntelliJ IDEA.
 * User: vagvaz
 * Date: 11/3/13
 * Time: 9:49 PM
 * To change this template use File | Settings | File Templates.
 */
//Class converting json tree node to groupby



public class GroupByJsonDelegate extends StdConverter<JsonNode, GroupByOperator> {
    @Override
    public GroupByOperator convert(JsonNode value) {
        return null;
    }
//    @Override
//    public GroupByOperator convert(JsonNode jsonNode) {
//        String name = jsonNode.path("name").asText();
//        String output = jsonNode.path("output").asText();
//        ArrayList<Column> columns = new ArrayList<Column>();
//        ObjectMapper mapper = new ObjectMapper();
//        Iterator<JsonNode> iterator = jsonNode.path("columns").elements();
//        while (iterator.hasNext()) {
//            JsonNode col = iterator.next();
//            try {
//                Column c = mapper.readValue(col.toString(), Column.class);
//                columns.add(c);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        ArrayList<Function> functions = new ArrayList<Function>();
//        iterator = jsonNode.path("functions").elements();
//        while (iterator.hasNext()) {
//            JsonNode func = iterator.next();
//            Function f = SQLUtils.extractFunction(func);
//            functions.add(f);
//        }
//        List<String> sources = null;
//        NodeStatus status = NodeStatus.PENDING;
//
//        GroupByOperator result = new GroupByOperator(name, output, columns, functions);
//        try {
////            status = mapper.readValue(jsonNode.path("status").toString(), NodeStatus.class);
//            sources = mapper.readValue(jsonNode.path("sources").toString(), new TypeReference<List<String>>() {
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        result.setSources(sources);
//        return result;
//    }
}
