import eu.leads.processor.common.utils.HDFSUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tajo.algebra.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

public class Apatar2Tajo {

    private static HDFSUtils fs = null;


    private static HashMap<String, Expr> ApatarTajoFilterFunctMap = new HashMap<String, Expr>();

    private static HashMap<String, Expr> ApatarTajoAgregateFunctMap = new HashMap<String, Expr>();
    private static HashMap<String, Sort.SortSpec> ApatarTajoSortFunctMap = new HashMap<String, Sort.SortSpec>();

    private static HashMap<String, Expr> GroupByOther = new HashMap<String, Expr>();
    private static boolean foundHaving;
    private int funtionsCount = 0;

    public static Expr xml2tajo(File xmlFile) throws JDOMException, IOException {

        SAXBuilder builder = new SAXBuilder();

        Document document = (Document) builder.build(xmlFile);
        Element rootNode = document.getRootElement();
        return visit_apatar_xml(rootNode);
    }


    public static String xml2tajo_json(File xmlFile) throws JDOMException, IOException {

        return xml2tajo(xmlFile).toJson();
    }

    public static void main(String[] args) {
        init_string_maps();
        System.out.println(" Appatar Parsing xml");

        File xmlFile = new File(args[0]);

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
    static void init_string_maps() {
        Expr nullArg = new NullLiteral();
        Expr nullArgs[] = new NullLiteral[0];
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.AndValidateFunction", new BinaryOperator(OpType.And, nullArg, nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.ContainValidateFunction", new PatternMatchPredicate(OpType.SimilarToPredicate, false, nullArg, nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.EqualToValidateFunction", new BinaryOperator(OpType.Equals, nullArg, nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.GreaterOrEqualValidateFunction", new BinaryOperator(OpType.GreaterThanOrEquals, nullArg, nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.GreaterThanValidateFunction", new BinaryOperator(OpType.GreaterThan, nullArg, nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.IsNotNullValidateFunction", new IsNullPredicate(true, nullArg));

        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.IsNullValidateFunction", new IsNullPredicate(false, nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.LessOrEqualValidateFunction", new BinaryOperator(OpType.LessThanOrEquals, nullArg, nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.LikeValidateFunction", new PatternMatchPredicate(OpType.LikePredicate, false, nullArg, nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.FindRegExpFunction", new PatternMatchPredicate(OpType.Regexp, false, nullArg, nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.NotValidateFunction", new NotExpr(nullArg));
        ApatarTajoFilterFunctMap.put("com.apatar.functions.Logic.LessThanValidateFunction", new BinaryOperator(OpType.LessThan, nullArg, nullArg));


        ApatarTajoAgregateFunctMap.put("com.apatar.functions.String.AvgFunction", new GeneralSetFunctionExpr("avg", false, nullArgs));
        ApatarTajoAgregateFunctMap.put("com.apatar.functions.String.CountFunction", new GeneralSetFunctionExpr("count", false, nullArgs));//new GeneralSetFunctionExpr("avg", false, nullArgs));
        ApatarTajoAgregateFunctMap.put("com.apatar.functions.String.CountStarFunction", new CountRowsFunctionExpr());
        ApatarTajoAgregateFunctMap.put("com.apatar.functions.String.CountDistinctFunction", new GeneralSetFunctionExpr("count", true, nullArgs));

        ApatarTajoAgregateFunctMap.put("com.apatar.functions.String.MaxFunction", new GeneralSetFunctionExpr("max", false, nullArgs));
        ApatarTajoAgregateFunctMap.put("com.apatar.functions.String.MinFunction", new GeneralSetFunctionExpr("min", false, nullArgs));


        ColumnReferenceExpr nullColumn = CreateColumnReference("null");
        ApatarTajoSortFunctMap.put("com.apatar.functions.String.DescFunction", new Sort.SortSpec(nullColumn, false, false));//; new GeneralSetFunctionExpr("min", false, nullArgs));
        ApatarTajoSortFunctMap.put("com.apatar.functions.String.AscFunction", new Sort.SortSpec(nullColumn, true, false));
        System.out.println("Initializing  ApatarTajoFilterFunctMap " + ApatarTajoFilterFunctMap.size());


    }

    static Expr visit_apatar_xml(Element rootNode) {

        Element node, functionNode = null;
        List<Element> list = null;
        HashMap<String, String> ArrowMap = new HashMap<String, String>();
        HashMap<String, String> ReverseArrowMap = new HashMap<String, String>();
        HashMap<String, List<String>> ReverseArrowTree = new HashMap<String, List<String>>();

        if (rootNode != null) {
            list = rootNode.getChildren("node");
            System.out.println("Nodes # " + list.size());
            //
            int nodesNum = 0;
            for (int j = 0; j < list.size(); j++)
                if (Integer.parseInt(list.get(j).getAttributeValue("id")) > nodesNum)
                    nodesNum = Integer.parseInt(list.get(j).getAttributeValue("id"));

            System.out.println("Max Id: " + nodesNum);
            String[][] ArrowConnections = new String[nodesNum + 1][nodesNum + 1];
            list = rootNode.getChildren("arrow");
            System.out.println("Arrows " + list.size());
            for (int j = 0; j < list.size(); j++) {
                node = (Element) list.get(j);
                String from, to;
                from = node.getAttributeValue("begin_id");
                to = node.getAttributeValue("end_id");
                System.out.println("Arrow "
                        + from + " -> " + to + " @ " + node.getAttributeValue("end_conn_name"));
                ArrowMap.put(from, to);

                //Store the connection name information
                ArrowConnections[Integer.parseInt(from)][Integer.parseInt(to)] = node.getAttributeValue("end_conn_name");
                //Store the reversed tree connections
                ReverseArrowMap.put(to, from);
                if (!ReverseArrowTree.containsKey(to)) {
                    List<String> child = new ArrayList<>();
                    child.add(from);
                    ReverseArrowTree.put(to, child);
                } else
                    (ReverseArrowTree.get(to)).add(from);
            }


            System.out.println("TreeMode Full WorkFlow"); //Print it
            for (Map.Entry<String, List<String>> entry : ReverseArrowTree.entrySet()) {
                List<String> C = entry.getValue();
                System.out.println("Found: " + entry.getKey() + " -> " + C.toString());
            }


            list = rootNode.getChildren("node");

            HashMap<String, Element> nodesMap = new HashMap<String, Element>();
            HashSet<String> endNodes = new HashSet<String>();

            for (int j = 0; j < list.size(); j++) {

                node = (Element) list.get(j);
                System.out.println("SubNode id: " + node.getAttributeValue("id") + " " + node.getAttributeValue("nodeClass") + " , " + node.getAttributeValue("title"));
                String cId = node.getAttributeValue("id");

                nodesMap.put(cId, node);
                //Search and find end nodes
                if (!ArrowMap.containsKey(cId)) {
                    //Possible an end node
                    endNodes.add(cId);
                    System.out.println("^ Possible End node");
                }
            }
            int treenum = 0;

            for (String id : endNodes) {
                System.out.println("Found Tree #" + treenum++ + " starting node: " + id);
                print("", true, id, ReverseArrowTree, nodesMap);
            }

            for (String id : endNodes) {
                //Check End node => output node !
                System.out.println("Search for Projection node result:  ");
                String projecID = FindnRaiseNode(id, ReverseArrowTree, nodesMap, "com.apatar.project.ProjectNode");
//                if(projecID!=null)
//                    id = projecID;
                print("", true, id, ReverseArrowTree, nodesMap);
                String MR = id;

                while(MR!=null){

                    System.out.println("Search for MapReduceNode node starting from node: "+ MR + " result: ");

                    MR = FindnRaiseNode(MR, ReverseArrowTree, nodesMap, "com.apatar.mapReduce.MapReduceNode");
                    print("", true, id, ReverseArrowTree, nodesMap);
//                    List<String> children = ReverseArrowTree.get(MR);
//                    if(children!=null) {
//                        MR = children.get(0);
//                    }else
//                        break;

                }

                //print("", true, id, ReverseArrowTree, nodesMap);

                //Recursive nodes traversing
                Expr ret = recursiveTree(id, ReverseArrowTree, nodesMap, ArrowConnections);

                //Select everything
                if (projecID == null && ret != null && ret.getType() != OpType.Projection) {
                    System.out.println("Select  * ");
                    Projection projection = new Projection();
                    NamedExpr[] targets;
                    targets = new NamedExpr[1];
                    targets[0] = new NamedExpr(new QualifiedAsteriskExpr());
                    projection.setNamedExprs(targets);

                    Expr[] relations = new Expr[1];

                    if (ret.getType() == OpType.Relation) {
                        relations[0] = ret;
                        projection.setChild(new RelationList(relations));
                        ret = projection;
                    } else if (ret.getType() == OpType.Filter) {
                        relations[0] = ((Selection) ret).getChild();
                        projection.setChild(relations[0]);
                        ((UnaryOperator) ret).setChild(projection);
                    } else {
                        relations[0] = ((Selection) ret).getChild();
                        projection.setChild(new RelationList(relations));
                        ((Selection) ret).setChild(projection);
                    }
                    //TODO Check again !

                }
                return ret;
            }
        }
        return null;

    }

    //Use only in Unary operators
    private static String FindnRaiseNode(String head, HashMap<String, List<String>> nodesTree, HashMap<String, Element> nodesMap, String SearchClass) {

        //Traverse the tree
        //Search for projection node
        Element cur = nodesMap.get(head);
        String searchNodeId = null;
        List<String> children = nodesTree.get(head);
        String nodeType = cur.getAttributeValue("nodeClass");
        boolean alreadyPushed = false;

        if (children != null) {
            for (String child : children) {
                Element childElement = nodesMap.get(child);
                String childNodeType = childElement.getAttributeValue("nodeClass");

                if (childNodeType.equals(SearchClass)) {
                    System.out.println("Found node " + SearchClass);
                    searchNodeId = child; //Found

                    List<String> childrenNext = nodesTree.get(child);
                    if (childrenNext != null) {
                        if (childrenNext.size() > 1) {
                            System.err.println("Error cannot raise a binary operator");
                            return null;
                        }

                        if (!(nodeType.equals("com.apatar.output.OutputNode") || nodeType.equals(SearchClass))) {
                            nodesTree.remove(head);
                            nodesTree.remove(child);
                            nodesTree.put(head, childrenNext);
                            //remove the child and return so another one will insert it
                        } else {
                            System.out.println(SearchClass + " node @ top ! *");
                            return searchNodeId;
                        }
                        break;
                    } else {
                        System.err.println("Found " + SearchClass + " node with no children error");
                    }
                }
                searchNodeId = FindnRaiseNode(child, nodesTree, nodesMap, SearchClass);
            }
        }

        if ((nodeType.equals("com.apatar.output.OutputNode")|| nodeType.equals(SearchClass))&& searchNodeId != null && !alreadyPushed) {
            nodesTree.remove(head);
            List<String> childrenNew = new ArrayList<>();
            childrenNew.add(searchNodeId);
            nodesTree.put(head, childrenNew);
            nodesTree.put(searchNodeId, children);
            System.out.println(SearchClass + " node @ top ! ");
        }


        return searchNodeId;

    }

    private static Expr recursiveTree(String head, HashMap<String, List<String>> nodesTree, HashMap<String, Element> nodesMap, String[][] arrowConnections) {
        Element cur = nodesMap.get(head);
        System.out.print(" ID " + head + " " + cur.getAttributeValue("title"));
        int currentID = Integer.parseInt(head);
        List<String> children = nodesTree.get(head);

        String nodeType = cur.getAttributeValue("nodeClass");
        System.out.println(" NodeType: " + nodeType);

        Expr subExpr = null;

        List<Expr> childrenExpr = new ArrayList<>();
        Expr ChildExpr = null;
        if (children != null)
            for (String child : children) {
                ChildExpr = recursiveTree(child, nodesTree, nodesMap, arrowConnections);
                if (ChildExpr != null)
                    childrenExpr.add(ChildExpr);
            }

        Expr ret = null;
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


        if (nodeType.equals("com.apatar.output.OutputNode")) {
            System.out.println("OutputNode  ");

            if (children == null) //Unconnected function node ... problem
                return null; //Error in parsing bad format
            if (ChildExpr == null)
                return null;

            if (ChildExpr.getType() == OpType.RelationList) {
                System.out.println("Select  * ");

                Projection projection = new Projection();
                NamedExpr[] targets;
                targets = new NamedExpr[1];
                targets[0] = new NamedExpr(new QualifiedAsteriskExpr());
                projection.setNamedExprs(targets);


                projection.setChild(ChildExpr);

                return projection;
            } else if (ChildExpr.getType() == OpType.Aggregation) {
                ChildExpr = ((Aggregation) ChildExpr).getChild();
                if (GroupByOther.size() == 1) {
                    Expr func = GroupByOther.get("CountRowsFunction");
                    System.out.println("Count  * ");
                    if (func != null)
                        if (func.getType() == OpType.CountRowsFunction) {
                            Projection projection = new Projection();
                            NamedExpr[] targets;
                            targets = new NamedExpr[1];
                            targets[0] = new NamedExpr(func);
                            projection.setNamedExprs(targets);
                            projection.setChild(ChildExpr);
                            return projection;
                        }
                }
            } else
                return ChildExpr;
            //return recursiveTree(children.get(0), nodesTree, nodesMap, arrowConnections);
        } else if (nodeType.equals("com.apatar.project.ProjectNode")) {

            if (ChildExpr == null)
                return null;

            ret = new Projection();
            String TargetName = "";
            Element node = cur.getChild("OutputConnectionPoints");
            node = node.getChild("ConnectionPoint");
            node = node.getChild("tableInfo");
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
                    if (GroupByOther.containsKey(TargetName))
                        targets[count++] = new NamedExpr(GroupByOther.get(TargetName));
                    else
                        targets[count++] = new NamedExpr(CreateColumnReference(TargetName));
                }
                ((Projection) ret).setNamedExprs(targets);

            }
            ((Projection) ret).setChild(ChildExpr);
            return ret;
        } else if (nodeType.equals("com.apatar.limit.OrderByNode")) {
            System.out.println("OrderByNode  ");

            if (ChildExpr == null)
                return null;

            if (subExpr != null)
                ((Sort) subExpr).setChild(ChildExpr);
            return subExpr;

        } else if (nodeType.equals("com.apatar.validate.FilterNode")) {
            System.out.println("FilterNode  ");


            if (ChildExpr == null)
                return null;
            //if(ChildExpr.getType()==OpType.Aggregation)

            if (subExpr != null)
                if (subExpr.getType() == OpType.Filter) {
                    ((Selection) subExpr).setChild(ChildExpr);

                } else if (subExpr.getType() == OpType.Having) {
                    if (ChildExpr.getType() == OpType.Aggregation)
                        if (((Having) subExpr).hasChild()) { //There is Filter node
                            Selection s = (Selection) ((Having) subExpr).getChild(); //get the filter node
                            s.setChild(((Aggregation) ChildExpr).getChild()); // set Filters node's child Agregation's child
                            ((Aggregation) ChildExpr).setChild(s);
                            ((Having) subExpr).setChild(ChildExpr);
                        } else {
                            ((Having) subExpr).setChild(ChildExpr);
                        }
                }


            return subExpr;
        } else if (nodeType.equals("com.apatar.join.JoinNode")) {
            Join join;
            System.out.println("JoinNode  ");
            if (childrenExpr.size() == 2) {
                join = new Join(JoinType.INNER);
                if (childrenExpr.get(0).getType() == OpType.RelationList) {
                    Expr[] r = ((RelationList) childrenExpr.get(0)).getRelations();
                    join.setRight(r[0]);
                } else
                    join.setRight(childrenExpr.get(0));

                if (childrenExpr.get(1).getType() == OpType.RelationList) {
                    Expr[] r = ((RelationList) childrenExpr.get(1)).getRelations();
                    join.setLeft(r[0]);
                } else
                    join.setLeft(childrenExpr.get(1));

                Element subnode = cur.getChild("Condition");
                Expr left = CreateColumnReference(subnode.getAttributeValue("column1"));
                Expr right = CreateColumnReference(subnode.getAttributeValue("column2"));
                Expr searchCondition = new BinaryOperator(OpType.Equals, left, right);
                join.setQual(searchCondition);
                Expr[] relations = new Expr[1];
                relations[0] = join;
                return new RelationList(relations);
            } else {
                System.err.println("Not correct inputs for Join  ");
            }
        } else if (nodeType.equals("com.apatar.groupByNew.GroupByNewNode")) {
            System.out.println("GroupByNewNode ");
            if (ChildExpr == null)
                return null;

            if (subExpr != null)
                ((UnaryOperator) subExpr).setChild(ChildExpr);
            return subExpr;
        } else if (nodeType.equals("com.apatar.read.READNode")) {
            System.out.println("READNode ");

            Expr[] relations = new Expr[1];
            relations[0] = new Relation(cur.getAttributeValue("title"));
            return new RelationList(relations);

        } else if (nodeType.equals("com.apatar.limit.LimitNode")) {
            System.out.println("LimitNode ");
            if (subExpr != null && ChildExpr != null) {
                Limit limitNode = new Limit(subExpr);

                limitNode.setChild(ChildExpr);
                return limitNode;
            }
        } else if (nodeType.equals("com.apatar.mapReduce.MapReduceNode")) {
            System.out.println("MapReduceNode ");

            if (subExpr != null && ChildExpr != null) {

                Selection mr = new Selection(subExpr);
                mr.setChild(ChildExpr);
                return mr;
            }

        } else
            System.out.println("Unknown Operator  ");

        return null;
    }

    static Expr visit_subproject(String xmlWithSpecial, String ParentNode) {

        Element subrootNode;
        Element subnode;
        List<Element> sublist;

        System.out.println(xmlWithSpecial);

        HashMap<String, String> subArrowMap = new HashMap<String, String>();
        HashMap<String, String> SubReverseArrowMap = new HashMap<String, String>();
        HashMap<String, List<String>> SubReverseArrowTree = new HashMap<String, List<String>>();

        //Parse subprojects XML
        if ((subrootNode = get_root(xmlWithSpecial)) != null) {
            String fieldName = null;

            sublist = subrootNode.getChildren("node");
            System.out.println("Analysing node: " + ParentNode + " Number of sub nodes: " + sublist.size());
            int nodesNum = 0;
            for (int j = 0; j < sublist.size(); j++)
                if (Integer.parseInt(sublist.get(j).getAttributeValue("id")) > nodesNum)
                    nodesNum = Integer.parseInt(sublist.get(j).getAttributeValue("id"));


            System.out.println("Max Id: " + nodesNum);
            String[][] ArrowConnections = new String[nodesNum + 1][nodesNum + 1];
            sublist = subrootNode.getChildren("arrow");
            System.out.println("subArrows " + sublist.size());

            for (int j = 0; j < sublist.size(); j++) {
                subnode = (Element) sublist.get(j);
                String from, to;
                from = subnode.getAttributeValue("begin_id");
                to = subnode.getAttributeValue("end_id");
                System.out.println("Arrow "
                        + from + " -> " + to + " @ " + subnode.getAttributeValue("end_conn_name"));
                subArrowMap.put(from, to);

                //Store the connection name information
                ArrowConnections[Integer.parseInt(from)][Integer.parseInt(to)] = subnode.getAttributeValue("end_conn_name");
                //Store the reversed tree connections
                SubReverseArrowMap.put(to, from);
                if (!SubReverseArrowTree.containsKey(to)) {
                    List<String> child = new ArrayList<>();
                    child.add(from);
                    SubReverseArrowTree.put(to, child);
                } else
                    (SubReverseArrowTree.get(to)).add(from);
            }

            System.out.println("TreeMode "); //Print it
            for (Map.Entry<String, List<String>> entry : SubReverseArrowTree.entrySet()) {
                List<String> C = entry.getValue();
                System.out.println("Found: " + entry.getKey() + " -> " + C.toString());
            }


            sublist = subrootNode.getChildren("node");

            HashMap<String, Element> subnodesMap = new HashMap<String, Element>();
            HashSet<String> endNodes = new HashSet<String>();

            for (int j = 0; j < sublist.size(); j++) {

                subnode = (Element) sublist.get(j);
                System.out.println("SubNode id: " + subnode.getAttributeValue("id") + " " + subnode.getAttributeValue("nodeClass") + " , " + subnode.getAttributeValue("title"));
                String cId = subnode.getAttributeValue("id");

                subnodesMap.put(cId, subnode);
                //Search and find end nodes
                if (!subArrowMap.containsKey(cId)) {
                    //Possible an end node
                    endNodes.add(cId);
                    System.out.println("^ Possible End node");
                }
            }
            int treenum = 0;

            for (String id : endNodes) {
                System.out.println("Found Tree #" + treenum++ + " starting node: " + id);
                print("", true, id, SubReverseArrowTree, subnodesMap);
            }
            treenum = 0;

            if (ParentNode.equals("Filter")) {
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
            } else if (ParentNode.equals("GroupBy")) {
                System.out.println(" GroupBy Node ");
                ArrayList<Expr> GroupByCollumns = new ArrayList<Expr>();


                for (String id : endNodes)
                    try {
                        System.out.println("Found Tree #" + treenum++ + " starting node: " + id);
                        print("", true, id, SubReverseArrowTree, subnodesMap);

                        Expr rec = recursiveGroupByFunction(id, SubReverseArrowTree, subnodesMap, ArrowConnections, subArrowMap, GroupByCollumns);
                        if (rec != null) {
                            if (rec.getType() == OpType.Target)
                                GroupByOther.put(((NamedExpr) rec).getAlias(), ((NamedExpr) rec).getExpr());

                            if (rec.getType() == OpType.CountRowsFunction)
                                GroupByOther.put("CountRowsFunction", rec);

                            //Selection ret = new Selection(rec);
                            //return ret;
                        } else
                            System.err.println("Error processing groupby node");
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

            } else if (ParentNode.equals("Sort")) {
                try {
                    ArrayList<Sort.SortSpec> sortList = null;
                    //Fix recursive read of operator if more than 1
                    if(endNodes.size()>1){
                        System.err.print("Found multiple end nodes, use multisort to connect the nodes, using just the first node, ignoring later");
                    }
                    for (String id : endNodes) {
                        sortList = recursiveSortFunction(id,SubReverseArrowTree,subnodesMap,ArrowConnections);//GetSortSpec(id, SubReverseArrowTree, subnodesMap);
                        if (sortList != null)
                            break;

                    }
                    if(sortList!=null)
                        if (sortList.size() > 0) {
                            Sort.SortSpec[] sortArray = new Sort.SortSpec[sortList.size()];
                            return new Sort((Sort.SortSpec[]) sortList.toArray(sortArray));
                        }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                return null;
            } else if (ParentNode.equals("Project")) {
                System.out.println(" Projection Node ");
            } else if (ParentNode.equals("Limit")) {
                System.out.println(" Limit Node ");
                for (String id : endNodes) {
                    Element cur = subnodesMap.get(id);
                    System.out.print(" ID " + id + " " + cur.getAttributeValue("title"));

                    String nodeType = cur.getAttributeValue("nodeClass");
                    if (nodeType.equals("com.apatar.functions.ConstantFunctionNode")) {
                        String functionType = cur.getAttributeValue("classFunction");

                        String value = cur.getChild(functionType).getAttributeValue("value");
                        if (value == null)
                            return null;
                        if (functionType.equals("com.apatar.functions.constant.LimitFunction"))
                            return new LiteralValue(value, LiteralValue.getLiteralType(value));
                        else
                            return null;
                    }
                }
            } else if (ParentNode.equals("MapReduce")) {
                System.out.println(" MapReduce Node ");

                Expr[] exprs = null;
                ArrayList<Expr> mrData = new ArrayList<>();
                for (String id : endNodes) {
                    subnode = (Element) subnodesMap.get(id);
                    //System.out.println("SubNode id: " + subnodesMap.get(id) + " " + subnode.getAttributeValue("nodeClass")+" , "+ subnode.getAttributeValue("title"));
                    String functionType = subnode.getAttributeValue("classFunction");
                    //if (functionType.equals("com.apatar.functions.constant.LimitFunction")
                    String value = subnode.getChild(functionType).getAttributeValue("value");

                    print("", true, id, SubReverseArrowTree, subnodesMap);
                    //fix the 4
                    functionType = functionType.split("\\.")[4];
                    System.out.println(" Function Type: " + functionType + " value " + value);

                    mrData.add(new NamedExpr(new LiteralValue(value, LiteralValue.LiteralType.String), functionType));
                }

                if (mrData.size() > 0) {
                    exprs = new NamedExpr[mrData.size()];
                    mrData.toArray(exprs);
                    return new ValueListExpr(exprs);
                }

                System.out.print("MR data parameters found: " + mrData.size());
            }
        }
        return (Expr) ErrorMessageNullReturn("Error parsing subroot Node");
    }

    static private void print(String prefix, boolean isTail, String head, HashMap<String, List<String>> nodesTree, HashMap<String, Element> nodesMap) {
        Element cur = nodesMap.get(head);
        //System.out.print(cur.toString() + " " + cur.getAttributeValue("title"));
        String name = head+": "+ cur.getAttributeValue("title");
        List<String> children = nodesTree.get(head);

        System.out.println(prefix + (isTail ? "└── " : "├── ") + name);
        if (children == null)
            return;
        for (int i = 0; i < children.size() - 1; i++) {
            //children.get(i).print(prefix + (isTail ? "    " : "│   "), false);
            print(prefix + (isTail ? "    " : "│   "), false, children.get(i), nodesTree, nodesMap);
        }
        if (children.size() > 0) {
            //children.get(children.size() - 1).print(prefix + (isTail ?"    " : "│   "), true);
            print(prefix + (isTail ? "    " : "│   "), true, children.get(children.size() - 1), nodesTree, nodesMap);
        }
    }

    static Sort.SortSpec GetSortSpec(String head, HashMap<String, List<String>> nodesTree, HashMap<String, Element> nodesMap) throws CloneNotSupportedException {
        Element cur = nodesMap.get(head);
        System.out.print(" ID " + head + " " + cur.getAttributeValue("title"));
        List<String> next = nodesTree.get(head);
        int currentID = Integer.parseInt(head);
        List<String> children = nodesTree.get(head);

        String nodeType = cur.getAttributeValue("nodeClass");
        if (nodeType.equals("com.apatar.functions.FunctionNode")) {
            String functionType = cur.getAttributeValue("classFunction").trim();
            if (children == null) //Unconnected function node ... problem
                return (Sort.SortSpec) ErrorMessageNullReturn("Zero children Function node at function node " + functionType );

            if (ApatarTajoSortFunctMap.containsKey(functionType)) {
                Sort.SortSpec Spec = (Sort.SortSpec) ApatarTajoSortFunctMap.get(functionType).clone();
                cur = nodesMap.get(children.get(0));
                nodeType = cur.getAttributeValue("nodeClass");
                if (nodeType.equals("com.apatar.core.ColumnNode")) {
                    String columnName = getCollumnName(cur);
                    if (GroupByOther.containsKey(columnName)) {
                        Spec.setKey(GroupByOther.get(columnName));
                    } else
                        Spec.setKey(CreateColumnReference(columnName));
                    return Spec;
                }
            }
        }

        return (Sort.SortSpec) ErrorMessageNullReturn("Error in sortSpec Node");

    }

    static ArrayList<Sort.SortSpec>  recursiveSortFunction(String head, HashMap<String, List<String>> nodesTree, HashMap<String, Element> nodesMap, String[][] ArrowConnections) throws CloneNotSupportedException {
        //Get node
        ArrayList<Sort.SortSpec>  ChildList=null;
        ArrayList<Sort.SortSpec>  SortList=new ArrayList<Sort.SortSpec>();

        Element cur = nodesMap.get(head);
        System.out.print(" ID " + head + " " + cur.getAttributeValue("title"));

        List<String> children = nodesTree.get(head);

        String nodeType = cur.getAttributeValue("nodeClass");
        if (nodeType.equals("com.apatar.functions.FunctionNode")) {
            String functionType = cur.getAttributeValue("classFunction").trim();
            if (children == null) //Unconnected function node ... problem
                return (ArrayList<Sort.SortSpec>) ErrorMessageNullReturn("Zero children Function node at function node " + functionType );


            if (ApatarTajoSortFunctMap.containsKey(functionType)) {
                Sort.SortSpec childSpec = GetSortSpec(head,nodesTree,nodesMap);
                if(childSpec!=null)
                    SortList.add(childSpec);
                else
                    return (ArrayList<Sort.SortSpec>) ErrorMessageNullReturn("Error in sort node. ");

            }else if(functionType.equals("com.apatar.functions.math.MultipleSortFunction")){
                if (children.size() == 2)
                    for (int i = 0; i < children.size(); i++) {
                        ChildList = recursiveSortFunction(children.get(i), nodesTree, nodesMap, ArrowConnections);
                        if (ChildList == null)
                            return (ArrayList<Sort.SortSpec>) ErrorMessageNullReturn("Wrong multisort input, connection " + (i+1));

                        SortList.addAll(ChildList);
                    }
                else
                    return (ArrayList<Sort.SortSpec>) ErrorMessageNullReturn("Found wrong child size: " + children.size() + " must have 2 inputs exactly");
            }
            else
                return (ArrayList<Sort.SortSpec>) ErrorMessageNullReturn("class Function unknown field name: \'" + functionType + '\'');


            return SortList;

        }

        return (ArrayList<Sort.SortSpec>) ErrorMessageNullReturn("Error in sort node. ");
    }

    static Object ErrorMessageNullReturn(String message){
        System.err.println(message);
        return null;
    }

    static Expr recursiveFilterFunction(String head, HashMap<String, List<String>> nodesTree, HashMap<String, Element> nodesMap, String[][] ArrowConnections) throws CloneNotSupportedException {
        //Get node

        Element cur = nodesMap.get(head);
        System.out.print(" ID " + head + " " + cur.getAttributeValue("title"));

        int currentID = Integer.parseInt(head);
        List<String> children = nodesTree.get(head);

        String nodeType = cur.getAttributeValue("nodeClass");
        if (nodeType.equals("com.apatar.functions.FunctionNode")) {
            String functionType = cur.getAttributeValue("classFunction").trim();

            if (children == null) //Unconnected function node ... problem
                return (Expr) ErrorMessageNullReturn("Zero children Function node at function node " + functionType );

            if (ApatarTajoFilterFunctMap.containsKey(functionType)) {
                Expr FuncExpr = (Expr) ApatarTajoFilterFunctMap.get(functionType).clone();

                if (FuncExpr.getType().getBaseClass() == BinaryOperator.class && children.size() != 2)
                    return (Expr) ErrorMessageNullReturn("childernd Function node at function node " );

                Expr[] childrExprArray = new Expr[children.size()];

                for (int i = 0; i < children.size(); i++) {

                    Expr childExpr = recursiveFilterFunction(children.get(i), nodesTree, nodesMap, ArrowConnections);
                    if (childExpr == null)
                        return null;
                    int from = Integer.parseInt(children.get(i));
                    String conection_input = ArrowConnections[from][currentID];
                    if (conection_input.equals("Input1"))
                        childrExprArray[0] = childExpr;
                    else if (conection_input.equals("Input2"))
                        childrExprArray[1] = childExpr;
                    else
                        childrExprArray[0] = childExpr;
                }

                if (children.size() == 2) {
                    ((BinaryOperator) FuncExpr).setLeft(childrExprArray[0]);
                    ((BinaryOperator) FuncExpr).setRight(childrExprArray[1]);
                } else if (children.size() == 1)
                    if (FuncExpr.getType().getBaseClass() == PatternMatchPredicate.class) {
                        ((PatternMatchPredicate) FuncExpr).setLeft(childrExprArray[0]);
                        //pattern
                        String patternValue = cur.getChild(functionType).getAttributeValue("Value");
                        if (patternValue == null)
                            return null;
                        ((PatternMatchPredicate) FuncExpr).setRight(new LiteralValue(patternValue, LiteralValue.LiteralType.String));
                    } else//other simple operators NOT null etc
                        ((UnaryOperator) FuncExpr).setChild(childrExprArray[0]);
                else
                    return (Expr) ErrorMessageNullReturn("Found wrong child size: " + children.size());


                return FuncExpr;
            } else
                return (Expr) ErrorMessageNullReturn("class Function unknown field name: \'" + functionType + '\'');


        } else if (nodeType.equals("com.apatar.functions.ConstantFunctionNode")) {
            String functionType = cur.getAttributeValue("classFunction");

            String value = cur.getChild(functionType).getAttributeValue("value");
            if (value == null)
                return (Expr) ErrorMessageNullReturn("Bad Constant Value");
            if (functionType.equals("com.apatar.functions.constant.NumericFunction"))
                return new LiteralValue(value, LiteralValue.getLiteralType(value));
            else if (functionType.equals("com.apatar.functions.constant.TextFunction"))
                return new LiteralValue(value, LiteralValue.LiteralType.String);
            else
                return null;
        } else if (nodeType.equals("com.apatar.core.ColumnNode")) {
            String collumnName = getCollumnName(cur);
            if (GroupByOther.containsKey(collumnName))//Having possible
            {
                foundHaving = true;
                return GroupByOther.get(collumnName);
            } else
                return CreateColumnReference(collumnName);
        } else
            return (Expr) ErrorMessageNullReturn("Unknown NodeClass");

    }

    public static ColumnReferenceExpr CreateColumnReference(String fullColumnName) {
        String attrNames[] = fullColumnName.split(".");
        if (attrNames.length >= 2) {
            return new ColumnReferenceExpr(attrNames[attrNames.length - 2], attrNames[attrNames.length - 1]);
        }
        return new ColumnReferenceExpr(fullColumnName);
    }

    public static String getCollumnName(Element node) {

        String title = node.getAttributeValue("title");
        if (title != null)
            if (title.contains(".")) {
                String[] s = title.split("\\.", 2);
                title = s[1];

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
        if (nodeType.equals("com.apatar.functions.FunctionNode")) {


            String functionType = cur.getAttributeValue("classFunction").trim();
            // System.out.print("ApatarTajoFilterFunctMap.size() = " + ApatarTajoFilterFunctMap.size());
            if (ApatarTajoFilterFunctMap.containsKey(functionType)) {
                if (children == null) //Unconnected function node ... problem
                    return null;
                System.out.print("Found filter function map: " + functionType);

                Expr FuncExpr = (Expr) ApatarTajoFilterFunctMap.get(functionType).clone();

                if (FuncExpr.getType().getBaseClass() == BinaryOperator.class && children.size() != 2)
                    return null;

                Expr[] childrExprArray = new Expr[children.size()];

                for (int i = 0; i < children.size(); i++) {

                    Expr childExpr = recursiveGroupByFunction(children.get(i), nodesTree, nodesMap, ArrowConnections, subArrowMap, groupByCollumns);
                    if (childExpr == null)
                        return null;
                    int from = Integer.parseInt(children.get(i));
                    String conection_input = ArrowConnections[from][currentID];
                    if (conection_input.equals("Input1"))
                        childrExprArray[0] = childExpr;
                    else if (conection_input.equals("Input2"))
                        childrExprArray[1] = childExpr;
                    else
                        childrExprArray[0] = childExpr;
                }

                if (children.size() == 2) {
                    ((BinaryOperator) FuncExpr).setLeft(childrExprArray[0]);
                    ((BinaryOperator) FuncExpr).setRight(childrExprArray[1]);
                } else if (children.size() == 1)
                    if (FuncExpr.getType().getBaseClass() == PatternMatchPredicate.class) {
                        ((PatternMatchPredicate) FuncExpr).setLeft(childrExprArray[0]);
                        //pattern
                        String patternValue = cur.getChild(functionType).getAttributeValue("Value");
                        if (patternValue == null)
                            return null;
                        ((PatternMatchPredicate) FuncExpr).setRight(new LiteralValue(patternValue, LiteralValue.LiteralType.String));
                    } else//other simple operators NOT null etc
                        ((UnaryOperator) FuncExpr).setChild(childrExprArray[0]);
                else {
                    System.err.println("Found wrong child size: " + children.size());
                    return null;
                }
                return FuncExpr;
            }
            if (ApatarTajoAgregateFunctMap.containsKey(functionType)) {
                System.out.print("Found filter function map: " + functionType);
                Expr FuncExpr = (Expr) ApatarTajoAgregateFunctMap.get(functionType).clone();
                if (FuncExpr.getType() == OpType.CountRowsFunction)
                    return FuncExpr;
                if (children == null) //Unconnected function node ... problem
                    return null;
                if (children.size() == 1) {
                    Expr childExpr = recursiveGroupByFunction(children.get(0), nodesTree, nodesMap, ArrowConnections, subArrowMap, groupByCollumns);

                    if (childExpr != null) {
                        Expr[] params = new Expr[1];
                        params[0] = childExpr;
                        ((FunctionExpr) FuncExpr).setParams(params);
                        return FuncExpr;
                    }

                }
                return null;

            } else {
                System.err.println("class Function unknown field name: \'" + functionType + '\'');
                return null;
            }

        } else if (nodeType.equals("com.apatar.functions.ConstantFunctionNode")) {
            String functionType = cur.getAttributeValue("classFunction");

            String value = cur.getChild(functionType).getAttributeValue("value");
            if (value == null)
                return null;
            if (functionType.equals("com.apatar.functions.constant.NumericFunction"))
                return new LiteralValue(value, LiteralValue.getLiteralType(value));
            else if (functionType.equals("com.apatar.functions.constant.TextFunction"))
                return new LiteralValue(value, LiteralValue.LiteralType.String);
            else
                return null;

        } else if (nodeType.equals("com.apatar.core.ColumnNode")) {//Groupby
            //check if the node is input node
            String collumnName = getCollumnName(cur);

            if (!subArrowMap.containsKey(head) && cur.getAttributeValue("connectionName").equals("input")) {
                groupByCollumns.add(CreateColumnReference(collumnName));
            }

            //if it is output node -> to be saved for later use
            if (children != null && cur.getAttributeValue("connectionName").equals("output"))

                if (children.size() == 1) {
                    Expr childExpr = recursiveGroupByFunction(children.get(0), nodesTree, nodesMap, ArrowConnections, subArrowMap, groupByCollumns);
                    if (childExpr != null)
                        return new NamedExpr(childExpr, collumnName);//cur.getAttributeValue("title"));
                }

            //if it is the input node
            if (children == null && subArrowMap.containsKey(head)) {
                return CreateColumnReference(collumnName);
            }
        }

        return null;
    }

    static Element get_root(String xml) {
        Element rootNode = null;
        InputStream inputStream = new ByteArrayInputStream(
                xml.getBytes(Charset.forName("UTF-8")));
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
}
