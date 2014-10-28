import com.google.common.collect.HashMultimap;
import org.apache.commons.lang.StringEscapeUtils;
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

    private static HashMap<String, Expr> ApatarTajoFilterFunctMap = new HashMap<String, Expr> ();

    private static HashMap<String, Expr> ApatarTajoAgregateFunctMap = new HashMap<String, Expr> ();
    private static HashMap<String, Sort.SortSpec> ApatarTajoSortFunctMap = new HashMap<String, Sort.SortSpec> ();

    private static HashMap<String,Expr> GroupByOther ;
    private static boolean foundHaving;
    private int funtionsCount = 0;

    public static Expr xml2tajo(File xmlFile) throws JDOMException, IOException {
        Expr ApatarExpr = null;
        SAXBuilder builder = new SAXBuilder();

        Document document = (Document) builder.build(xmlFile);
        Element rootNode = document.getRootElement();
        return visit_apatar_xml(rootNode);

//        //Display data
//
//        System.out.println("Arrows");
//        for (Map.Entry<String, String> entry : ArrowMap.entrySet()) {
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }
//
//        multiMap = Multimaps.invertFrom(Multimaps.forMap(ArrowMap),
//                HashMultimap.<String, String>create());
//
//        for (Map.Entry<String, Collection<String>> entry : multiMap.asMap().entrySet()) {
//            System.out.println("Original value: " + entry.getKey() + " was mapped to keys: "
//                    + entry.getValue());
//        }
//
//        System.out.println("Nodes");
//        for (Map.Entry<String, Element> entry : nodesMap.entrySet()) {
//            System.out.println(entry.getKey() + " : " + entry.getValue().getAttributeValue("nodeClass"));
//        }
//
//
//        return ApatarExpr = visit_project();


    }


    public static String xml2tajo_json(File xmlFile) throws JDOMException, IOException {



        return xml2tajo(xmlFile).toJson();
    }

    public static void main(String[] args) {
        init_string_maps();
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

    //initialize function recognition maps
    static void init_string_maps(){
        Expr nullArg=new  NullLiteral();
        Expr nullArgs[]=new  NullLiteral[0];
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.AndValidateFunction", new BinaryOperator(OpType.And, nullArg, nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.ContainValidateFunction",new PatternMatchPredicate(OpType.SimilarToPredicate,false,nullArg,nullArg) );
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.EqualToValidateFunction",new BinaryOperator(OpType.Equals,nullArg,nullArg) );
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.GreaterOrEqualValidateFunction",new BinaryOperator(OpType.GreaterThanOrEquals,nullArg,nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.GreaterThanValidateFunction",new BinaryOperator(OpType.GreaterThan,nullArg,nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.IsNotNullValidateFunction",new IsNullPredicate(true,nullArg));

        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.IsNullValidateFunction",new IsNullPredicate(false,nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.LessOrEqualValidateFunction",new BinaryOperator(OpType.LessThanOrEquals,nullArg,nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.LikeValidateFunction",new PatternMatchPredicate(OpType.LikePredicate,false,nullArg,nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.FindRegExpFunction", new PatternMatchPredicate(OpType.Regexp,false,nullArg,nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.NotValidateFunction",new NotExpr(nullArg));


        ApatarTajoAgregateFunctMap.put("com.apatar.functions.String.AvgFunction", new GeneralSetFunctionExpr("avg", false, nullArgs));
        ApatarTajoAgregateFunctMap.put("com.apatar.functions.String.CountFunction", new GeneralSetFunctionExpr(  "count", false, nullArgs) );//new GeneralSetFunctionExpr("avg", false, nullArgs));
        ApatarTajoAgregateFunctMap.put("com.apatar.functions.String.CountStarFunction", new CountRowsFunctionExpr());
        ApatarTajoAgregateFunctMap.put("com.apatar.functions.String.CountDistinctFunction",new GeneralSetFunctionExpr(  "count", true, nullArgs));

        ApatarTajoAgregateFunctMap.put("com.apatar.functions.String.MaxFunction", new GeneralSetFunctionExpr("max", false, nullArgs));
        ApatarTajoAgregateFunctMap.put("com.apatar.functions.String.MinFunction", new GeneralSetFunctionExpr("min", false, nullArgs));

        ColumnReferenceExpr nullColumn= new ColumnReferenceExpr("null");
        ApatarTajoSortFunctMap.put("com.apatar.functions.String.DescFunction", new  Sort.SortSpec(nullColumn,false,false));//; new GeneralSetFunctionExpr("min", false, nullArgs));
        ApatarTajoSortFunctMap.put("com.apatar.functions.String.AscFunction", new  Sort.SortSpec(nullColumn,true,false));
        System.out.println("Initializing  ApatarTajoFilterFunctMap " + ApatarTajoFilterFunctMap.size());

        GroupByOther = new HashMap<>();
    }

    static Expr visit_apatar_xml(Element rootNode) {

        Element node, functionNode = null;
        List<Element>  list=null;
        HashMap<String, String> ArrowMap = new HashMap<String, String>();
        HashMap<String, String> ReverseArrowMap = new HashMap<String, String>();
        HashMap<String, List<String>> ReverseArrowTree = new HashMap<String, List<String>>();

        if (rootNode!=null) {
            list = rootNode.getChildren("node");
            System.out.println("Nodes # " + list.size());
            //
            int nodesNum=0;
            for (int j = 0; j <  list.size(); j++)
                if(  Integer.parseInt( list.get(j).getAttributeValue("id")) > nodesNum)
                    nodesNum = Integer.parseInt(list.get(j).getAttributeValue("id"));

            System.out.println("Max Id: " + nodesNum);
            String[][] ArrowConnections    = new String[nodesNum+1][nodesNum+1];
            list = rootNode.getChildren("arrow");
            System.out.println("Arrows " + list.size());
            for (int j = 0; j < list.size(); j++) {
                node = (Element) list.get(j);
                String from,to;
                from =node.getAttributeValue("begin_id");
                to = node.getAttributeValue("end_id");
                System.out.println("Arrow "
                        + from + " -> " + to + " @ " + node.getAttributeValue("end_conn_name"));
                ArrowMap.put(from, to);

                //Store the connection name information
                ArrowConnections[Integer.parseInt(from)][Integer.parseInt(to)]=node.getAttributeValue("end_conn_name");
                //Store the reversed tree connections
                ReverseArrowMap.put(to, from);
                if(!ReverseArrowTree.containsKey(to)){
                    List<String> child = new ArrayList<>();
                    child.add(from);
                    ReverseArrowTree.put(to,child);
                }else
                    (ReverseArrowTree.get(to)).add(from);
            }


            System.out.println("TreeMode Full WorkFlow"); //Print it
            for (Map.Entry<String,  List<String>> entry : ReverseArrowTree.entrySet()) {
                List<String> C =  entry.getValue();
                System.out.println("Found: " + entry.getKey() + " -> " + C.toString() );
            }



            list = rootNode.getChildren("node");

            HashMap<String, Element> nodesMap = new HashMap<String, Element>();
            HashSet<String> endNodes = new HashSet<String>();

            for (int j = 0; j < list.size(); j++) {

                node = (Element) list.get(j);
                System.out.println("SubNode id: " + node.getAttributeValue("id") + " " + node.getAttributeValue("nodeClass")+" , "+ node.getAttributeValue("title"));
                String cId = node.getAttributeValue("id");

                nodesMap.put(cId, node);
                //Search and find end nodes
                if(!ArrowMap.containsKey(cId)){
                    //Possible an end node
                    endNodes.add(cId);
                    System.out.println("^ Possible End node");
                }
            }
            int treenum=0;

            for(String id:endNodes) {
                System.out.println("Found Tree #" + treenum++ + " starting node: " + id);
                print("", true, id, ReverseArrowTree, nodesMap);
            }

            for(String id:endNodes)
            {
                Expr target=null;
                Expr from = null;
                Projection projection = new Projection();
                NamedExpr[] targets = new NamedExpr[1];
                targets[0] = new NamedExpr(target);

                projection.setNamedExprs(targets);
                projection.setChild(from);
                //Recursive nodes traversing

                //Check End node => output node !
                Expr ret = recursiveTree(id,ReverseArrowTree,nodesMap,ArrowConnections);

                return ret;
            }
        }
        return null;

        /*


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

                Expr sub = visit_subproject(xmlWithSpecial, node.getAttributeValue("title"));
                if(sub !=null)
                System.out.println("Visited subproject: " + sub.toJson().toString());

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
        }*/
    }

    private static Expr recursiveTree(String head, HashMap<String, List<String>> nodesTree, HashMap<String, Element> nodesMap, String[][] arrowConnections) {
        Element cur = nodesMap.get(head);
        System.out.print(" ID " + head + " " + cur.getAttributeValue("title"));
        int currentID = Integer.parseInt(head);
        List<String> children = nodesTree.get(head);

        String nodeType = cur.getAttributeValue("nodeClass");
        System.out.println(" NodeType: " + nodeType);

        Expr subExpr=null;

        List<Expr> childrenExpr = new ArrayList<>();
        Expr ChildExpr = null;
        if(children!=null)
            for(String child: children) {
                ChildExpr = recursiveTree(child, nodesTree, nodesMap, arrowConnections);
                if(ChildExpr!=null)
                    childrenExpr.add(ChildExpr);
            }

        Expr ret=null;
        if (cur.getAttribute("subProject") != null) {
            System.out.println("\nSubproject found  ");
            String xmlWithSpecial = StringEscapeUtils
                    .unescapeXml(StringEscapeUtils.unescapeXml(cur
                            .getAttributeValue("subProject")));
            subExpr = visit_subproject(xmlWithSpecial, cur.getAttributeValue("title"));
            System.out.println("\n Reparsing");
//            if(sub !=null)
//                System.out.println("Visited subproject: " + sub.toJson().toString());

        }


        if(nodeType.equals("com.apatar.output.OutputNode")) {
            System.out.println("OutputNode  ");

            if(children==null) //Unconnected function node ... problem
                return null; //Error in parsing bad format
            if(ChildExpr==null)
                return null;

            if(ChildExpr.getType()==OpType.Relation){
                System.out.println("Select  * ");

                Projection projection = new Projection();
                NamedExpr[] targets;
                targets = new NamedExpr[1];
                targets[0] = new NamedExpr(new QualifiedAsteriskExpr());
                projection.setNamedExprs(targets);

                Expr[] relations = new Expr[1];
                relations[0] = ChildExpr;
                projection.setChild(new RelationList(relations));

                return projection;
            }else if(ChildExpr.getType()==OpType.Aggregation){

                if(GroupByOther.size()==1) {
                    Expr func = GroupByOther.get("CountRowsFunction");
                    System.out.println("Count  * ");
                    if(func!=null)
                    if(func.getType()==OpType.CountRowsFunction){
                        Projection projection = new Projection();
                        NamedExpr[] targets;
                        targets = new NamedExpr[1];
                        targets[0] = new NamedExpr(func);
                        projection.setNamedExprs(targets);
                        Expr[] relations = new Expr[1];
                        relations[0] = ((Aggregation) ChildExpr).getChild();
                        projection.setChild(new RelationList(relations));
                        return projection;
                    }
                }
            }
            else
                return ChildExpr;
            //return recursiveTree(children.get(0), nodesTree, nodesMap, arrowConnections);
        }else if(nodeType.equals("com.apatar.project.ProjectNode")) {

            if(ChildExpr==null)
                return null;

            ret = new Projection();
            String TargetName = "";
            Element node = cur.getChild("OutputConnectionPoints");
            node = node.getChild("ConnectionPoint");
            node = node.getChild("tableInfo");
            node = node.getChild("records");
            node = node.getChild("records");
            System.out.println("ProjectNode  " + ((List<Element>) node.getChildren("com.apatar.core.Record")).size());
            List<Element> recordslist;
            NamedExpr[] targets;

            if ((recordslist = (List<Element>) node.getChildren("com.apatar.core.Record")).size() > 0) {
                List<String> targetnames = new ArrayList<>();
                targets = new NamedExpr[recordslist.size()];
                int count = 0;
                for (Element temp : recordslist) {
                    TargetName = temp.getAttributeValue("fieldName");

                    System.out.println("Record  " + TargetName);
                    if (AlliasedExpr.containsKey(TargetName))
                        targets[count++] = AlliasedExpr.get(TargetName);
                    else
                        targets[count++] = new NamedExpr(new ColumnReferenceExpr(TargetName));
                }
                ((Projection)ret).setNamedExprs(targets);

            }
            ((Projection)ret).setChild(ChildExpr);
            return ret;
        }else if(nodeType.equals("com.apatar.limit.OrderByNode")) {
            System.out.println("OrderByNode  " );

            if(ChildExpr==null)
                return null;

            if(subExpr!=null)
                ((Sort)subExpr).setChild(ChildExpr);
            return subExpr;

        }else if(nodeType.equals("com.apatar.validate.FilterNode")) {
            System.out.println("FilterNode  " );


            if(ChildExpr==null)
                return null;
            //if(ChildExpr.getType()==OpType.Aggregation)

            if(subExpr!=null)
                if(subExpr.getType()==OpType.Filter)
                {
                    ((Selection)subExpr).setChild(ChildExpr);

                }else if(subExpr.getType()==OpType.Having){
                    if(ChildExpr.getType()==OpType.Aggregation)
                        if(((Having)subExpr).hasChild()){ //There is Filter node
                            Selection s = (Selection)((Having)subExpr).getChild(); //get the filter node
                            s.setChild(((Aggregation) ChildExpr).getChild()); // set Filters node's child Agregation's child
                            ((Aggregation) ChildExpr).setChild(s);
                            ((Having)subExpr).setChild(ChildExpr);
                        }else
                        {
                            ((Having)subExpr).setChild(ChildExpr);
                        }
                }


            return subExpr;
        }else if(nodeType.equals("com.apatar.join.JoinNode")) {
            Join join;
            System.out.println("JoinNode  " );
            if(childrenExpr.size()==2){
                join = new Join(JoinType.INNER);
                join.setRight(childrenExpr.get(0));
                join.setLeft(childrenExpr.get(1));
                Element subnode = cur.getChild("Condition");
                Expr right = new ColumnReferenceExpr(subnode.getAttributeValue("column1"));
                Expr left = new ColumnReferenceExpr(subnode.getAttributeValue("column2"));
                Expr searchCondition = new BinaryOperator(OpType.Equals, left, right);
                join.setQual(searchCondition);
                return join;
            }else{
                System.err.println("Not correct inputs for Join  " );
            }
        }else if(nodeType.equals("com.apatar.groupByNew.GroupByNewNode")) {
            System.out.println("GroupByNewNode " );
            if(ChildExpr==null)
                return null;

            if(subExpr!=null)
                ((UnaryOperator)subExpr).setChild(ChildExpr);
            return subExpr;
        }else if(nodeType.equals("com.apatar.read.READNode")) {
            System.out.println("READNode " );
            return new Relation(cur.getAttributeValue("title"));

        }else
            System.out.println("Unknown Operator  " );

        return  null;
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
        //XMLConfiguration subxml = new XMLConfiguration(temp);

        //Iterator <String> it = subxml.getKeys();
       // while(it.hasNext()){
        //    System.out.println(it.next()+" ");
        //}

    } catch (IOException e) {
    //} catch (ConfigurationException e) {
        e.printStackTrace();
    }
    }

    static Expr visit_subproject(String xmlWithSpecial, String ParentNode) {

        Element subrootNode;
        Element subnode;
        List<Element> sublist ;

        System.out.println(xmlWithSpecial);

        //test_conf( xmlWithSpecial);
        HashMap<String, String> subArrowMap = new HashMap<String, String>();
        HashMap<String, String> SubReverseArrowMap = new HashMap<String, String>();
        HashMap<String, List<String>> SubReverseArrowTree = new HashMap<String, List<String>>();

        //Parse subprojects XML
        if ((subrootNode = get_root(xmlWithSpecial)) != null) {
           String fieldName = null;

            sublist = subrootNode.getChildren("node");
            System.out.println("Analysing node: "+ ParentNode +  " Number of sub nodes: " + sublist.size());
            int nodesNum=0;
            for (int j = 0; j < sublist.size(); j++)
                if(  Integer.parseInt(sublist.get(j).getAttributeValue("id")) > nodesNum)
                    nodesNum = Integer.parseInt(sublist.get(j).getAttributeValue("id"));


            System.out.println("Max Id: " + nodesNum);
            String[][] ArrowConnections    = new String[nodesNum+1][nodesNum+1];
            sublist = subrootNode.getChildren("arrow");
            System.out.println("subArrows " + sublist.size());

            for (int j = 0; j < sublist.size(); j++) {
                subnode = (Element) sublist.get(j);
                String from,to;
                from =subnode.getAttributeValue("begin_id");
                to = subnode.getAttributeValue("end_id");
                System.out.println("Arrow "
                        + from + " -> " + to + " @ " + subnode.getAttributeValue("end_conn_name"));
                subArrowMap.put(from, to);

                //Store the connection name information
                ArrowConnections[Integer.parseInt(from)][Integer.parseInt(to)]=subnode.getAttributeValue("end_conn_name");
                //Store the reversed tree connections
                SubReverseArrowMap.put(to, from);
                if(!SubReverseArrowTree.containsKey(to)){
                    List<String> child = new ArrayList<>();
                    child.add(from);
                    SubReverseArrowTree.put(to,child);
                }else
                    (SubReverseArrowTree.get(to)).add(from);
            }

            System.out.println("TreeMode "); //Print it
            for (Map.Entry<String,  List<String>> entry : SubReverseArrowTree.entrySet()) {
                List<String> C =  entry.getValue();
                System.out.println("Found: " + entry.getKey() + " -> " + C.toString() );
            }


            sublist = subrootNode.getChildren("node");

            HashMap<String, Element> subnodesMap = new HashMap<String, Element>();
            HashSet<String> endNodes = new HashSet<String>();

            for (int j = 0; j < sublist.size(); j++) {

                subnode = (Element) sublist.get(j);
                System.out.println("SubNode id: " + subnode.getAttributeValue("id") + " " + subnode.getAttributeValue("nodeClass")+" , "+ subnode.getAttributeValue("title"));
                String cId = subnode.getAttributeValue("id");

                subnodesMap.put(cId, subnode);
                //Search and find end nodes
                if(!subArrowMap.containsKey(cId)){
                    //Possible an end node
                    endNodes.add(cId);
                    System.out.println("^ Possible End node");
                }
            }
            int treenum=0;

            for(String id:endNodes) {
                System.out.println("Found Tree #" + treenum++ + " starting node: " + id);
                print("", true, id, SubReverseArrowTree, subnodesMap);
            }
            treenum=0;

            if(ParentNode.equals("Filter")) {
                foundHaving = false;
                Expr ret = null;
                for (String id : endNodes)
                    try {
                        System.out.println("Found Tree #" + treenum++ + " starting node: " + id);
                        print("", true, id, SubReverseArrowTree, subnodesMap);
                        Expr rec = recursiveFilterFunction(id, SubReverseArrowTree, subnodesMap, ArrowConnections);

                        if (rec != null) {
                            if (foundHaving) {
                                if (ret != null)
                                    if (ret.getType() == OpType.Filter) {
                                        Selection s = (Selection) ret;
                                        ret = new Having(rec);
                                        ((Having) ret).setChild(s);
                                        return ret;
                                    }
                                ret = new Having(rec);
                            } else
                                ret = new Selection(rec);
                            System.out.println(" Found rec:" + ret.toJson().toString() + " returning only this result ");

                        } else {
                            System.out.println(" null child");
                            return null;
                        }
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }

                return ret;
            } else  if(ParentNode.equals("GroupBy")) {
                System.out.println(" GroupBy Node ");
                ArrayList<Expr> GroupByCollumns = new ArrayList<Expr>();


                for (String id : endNodes)
                    try {
                        System.out.println("Found Tree #" + treenum++ + " starting node: " + id);
                        print("", true, id, SubReverseArrowTree, subnodesMap);

                        Expr rec = recursiveGroupByFunction(id, SubReverseArrowTree, subnodesMap, ArrowConnections, subArrowMap, GroupByCollumns);
                        if (rec != null) {
                            if(rec.getType()==OpType.Target) {
                                GroupByOther.put(((NamedExpr) rec).getAlias(), ((NamedExpr) rec).getExpr());
                            }
                            if( rec.getType()==OpType.CountRowsFunction)
                                GroupByOther.put("CountRowsFunction", rec);
                            System.out.println(" Found GroupBy expr: " + rec.toJson().toString() + "  ");

                            //Selection ret = new Selection(rec);


                            //return ret;
                        } else
                            System.out.println(" null groupby");
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }

                if (GroupByCollumns.size() > 0) {
                    Aggregation clause = new Aggregation();
                    ArrayList<Aggregation.GroupElement> groups = new ArrayList<Aggregation.GroupElement>(1);
                    groups.add(new Aggregation.GroupElement(Aggregation.GroupType.OrdinaryGroup, GroupByCollumns.toArray(new Expr[GroupByCollumns.size()])));
                    int groupSize = 1;
                    clause.setGroups(groups.subList(0, groupSize).toArray(new Aggregation.GroupElement[groupSize]));

                    return clause;
                } else
                    return new Aggregation();

            }else if(ParentNode.equals("Sort")) {
                try {
                ArrayList<Sort.SortSpec> sortList = new ArrayList<Sort.SortSpec>();
                //Fix recursive read of operator if more than 1
                for(String id:endNodes)
                {
                    Sort.SortSpec c = GetSortSpec(id, SubReverseArrowTree,subnodesMap);
                    if(c!=null)
                        sortList.add(c);
                }
                if(sortList.size()>0){
                    Sort.SortSpec [] sortArray = new Sort.SortSpec[sortList.size()];
                    return new Sort((Sort.SortSpec[]) sortList.toArray(sortArray));
                }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
               return null;
            }else  if(ParentNode.equals("Project")) {
                System.out.println(" Projection Node ");
            }


            String Attrb;
            for (Map.Entry<String, Element> entry : subnodesMap.entrySet()) {
                subnode = entry.getValue();
                fieldName = null;

                if (subnode.getAttributeValue("nodeClass").contains("ColumnNode")) {
                    fieldName = subnode.getChild("com.apatar.core.Record").getAttributeValue("fieldName");
                    fieldName.toLowerCase();
                    System.out.println("Found Column fieldName: " + fieldName);
                    if (!subArrowMap.containsKey(subnode.getAttributeValue("id")) && !SubReverseArrowMap.containsKey(subnode.getAttributeValue("id"))) {
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
                            if (subnodesMap.containsKey(subArrowMap.get(subnode.getAttributeValue("id")))) {
                                nextnode = subnodesMap.get(subArrowMap.get(subnode.getAttributeValue("id")));
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
        return null;
    }
    static private void print(String prefix, boolean isTail,String head, HashMap<String, List<String>>  nodesTree, HashMap<String, Element> nodesMap ) {
        Element cur = nodesMap.get(head);
        //System.out.print(cur.toString() + " " + cur.getAttributeValue("title"));
        String name = cur.getAttributeValue("title");
        List<String> children = nodesTree.get(head);

        System.out.println(prefix + (isTail ? "└── " : "├── ") + name);
        if(children==null)
            return;
        for (int i = 0; i < children.size() - 1; i++) {
            //children.get(i).print(prefix + (isTail ? "    " : "│   "), false);
            print(prefix + (isTail ? "    " : "│   "), false,children.get(i),nodesTree,nodesMap);
        }
        if (children.size() > 0) {
            //children.get(children.size() - 1).print(prefix + (isTail ?"    " : "│   "), true);
            print(prefix + (isTail ?"    " : "│   "), true,children.get(children.size() - 1),nodesTree,nodesMap);
        }
    }

    static Sort.SortSpec GetSortSpec(String head, HashMap<String, List<String>>  nodesTree, HashMap<String, Element> nodesMap ) throws CloneNotSupportedException {
        Element cur = nodesMap.get(head);
        System.out.print(" ID " + head + " " + cur.getAttributeValue("title"));
        List<String> next = nodesTree.get(head);
        int currentID = Integer.parseInt(head);
        List<String> children = nodesTree.get(head);

        String nodeType = cur.getAttributeValue("nodeClass");
        if(nodeType.equals("com.apatar.functions.FunctionNode")) {
            if (children == null) //Unconnected function node ... problem
                return null;
            String functionType = cur.getAttributeValue("classFunction").trim();
            // System.out.print("ApatarTajoFilterFunctMap.size() = " + ApatarTajoFilterFunctMap.size());
            if(ApatarTajoSortFunctMap.containsKey(functionType)) {
                Sort.SortSpec Spec = (Sort.SortSpec) ApatarTajoSortFunctMap.get(functionType).clone();
                cur= nodesMap.get(children.get(0));
                nodeType = cur.getAttributeValue("nodeClass");
                if(nodeType.equals("com.apatar.core.ColumnNode")) {
                    Spec.setKey(new ColumnReferenceExpr(getCollumnName(cur)));
                    return Spec;
                }
            }
        }

        return null;

    }

    static Expr recursiveSortFunction(String head, HashMap<String, List<String>>  nodesTree, HashMap<String, Element> nodesMap,String [][] ArrowConnections ) throws CloneNotSupportedException {
        //Get node

        Element cur = nodesMap.get(head);
        System.out.print(" ID " + head + " " + cur.getAttributeValue("title"));
        List<String> next = nodesTree.get(head);
        int currentID = Integer.parseInt(head);
        List<String> children = nodesTree.get(head);

        String nodeType = cur.getAttributeValue("nodeClass");
        if(nodeType.equals("com.apatar.functions.FunctionNode")) {
            if(children==null) //Unconnected function node ... problem
                return null;

            String functionType = cur.getAttributeValue("classFunction").trim();
            // System.out.print("ApatarTajoFilterFunctMap.size() = " + ApatarTajoFilterFunctMap.size());
            if(ApatarTajoSortFunctMap.containsKey(functionType)){
                Object FuncExpr =  ApatarTajoSortFunctMap.get(functionType).clone();


               // if
//                if(FuncExpr.getType().getBaseClass()== BinaryOperator.class && children.size()!=2)
//                    return null;

                Expr [] childrExprArray = new Expr[children.size()];
                Expr childExpr=null;
                for (int i = 0; i < children.size(); i++) {

                      childExpr = recursiveSortFunction(children.get(i), nodesTree, nodesMap, ArrowConnections);
                    if(childExpr==null)
                        return null;
                    int from = Integer.parseInt(children.get(i));
                    String conection_input = ArrowConnections[from][currentID] ;
                    if(conection_input.equals("Input1"))
                        childrExprArray[0] = childExpr ;
                    else if(conection_input.equals("Input2"))
                        childrExprArray[1] = childExpr ;
                    else
                        childrExprArray[0] = childExpr ;
                }

                if(children.size()==2){
                    ((BinaryOperator)FuncExpr).setLeft(childrExprArray[0]);
                    ((BinaryOperator)FuncExpr).setRight(childrExprArray[1]);
                }else if(children.size()==1 ) {
                    ((Sort.SortSpec)FuncExpr).setKey((ColumnReferenceExpr)childExpr);
                }
                else
                {
                    System.err.println("Found wrong child size: " + children.size());
                    return null;
                }
                //return FuncExpr;
            }
            else{
                System.err.println("class Function unknown field name: \'" + functionType+'\'');
                return null;
            }

        }else if(nodeType.equals("com.apatar.functions.ConstantFunctionNode")) {
            String functionType = cur.getAttributeValue("classFunction");

            String  value = cur.getChild(functionType).getAttributeValue("value");
            if(value==null)
                return null;
            if(functionType.equals("com.apatar.functions.constant.NumericFunction"))
                return new LiteralValue(value, LiteralValue.getLiteralType(value));
            else  if(functionType.equals("com.apatar.functions.constant.TextFunction"))
                return new LiteralValue(value, LiteralValue.LiteralType.String);
            else
                return null;
        }else if(nodeType.equals("com.apatar.core.ColumnNode"))
            return  new ColumnReferenceExpr(getCollumnName(cur));
        else
            return null;
        return null;
    }

    static Expr recursiveFilterFunction(String head, HashMap<String, List<String>>  nodesTree, HashMap<String, Element> nodesMap,String [][] ArrowConnections ) throws CloneNotSupportedException {
        //Get node

        Element cur = nodesMap.get(head);
        System.out.print(" ID " + head + " " + cur.getAttributeValue("title"));

        int currentID = Integer.parseInt(head);
        List<String> children = nodesTree.get(head);

        String nodeType = cur.getAttributeValue("nodeClass");
        if(nodeType.equals("com.apatar.functions.FunctionNode")) {
            if(children==null) //Unconnected function node ... problem
                return null;

            String functionType = cur.getAttributeValue("classFunction").trim();
           // System.out.print("ApatarTajoFilterFunctMap.size() = " + ApatarTajoFilterFunctMap.size());
            if(ApatarTajoFilterFunctMap.containsKey(functionType)){
                Expr FuncExpr = (Expr)ApatarTajoFilterFunctMap.get(functionType).clone();

                if(FuncExpr.getType().getBaseClass()== BinaryOperator.class && children.size()!=2)
                    return null;

                Expr [] childrExprArray = new Expr[children.size()];

                for (int i = 0; i < children.size(); i++) {

                    Expr childExpr = recursiveFilterFunction(children.get(i),nodesTree,nodesMap, ArrowConnections);
                    if(childExpr==null)
                        return null;
                    int from = Integer.parseInt(children.get(i));
                    String conection_input = ArrowConnections[from][currentID] ;
                    if(conection_input.equals("Input1"))
                        childrExprArray[0] = childExpr ;
                    else if(conection_input.equals("Input2"))
                        childrExprArray[1] = childExpr ;
                    else
                        childrExprArray[0] = childExpr ;
                }

                if(children.size()==2){
                    ((BinaryOperator)FuncExpr).setLeft(childrExprArray[0]);
                    ((BinaryOperator)FuncExpr).setRight(childrExprArray[1]);
                }else if(children.size()==1 )
                    if(FuncExpr.getType().getBaseClass()== PatternMatchPredicate.class){
                        ((PatternMatchPredicate)FuncExpr).setLeft(childrExprArray[0]);
                        //pattern
                        String patternValue = cur.getChild(functionType).getAttributeValue("Value");
                        if(patternValue==null)
                            return null;
                        ((PatternMatchPredicate)FuncExpr).setRight(new LiteralValue(patternValue, LiteralValue.LiteralType.String));
                    }else//other simple operators NOT null etc
                        ((UnaryOperator)FuncExpr).setChild(childrExprArray[0]);
                else
                {
                    System.err.println("Found wrong child size: " + children.size());
                    return null;
                }
                return FuncExpr;
            }
            else{
                System.err.println("class Function unknown field name: \'" + functionType+'\'');
                return null;
            }

        }else if(nodeType.equals("com.apatar.functions.ConstantFunctionNode")) {
            String functionType = cur.getAttributeValue("classFunction");

            String  value = cur.getChild(functionType).getAttributeValue("value");
            if(value==null)
                return null;
            if(functionType.equals("com.apatar.functions.constant.NumericFunction"))
                return new LiteralValue(value, LiteralValue.getLiteralType(value));
            else  if(functionType.equals("com.apatar.functions.constant.TextFunction"))
                return new LiteralValue(value, LiteralValue.LiteralType.String);
            else
                return null;
        }else if(nodeType.equals("com.apatar.core.ColumnNode")){
            String collumnName = getCollumnName(cur);
            if(GroupByOther.containsKey(collumnName))//Having possible
            {
                foundHaving = true;
                return GroupByOther.get(collumnName);
            }else
                return  new ColumnReferenceExpr(collumnName);
        }
        else
            return null;

    }

    public static String getCollumnName(Element node){

        String title = node.getAttributeValue("title");
        if(title!=null)
            if(title.contains(".")) {
                String[] s = title.split("\\.", 2);
                title=s[1];

            }
        return title;
    }


    static Expr recursiveGroupByFunction(String head, HashMap<String, List<String>> nodesTree, HashMap<String, Element> nodesMap, String[][] ArrowConnections, HashMap<String, String> subArrowMap, ArrayList<Expr> groupByCollumns) throws CloneNotSupportedException {
        //Get node

        Element cur = nodesMap.get(head);
        System.out.print(" ID " + head + " " + cur.getAttributeValue("title"));
        List<String> next = nodesTree.get(head);
        int currentID = Integer.parseInt(head);
        List<String> children = nodesTree.get(head);

        String nodeType = cur.getAttributeValue("nodeClass");
        if(nodeType.equals("com.apatar.functions.FunctionNode")) {


            String functionType = cur.getAttributeValue("classFunction").trim();
            // System.out.print("ApatarTajoFilterFunctMap.size() = " + ApatarTajoFilterFunctMap.size());
            if(ApatarTajoFilterFunctMap.containsKey(functionType)){
                if(children==null) //Unconnected function node ... problem
                    return null;
                System.out.print("Found filter function map: " + functionType);

                Expr FuncExpr = (Expr)ApatarTajoFilterFunctMap.get(functionType).clone();

                if(FuncExpr.getType().getBaseClass()== BinaryOperator.class && children.size()!=2)
                    return null;

                Expr [] childrExprArray = new Expr[children.size()];

                for (int i = 0; i < children.size(); i++) {

                    Expr childExpr = recursiveGroupByFunction(children.get(i), nodesTree, nodesMap, ArrowConnections, subArrowMap, groupByCollumns);
                    if(childExpr==null)
                        return null;
                    int from = Integer.parseInt(children.get(i));
                    String conection_input = ArrowConnections[from][currentID] ;
                    if(conection_input.equals("Input1"))
                        childrExprArray[0] = childExpr ;
                    else if(conection_input.equals("Input2"))
                        childrExprArray[1] = childExpr ;
                    else
                        childrExprArray[0] = childExpr ;
                }

                if(children.size()==2){
                    ((BinaryOperator)FuncExpr).setLeft(childrExprArray[0]);
                    ((BinaryOperator)FuncExpr).setRight(childrExprArray[1]);
                }else if(children.size()==1 )
                    if(FuncExpr.getType().getBaseClass()== PatternMatchPredicate.class){
                        ((PatternMatchPredicate)FuncExpr).setLeft(childrExprArray[0]);
                        //pattern
                        String patternValue = cur.getChild(functionType).getAttributeValue("Value");
                        if(patternValue==null)
                            return null;
                        ((PatternMatchPredicate)FuncExpr).setRight(new LiteralValue(patternValue, LiteralValue.LiteralType.String));
                    }else//other simple operators NOT null etc
                        ((UnaryOperator)FuncExpr).setChild(childrExprArray[0]);
                else
                {
                    System.err.println("Found wrong child size: " + children.size());
                    return null;
                }
                return FuncExpr;
            }
            if(ApatarTajoAgregateFunctMap.containsKey(functionType)) {
                System.out.print("Found filter function map: " + functionType);
                Expr FuncExpr = (Expr)ApatarTajoAgregateFunctMap.get(functionType).clone();
                if(FuncExpr.getType()==OpType.CountRowsFunction)
                    return FuncExpr;
                if(children==null) //Unconnected function node ... problem
                    return null;
                if(children.size()==1) {
                    Expr childExpr = recursiveGroupByFunction(children.get(0), nodesTree, nodesMap, ArrowConnections, subArrowMap, groupByCollumns);

                    if (childExpr != null) {
                        Expr [] params = new Expr[1];
                        params[0] = childExpr;
                        ((FunctionExpr) FuncExpr).setParams(params);
                        return FuncExpr;
                    }

                }
                return null;

            }else{
                System.err.println("class Function unknown field name: \'" + functionType+'\'');
                return null;
            }

        }else if(nodeType.equals("com.apatar.functions.ConstantFunctionNode")) {
            String functionType = cur.getAttributeValue("classFunction");

            String  value = cur.getChild(functionType).getAttributeValue("value");
            if(value==null)
                return null;
            if(functionType.equals("com.apatar.functions.constant.NumericFunction"))
                return new LiteralValue(value, LiteralValue.getLiteralType(value));
            else  if(functionType.equals("com.apatar.functions.constant.TextFunction"))
                return new LiteralValue(value, LiteralValue.LiteralType.String);
            else
                return null;

        }else if(nodeType.equals("com.apatar.core.ColumnNode")) {//Groupby
            //check if the node is input node
            String collumnName = getCollumnName(cur);

            if(!subArrowMap.containsKey(head) && cur.getAttributeValue("connectionName").equals("input") ) {
                groupByCollumns.add(new ColumnReferenceExpr(collumnName));
            }

            //if it is output node -> to be saved for later use
            if(children!=null &&  cur.getAttributeValue("connectionName").equals("output"))

                if(children.size()==1) {
                    Expr childExpr = recursiveGroupByFunction(children.get(0), nodesTree, nodesMap, ArrowConnections, subArrowMap, groupByCollumns);
                    if(childExpr!=null)
                        return new NamedExpr(childExpr, collumnName);//cur.getAttributeValue("title"));
                }

            //if it is the input node
            if(children==null && subArrowMap.containsKey(head)) {
                return new ColumnReferenceExpr(collumnName);
            }
        }

        return null;
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
            String numValue = subnode.getChild("").getAttributeValue("Value");
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
