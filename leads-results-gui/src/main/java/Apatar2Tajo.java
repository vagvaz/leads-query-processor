import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.tajo.algebra.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class Apatar2Tajo {
    static HashMultimap<String, String> multiMap;
    //private static  Expr ApatarExpr;
    static String InputNodeId = null;
    static String OutputNodeId = null;
    private static HashMap<String, Expr> expmap = new HashMap<String, Expr>();
    private static HashMap<String, NamedExpr> AlliasedExpr = new HashMap<String, NamedExpr>();
    private static HashMap<String, Expr> GroupBynamelist = new HashMap<String, Expr>();
    private static HashMap<String, Expr> FunctionMap = new HashMap<String, Expr>();
    private static List<Expr> UnconnectedFunctionMap = new ArrayList<Expr>();

    private static HashMap<String, Element> nodesMap = new HashMap<String, Element>();
    private static HashMap<String, String> ArrowMap = new HashMap<String, String>();
    private int funtionsCount = 0;

    public static Expr xml2tajo(File xmlFile) throws JDOMException, IOException {
        Expr ApatarExpr = null;
        SAXBuilder builder = new SAXBuilder();

        Document document = (Document) builder.build(xmlFile);
        Element rootNode = document.getRootElement();
        visit_apatar_xml(rootNode);

        //Display data

        System.out.println("Arrows");
        for (Map.Entry<String, String> entry : ArrowMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

        multiMap = Multimaps.invertFrom(Multimaps.forMap(ArrowMap),
                HashMultimap.<String, String>create());

        for (Map.Entry<String, Collection<String>> entry : multiMap.asMap().entrySet()) {
            System.out.println("Original value: " + entry.getKey() + " was mapped to keys: "
                    + entry.getValue());
        }

        System.out.println("Nodes");
        for (Map.Entry<String, Element> entry : nodesMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue().getAttributeValue("nodeClass"));
        }


        return ApatarExpr = visit_project();


    }


    public static String xml2tajo_json(File xmlFile) throws JDOMException, IOException {
        return xml2tajo(xmlFile).toJson();
    }

    public static void main(String[] args) {
        String xmlWithSpecial = "";
        System.out.println(" Appatar Parsing xml");


        File xmlFile = new File(args[0]);// "QUERY 2/XML_output.aptr");//"QUERY 3/XML_output_1.aptr");// "QUERY 1/XML-OUTPUT.aptr");//+
        //
        //"/home/tr/Projects/LEADs/xml/Leuteris/Example3.aptr");


        try {
            System.out.println("Expr" + xml2tajo(xmlFile).toJson());
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }


    static void visit_apatar_xml(Element rootNode) {
        List list = rootNode.getChildren("node");
        Element node, functionNode = null;

        System.out.println("Nodes # " + list.size());
        for (int i = 0; i < list.size(); i++) {
            node = (Element) list.get(i);
            System.out.println("Node  id: " + node.getAttributeValue("id") + " "
                    + node.getAttributeValue("nodeClass"));


            if (node.getAttributeValue("nodeClass").contains("read.READNode")) {
                System.out.println("Found input node: " + node.getAttributeValue("id"));
                InputNodeId = node.getAttributeValue("id");
            }

            if (node.getAttributeValue("nodeClass").contains("output.OutputNode")) {
                System.out.println("Found output node: " + node.getAttributeValue("id"));
                OutputNodeId = node.getAttributeValue("id");
            }

            //Found Subproject
            if (node.getAttribute("subProject") != null) {
                System.out.println("\nSubproject found Reparsing");
                String xmlWithSpecial = StringEscapeUtils
                        .unescapeXml(StringEscapeUtils.unescapeXml(node
                                .getAttributeValue("subProject")));

                visit_subproject(xmlWithSpecial, node.getAttributeValue("title"));

            }


            System.out.println("Save node  " + node.getAttributeValue("nodeClass"));
            //Save nodes !!
            //if (functionNode != null){
            //    nodesMap.put(node.getAttributeValue("id"), functionNode);functionNode = null;}
            // else
            nodesMap.put(node.getAttributeValue("id"), node);

        }
        //Get arrows
        System.out.println("Found Function SubEntries: " + FunctionMap.size());

        for (Map.Entry<String, Expr> Subentry : FunctionMap.entrySet()) {
            System.out.println("Function Subentry key: " + Subentry.getKey() + " value: " + Subentry.getValue().toJson());

        }


        list = rootNode.getChildren("arrow");
        System.out.println("Arrows " + list.size());
        for (int i = 0; i < list.size(); i++) {
            node = (Element) list.get(i);
            System.out.println("Arrow id: " + node.getAttributeValue("id") + "  from: "
                    + node.getAttributeValue("begin_id") + " to: " + node.getAttributeValue("end_id"));
            ArrowMap.put(node.getAttributeValue("begin_id"), node.getAttributeValue("end_id"));
        }
    }

    static Expr visit_project() {
        Element node, subnode;
        boolean projectnodeFound = false;
        Expr projectExpr = new Projection();
        Projection Project = null;

        if (ArrowMap.get(InputNodeId).equals(OutputNodeId)) {
            List<String> from = new ArrayList<>();
            System.out.println("Select *");
            from.add(nodesMap.get(InputNodeId).getAttributeValue("title").toLowerCase());
            projectExpr = visitSelect_list(null, visitFrom_clause(from));
        } else {
            //Check if more than 1 inputs
            Expr Curr = new Projection();
            String curNodeId = InputNodeId;
            String Attrb = null;
            while ((node = nodesMap.get(curNodeId)) != null) {

                Attrb = node.getAttributeValue("nodeClass");
                System.out.println("Checking Node: " + Attrb);

                if (Attrb.contains("READNode")) {
                    List<String> from = new ArrayList<>();
                    System.out.println("READNode ");
                    from.add(nodesMap.get(InputNodeId).getAttributeValue("title").toLowerCase());
                    Curr = visitFrom_clause(from);
                } else if (Attrb.contains("OutputNode")) {
                    if (!projectnodeFound) {
                        //Fix
                        Curr = visitSelect_list_expr(UnconnectedFunctionMap.get(0), Curr);

                    }
                } else if (Attrb.contains("LimitNode")) {
                    Limit lim = null;
                    if (FunctionMap.containsKey("limit")) {
                        lim = (Limit) ((NamedExpr) FunctionMap.get("limit")).getExpr();
                        lim.setChild(Curr);
                        Curr = lim;
                    }


                } else if (Attrb.contains("FilterNode")) {
                    Expr qual = null;

                    for (Map.Entry<String, Expr> Subentry : FunctionMap.entrySet()) {
                        qual = ((NamedExpr) (Subentry.getValue())).getExpr();
                        System.out.println("Filter node Subentry Type" + qual.getType());
                        if (qual.getType() == OpType.LikePredicate) {
                            qual = ((NamedExpr) Subentry.getValue()).getChild();
                            break; //CHeck
                        }
                    }
                    Selection ret = new Selection(qual);
                    ret.setChild(Curr);


                    Curr = ret;
                } else if (Attrb.contains("GroupByNewNode")) {

                    if (GroupBynamelist.size() > 0) {
                        Expr havingCondition = null;
                        Aggregation clause = new Aggregation();
                        ArrayList<Expr> ordinaryExprs = new ArrayList<Expr>();

                        for (Map.Entry<String, Expr> Subentry : GroupBynamelist.entrySet()) {
                            System.out.println("GroupBynamelist key: " + Subentry.getKey() + " value: " + Subentry.getValue().toJson());
                            ordinaryExprs.add(((NamedExpr) Subentry.getValue()).getChild());
                        }
                        ArrayList<Aggregation.GroupElement> groups = new ArrayList<Aggregation.GroupElement>(1);
                        groups.add(new Aggregation.GroupElement(Aggregation.GroupType.OrdinaryGroup, ordinaryExprs.toArray(new Expr[ordinaryExprs.size()])));
                        int groupSize = 1;
                        clause.setGroups(groups.subList(0, groupSize).toArray(new Aggregation.GroupElement[groupSize]));
                        clause.setChild(Curr);

                        Curr = clause;

                        //FIIIIIIIIIIIIIIX IT
                        for (Map.Entry<String, Expr> Subentry : FunctionMap.entrySet()) {
                            Expr tmpexpr = ((NamedExpr) Subentry.getValue()).getExpr();
                            System.out.println("Subentry Type" + tmpexpr.getType());
                            if (tmpexpr.getType() == OpType.LikePredicate) {

                            } else if (tmpexpr.getType() == OpType.GreaterThan) {
                                havingCondition = ((NamedExpr) Subentry.getValue()).getChild();
                                Having having = new Having(havingCondition);
                                having.setChild(Curr);
                                Curr = having;
                            } else {
                                System.out.println("Unknown function?");
                            }

                        }
                    }


                } else if (Attrb.contains("JoinNode")) {
                    Join join;
                    if (multiMap.get(ArrowMap.get(InputNodeId)).size() == 2) {
                        String[] InputIds = new String[2];
                        (multiMap.get(ArrowMap.get(InputNodeId))).toArray(InputIds);
                        join = new Join(JoinType.INNER);

                        join.setRight(new Relation(nodesMap.get(InputIds[0]).getAttributeValue("title")));
                        join.setLeft(new Relation(nodesMap.get(InputIds[1]).getAttributeValue("title")));

                        subnode = node.getChild("Condition");
                        Expr right = new ColumnReferenceExpr(subnode.getAttributeValue("column1"));
                        Expr left = new ColumnReferenceExpr(subnode.getAttributeValue("column2"));
                        Expr searchCondition = new BinaryOperator(OpType.Equals, left, right);

                        join.setQual(searchCondition);
                        Curr = join;
                        //More than one input possible join
                    } else {
                        System.out.println("Triple join?");
                    }

                } else if (Attrb.contains("ProjectNode")) {
                    projectnodeFound = true;
                    Project = new Projection();

                    String TargetName = "";

                    node = node.getChild("OutputConnectionPoints");
                    node = node.getChild("ConnectionPoint");
                    node = node.getChild("tableInfo");
                    node = node.getChild("records");
                    System.out.println("ProjectNode  " + ((List<Element>) node.getChildren("com.apatar.core.Record")).size());
                    List<Element> recordslist;
                    if ((recordslist = (List<Element>) node.getChildren("com.apatar.core.Record")).size() > 0) {
                        List<String> targetnames = new ArrayList<>();
                        NamedExpr[] targets = new NamedExpr[recordslist.size()];
                        int count = 0;
                        for (Element temp : recordslist) {
                            TargetName = temp.getAttributeValue("fieldName");

                            System.out.println("Record  " + TargetName);
                            if (AlliasedExpr.containsKey(TargetName))
                                targets[count++] = AlliasedExpr.get(TargetName);
                            else
                                targets[count++] = new NamedExpr(new ColumnReferenceExpr(TargetName));
                        }
                        Project.setNamedExprs(targets);

                    }
                    //Project.setNamedExprs(from);
                    //Project.setChild(Curr);
                    //Project = visitSelect_list_expr()
                    //Curr = Project;

                } else if (Attrb.contains("OrderByNode")) {

                    Expr qual = null;

                    for (Map.Entry<String, Expr> Subentry : FunctionMap.entrySet()) {
                        qual = ((NamedExpr) (Subentry.getValue())).getExpr();
                        System.out.println("Filter node Subentry Type" + qual.getType());
                        if (qual.getType() == OpType.Sort) {
                            Sort asort = (Sort) qual;
                            asort.setChild(Curr);
                            Curr = asort;
                        }
                    }
                }

                projectExpr = Curr;
                curNodeId = ArrowMap.get(curNodeId);
            }

            if(Project!=null) {
                Project.setChild(Curr);
                projectExpr = Project;
            }
        }

        return projectExpr;
    }
    static void  test_conf(String xmlWithSpecial){
        File temp;
    try {
        // Create temp file.
        temp = File.createTempFile("pattern", ".suffix");

        // Delete temp file when program exits.
        temp.deleteOnExit();

        // Write to temp file
        BufferedWriter out = new BufferedWriter(new FileWriter(temp));
        out.write(xmlWithSpecial);
        out.close();
        System.out.println("Subproject Config");
        XMLConfiguration subxml = new XMLConfiguration(temp);

        Iterator <String> it = subxml.getKeys();
        while(it.hasNext()){
            System.out.println(it.next()+" ");
        }

    } catch (IOException e) {
    } catch (ConfigurationException e) {
        e.printStackTrace();
    }
    }

    static void visit_subproject(String xmlWithSpecial, String ParentNode) {
        Element subrootNode;
        Element subnode;
        List<Element> sublist, recordslist;
        String Attrb;
        System.out.println(xmlWithSpecial);
        test_conf( xmlWithSpecial);
        HashMap<String, String> SubArrowMap = new HashMap<String, String>();
        //HashMap<String, List<String>> SubReverseArrowMap = new HashMap<String, List<String>>();
        HashMap<String, String> SubReverseArrowMap = new HashMap<String, String>();
        if ((subrootNode = get_root(xmlWithSpecial)) != null) {


            String fieldName = null;

            HashMap<String, Element> submap = new HashMap<String, Element>();
            sublist = subrootNode.getChildren("arrow");
            System.out.println("subArrows " + sublist.size());
            for (int j = 0; j < sublist.size(); j++) {
                subnode = (Element) sublist.get(j);
                System.out.println("Arrow  from: "
                        + subnode.getAttributeValue("begin_id") + " to: " + subnode.getAttributeValue("end_id"));
                SubArrowMap.put(subnode.getAttributeValue("begin_id"), subnode.getAttributeValue("end_id"));
                //fix add list List<String>
                //Now assume 1-1 connections
                SubReverseArrowMap.put(subnode.getAttributeValue("end_id"), subnode.getAttributeValue("begin_id"));
            }

            sublist = subrootNode.getChildren("node");
            HashMap<String, Element> subnodesMap = new HashMap<String, Element>();

            for (int j = 0; j < sublist.size(); j++) {

                subnode = (Element) sublist.get(j);
                System.out.println("SubNode id: " + subnode.getAttributeValue("id")
                        + " " + subnode.getAttributeValue("nodeClass"));

                subnodesMap.put(subnode.getAttributeValue("id"), subnode);
                //nodesMap.put(node.getAttributeValue("id") + "_" + Integer.toString(j + 1), subnode);
            }


            for (Map.Entry<String, Element> entry : subnodesMap.entrySet()) {
                subnode = entry.getValue();
                fieldName = null;

                if (subnode.getAttributeValue("nodeClass").contains("ColumnNode")) {
                    fieldName = subnode.getChild("com.apatar.core.Record").getAttributeValue("fieldName");
                    fieldName.toLowerCase();
                    System.out.println("Found Column fieldName: " + fieldName);
                    if (!SubArrowMap.containsKey(subnode.getAttributeValue("id")) && !SubReverseArrowMap.containsKey(subnode.getAttributeValue("id"))) {
                        System.out.println("Not connected fieldName: " + fieldName);
                        GroupBynamelist.put(fieldName, new NamedExpr(new ColumnReferenceExpr(fieldName)));
                    }
                } else if (subnode.getAttributeValue("nodeClass").contains("FunctionNode")) {
                    //Search for arrow that are input to this node
                    Element prenode = null, nextnode = null;
                    if (SubReverseArrowMap.containsKey(subnode.getAttributeValue("id"))) {

                        prenode = subnodesMap.get(SubReverseArrowMap.get(subnode.getAttributeValue("id")));
                        fieldName = prenode.getChild("com.apatar.core.Record").getAttributeValue("fieldName");

                        fieldName.toLowerCase();
                        Expr Column;
                        if (AlliasedExpr.containsKey(fieldName))
                            Column = AlliasedExpr.get(fieldName).getExpr();
                        else
                            Column = new ColumnReferenceExpr(fieldName);
                        String alias = "";


                        NamedExpr tmpNE = new NamedExpr(new ColumnReferenceExpr(fieldName));
                        Attrb = null;

                        if ((Attrb = subnode.getAttributeValue("classFunction")) != null) {
                            NamedExpr[] targets = new NamedExpr[1];
                            targets[0] = tmpNE;
                            Expr tmpGF = null;

                            tmpNE = new NamedExpr(get_function( subnode, Column)); //named or not ?

                            //output alias
                            if (subnodesMap.containsKey(SubArrowMap.get(subnode.getAttributeValue("id")))) {
                                nextnode = subnodesMap.get(SubArrowMap.get(subnode.getAttributeValue("id")));
                                fieldName = nextnode.getChild("com.apatar.core.Record").getAttributeValue("fieldName");
                                fieldName.toLowerCase();
                                tmpNE.setAlias(fieldName);
                                AlliasedExpr.put(fieldName, tmpNE);
                            }
                            if (fieldName == null && !tmpNE.hasAlias()) { //FIX
                                //fieldName = tmpGF.getSignature() + "_" + tmpGF.getParams()[0].toString();
                                System.out.println("Artificial field name: " + fieldName);
                                tmpNE.setAlias(fieldName);
                            }
                            System.out.println("Save function   " + tmpNE.toJson());
                            FunctionMap.put(ParentNode + alias + fieldName, tmpNE);

                        }
                    } else {
                        System.out.println("Found function  no arrows " + subnode.getAttributeValue("nodeClass"));
                        //Now arrow to this function
                        if ((Attrb = subnode.getAttributeValue("classFunction")) != null) {
                            get_function(subnode);
                            // FunctionMap.put(ParentNode, tmpNE);
                        }
                    }

                }
            }

        }
    }

    static void get_function(Element subnode){
       String Attrb = subnode.getAttributeValue("classFunction");
        if (Attrb.contains("LimitFunction")) {
            subnode = subnode.getChild("com.apatar.functions.constant.LimitFunction");
            System.out.println("limit " + subnode.getAttributeValue("value"));
            //ApatarExpr = visitLimit_clause(node.getAttributeValue("value"), ApatarExpr);
            NamedExpr tmpNE = new NamedExpr(visitLimit_clause(subnode.getAttributeValue("value"), null));
            FunctionMap.put("limit", tmpNE);
        } else if (Attrb.contains("CountStarFunction")) {
            System.out.println("Count*  ");
            Expr func = new CountRowsFunctionExpr();
            UnconnectedFunctionMap.add(func);
        }
        else if (Attrb.contains("CountFunction")) {
            System.out.println("Count  ");
            Expr func = new CountRowsFunctionExpr();
            UnconnectedFunctionMap.add(func);
        }
        else if (Attrb.contains("MaxFunction")) {
            System.out.println("MaxFunction*  ");
            //Expr func = new  ();
            //UnconnectedFunctionMap.add(func);
        }else {
            System.out.println("Unknown no arrows field name: " + Attrb);
        }
    }


    static Expr get_function( Element subnode, Expr Column){
       String Attrb = subnode.getAttributeValue("classFunction");
        Expr tmpGF=null;
        if (Attrb.contains("GreaterThanValidateFunction")) {
            String numValue = subnode.getChild("com.apatar.functions.Logic.GreaterThanValidateFunction").getAttributeValue("Value");
            Expr value = new LiteralValue(numValue, LiteralValue.getLiteralType(numValue));

            tmpGF = new BinaryOperator(OpType.GreaterThan, Column, value);//new GeneralSetFunctionExpr("GTH", false, targets);
        } else if (Attrb.contains("AvgFunction")) {
            ColumnReferenceExpr[] params = new ColumnReferenceExpr[1];
            params[0] = (ColumnReferenceExpr) Column;
            tmpGF = new GeneralSetFunctionExpr("avg", false, params);
        } else if (Attrb.contains("EqualToValidateFunction")) {
            String numValue = subnode.getChild("com.apatar.functions.Logic.EqualToValidateFunction").getAttributeValue("Value");
            //check if does not contain value
            Expr value = new LiteralValue(numValue, LiteralValue.getLiteralType(numValue));

            tmpGF = new BinaryOperator(OpType.Equals, Column, value);
        }else if (Attrb.contains("ContainValidateFunction")) {
            System.err.println("Not yet implemmented");
            //ColumnReferenceExpr[] params = new ColumnReferenceExpr[1];
            //params[0] = (ColumnReferenceExpr) Column;
            //tmpGF = new GeneralSetFunctionExpr("avg", false, params);
        }else if (Attrb.contains("FindRegExpFunction")) {
            System.err.println("Not yet implemmented");
            //ColumnReferenceExpr[] params = new ColumnReferenceExpr[1];
            //params[0] = (ColumnReferenceExpr) Column;
            //tmpGF = new GeneralSetFunctionExpr("avg", false, params);
        }else if (Attrb.contains("AndValidateFunction")) {

            //ColumnReferenceExpr[] params = new ColumnReferenceExpr[1];
            //params[0] = (ColumnReferenceExpr) Column;
            //tmpGF = new GeneralSetFunctionExpr("avg", false, params);
        }else if (Attrb.contains("IsNullValidateFunction")) {
            ColumnReferenceExpr[] params = new ColumnReferenceExpr[1];
            params[0] = (ColumnReferenceExpr) Column;
            tmpGF = new GeneralSetFunctionExpr("avg", false, params);
        }else if (Attrb.contains("IsNotNullValidateFunction")) {
            //ColumnReferenceExpr[] params = new ColumnReferenceExpr[1];
            //params[0] = (ColumnReferenceExpr) Column;
            //tmpGF = new GeneralSetFunctionExpr("avg", false, params);
        }else if (Attrb.contains("GreaterOrEqualValidateFunction")) {
            String numValue = subnode.getChild("com.apatar.functions.Logic.GreaterThan").getAttributeValue("Value");
            Expr value = new LiteralValue(numValue, LiteralValue.getLiteralType(numValue));

            tmpGF = new BinaryOperator(OpType.GreaterThanOrEquals, Column, value);
        }else if (Attrb.contains("LessOrEqualValidateFunction")) {
            String numValue = subnode.getChild("com.apatar.functions.Logic.LessOrEqualValidateFunction").getAttributeValue("Value");
            Expr value = new LiteralValue(numValue, LiteralValue.getLiteralType(numValue));

            tmpGF = new BinaryOperator(OpType.LessThanOrEquals, Column, value);
        }else if (Attrb.contains("NotValidateFunction")) {
            //ColumnReferenceExpr[] params = new ColumnReferenceExpr[1];
            //params[0] = (ColumnReferenceExpr) Column;
            //tmpGF = new GeneralSetFunctionExpr("avg", false, params);
        }else if (Attrb.contains("TESTFunction")) {
            //ColumnReferenceExpr[] params = new ColumnReferenceExpr[1];
            //params[0] = (ColumnReferenceExpr) Column;
            //tmpGF = new GeneralSetFunctionExpr("avg", false, params);
        }
        else if (Attrb.contains("LikeValidateFunction")) {
            Expr pattern = new LiteralValue(subnode.getChild("com.apatar.functions.Logic.LikeValidateFunction").getAttributeValue("Value"),
                    LiteralValue.LiteralType.String);
            tmpGF = new PatternMatchPredicate(OpType.LikePredicate, false, Column, pattern);
            //alias = "like."; betteruserOpType
        } else if (Attrb.contains("DescFunction")) {
            Sort.SortSpec specs[] = new Sort.SortSpec[1];
            specs[0] = new Sort.SortSpec(Column);
            specs[0].setDescending();
            //alias = "sort."; betteruserOpType
            tmpGF = new Sort(specs);
        } else {
            System.out.println("Unknown field name");
        }
        return  tmpGF;
    }


    static Element get_root(String xml) {
        Element rootNode = null;
        InputStream inputStream = new ByteArrayInputStream(
                xml.getBytes(Charset.forName("UTF-8")));
        Document newXml = null;
        SAXBuilder testbuilder = new SAXBuilder();
        Document document;
        try {
            document = (Document) testbuilder.build(inputStream);
            rootNode = document.getRootElement();
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rootNode;


    }

    public static Selection visitWhere_clause(Expr in, Expr left, Expr right) {

        Selection ret = new Selection(new BinaryOperator(OpType.GreaterThan, left, right));
        ret.setChild(in);
        return ret;
    }

    public static Projection visitSelect_list(List<String> TargetsNames, Expr from) {
        Projection projection = new Projection();
        NamedExpr[] targets;
        if (TargetsNames == null) {
            targets = new NamedExpr[1];
            targets[0] = new NamedExpr(new QualifiedAsteriskExpr());
        } else {
            targets = new NamedExpr[TargetsNames.size()];

            for (int i = 0; i < targets.length; i++) {
                targets[i] = new NamedExpr(new ColumnReferenceExpr(TargetsNames.get(i)));
            }
        }
        projection.setNamedExprs(targets);
        projection.setChild(from);
        return projection;
    }

    public static Projection visitSelect_list_expr(Expr target, Expr from) {
        Projection projection = new Projection();
        NamedExpr[] targets = new NamedExpr[1];
        targets[0] = new NamedExpr(target);

        projection.setNamedExprs(targets);
        projection.setChild(from);
        return projection;
    }

    public static RelationList visitFrom_clause(List<String> relations_names) {
        Expr[] relations = new Expr[relations_names.size()];
        System.out.println(" Size = " + relations_names.size());
        for (int i = 0; i < relations.length; i++) {
            relations[i] = new Relation(relations_names.get(i));
        }
        return new RelationList(relations);
    }


    public static Expr visitLimit_clause(String value, Expr right) {
        Expr left = new LiteralValue(value, LiteralValue.getLiteralType(value));
        return new Limit(left);
    }

}
