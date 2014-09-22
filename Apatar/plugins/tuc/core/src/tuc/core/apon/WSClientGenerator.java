package tuc.core.apon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.axis2.wsdl.WSDL2Java;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPHeaderImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12AddressImpl;
import com.ibm.wsdl.extensions.soap12.SOAP12HeaderImpl;

public class WSClientGenerator {

	private static final String XMLSchemaNameSpace = "http://www.w3.org/2001/XMLSchema";	//base types are defined here

	/* COLORS */
	private static final int WHITE=0;
	private static final int GRAY=1;
	private static final int BLACK=2;

	private static final int ELEMENT_NODE=1;

	private static final int NOT_INITIALIZED=-1;

	/* FLAGS */
	private static final int ENUM=0;
	private static final int ARRAY=1;
	private static final int XS_ANY=2;
	private static final int NESTED_ELEMENT = 3;
	private static final int EXTENSION = 4;

	private static int argsCtr = 0;
	private static int maxArrayCtr = -1;
	
	private static boolean isToTempDB = false;

	private static HashSet<String> allTypesSet = new HashSet<String>();

	private static HashMap<String, Integer> overloads = new HashMap<String, Integer>();	//hasn't been used yet

	public void generate(String wsdl){
		Utilities.initReserved();
		Utilities.initAxisToJava_mapping();
		Utilities.initJavaToApatarSQL_mapping();

		try {
			WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
			Definition def = reader.readWSDL(wsdl);
			reader.setFeature("javax.wsdl.verbose", true);
			reader.setFeature("javax.wsdl.importDocuments", true);

			checkOverloads(def);	//haven't implemented this one yet

			/*
			 * Here we find namespaces referring to XMLSchemaNameSpace
			 */
			LinkedList<String> baseTypesNSs= new LinkedList<String>();
			Map nameSpaces = def.getNamespaces();
			for(Object o:nameSpaces.keySet()){
				if(nameSpaces.get(o).equals(XMLSchemaNameSpace)){
					baseTypesNSs.add((String)o);
				}
			}

			/*
			 * *************************SERVICE Selection***************************
			 */
			Map servicesMap = def.getServices();
			Collection serviceCol = servicesMap.values();
			Iterator serviceIt = serviceCol.iterator();

			int ctr = 0;
			while(serviceIt.hasNext()){
				ctr++;
				Service serv = (Service) serviceIt.next();

				/*
				 * creating necessary files & folder here
				 */
				String serviceName = serv.getQName().getLocalPart();
				String packageName = "tuc.ws."+Utilities.lowerFirst(serviceName);

				String pluginFolderName = createPluginFileStructure(serv.getQName());
				generateStubs(wsdl,pluginFolderName,packageName);
				createNodeFactory(pluginFolderName,packageName,serviceName);
				createUtils(pluginFolderName,packageName, serviceName);
				createTable(pluginFolderName,packageName,serviceName);

				/*
				 * ***************************PORT Selection******************************
				 * port is selected automatically by its protocol 
				 * (SOAP or SOAP 1.2 is selected)
				 */
				Map ports = serv.getPorts();
				Collection portCol = ports.values();
				Iterator portIt = portCol.iterator();

				Port port = null;
				ctr =0;

				while(portIt.hasNext()){
					ctr++;
					port = (Port) portIt.next();
					if(port.getExtensibilityElements().get(0).getClass()==SOAPAddressImpl.class || 
							port.getExtensibilityElements().get(0).getClass()==SOAP12AddressImpl.class)
						break;
				}

				/*
				 * *************************OPERATION Selection***************************
				 */
				Binding binding = port.getBinding();
				List<BindingOperation> opers = binding.getBindingOperations();

				/*
				 * Twra exw parei ta inputs tou operation pou 8elw
				 * kai yparxoun sth List<String> inputs (ws local names twn Types)
				 */

				Types tps = def.getTypes();
				List<Schema> schemas = tps.getExtensibilityElements();

				//				Schema sc = (Schema)extEls.get(0);	//Check here if schema exists

				createTableList(def.getTargetNamespace(),schemas,serv,baseTypesNSs,nameSpaces,def,pluginFolderName,packageName,opers);

				//		}
				createNode(pluginFolderName, packageName, serviceName, allTypesSet);

				//compiling now
				if(!Utilities.compile(pluginFolderName, packageName, serviceName))
					System.err.println("COMPILATION ERROR!");
				else
					Utilities.copyIcons(pluginFolderName, serviceName);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}

	/*
	 * createClient Creates (or it will as an Alpha version)
	 * client for just one operation.
	 * I might need to create one for everyone
	 */
	private static void createFunctions( 
			String targetNameSpace,
			List<Schema> schemas,
			Service service,
			BindingOperation bindingOperation, 
			LinkedList<String> baseTypesNSs,
			Map nameSpaces,Definition def,
			String pluginFolderName, //the folder where the plugin will be created
			Writer output
	) throws IOException{

		isToTempDB = false;

		output.write("\n\tpublic static void "+bindingOperation.getOperation().getName()+"(HashMap<java.lang.String, java.lang.Object> argVals," +
		"\n\t\t\tTableInfo ti, ArrayList<Record> recList, java.lang.String tableName, DataBaseInfo dbi) throws AxisFault,RemoteException {\n\n");

		output.write("\t\tDataBaseTools.completeTransfer();\n");
		output.write("\t\ttry {\n");
		output.write("\t\t\tti.getSchemaTable().updateRecords(recList);\n");
		output.write("\t\t} catch (Exception e) {\n");
		output.write("\t\t\te.printStackTrace();\n");
		output.write("\t\t}\n\n");

		output.write("\t\tKeyInsensitiveMap data = new KeyInsensitiveMap();\n");
		output.write("\t\tint sqlType;\n\n");

		output.write("\t\t"+Utilities.capitalizeFirst(service.getQName().getLocalPart())+"Stub"+" _stub = new "+Utilities.capitalizeFirst(service.getQName().getLocalPart())+"Stub();\n");

		Operation operation = bindingOperation.getOperation();
		String operationName = Utilities.capitalizeFirst(operation.getName());

		/*
		 * Now the arguments must be initialized
		 * so we are going to use the schema to find their types
		 */
		Input opInput = operation.getInput();
		Message msg = opInput.getMessage();
		Map msgParts = msg.getParts();

		LinkedList<Part> inputs = new LinkedList<Part>();
		inputs.addAll(msgParts.values());

		List extElements = bindingOperation.getBindingInput().getExtensibilityElements();
		Object extElement;

		for(int i = 0; i<extElements.size();i++){
			extElement  = extElements.get(i);
			if (extElement.getClass().equals(com.ibm.wsdl.extensions.soap.SOAPHeaderImpl.class)){
				Message headerMsg = def.getMessage(((SOAPHeaderImpl)extElement).getMessage());
				inputs.addAll(headerMsg.getParts().values());
			}
			else if(extElement.getClass().equals(com.ibm.wsdl.extensions.soap12.SOAP12HeaderImpl.class)){
				Message headerMsg = def.getMessage(((SOAP12HeaderImpl)extElement).getMessage());
				inputs.addAll(headerMsg.getParts().values());
			}
		}

		for(int iii=0;iii<inputs.size();iii++){

			Part part = inputs.get(iii);
			QName partTypeQname = part.getTypeName();
			boolean nullPartTypeQname = false;
			String part_Local="";
			if(partTypeQname==null){
				partTypeQname = part.getElementName();
				QName elementName = part.getElementName();
				part_Local = elementName.getLocalPart();
				nullPartTypeQname = true;
			}else{
				part_Local = partTypeQname.getLocalPart();
			}

			/*
			 * Now we have the Type node so 
			 * we create a tree to decode it
			 */
			TreeNode root = new TreeNode(part_Local,part_Local,"",null, new LinkedList<TreeNode>(), NOT_INITIALIZED, null,null);

			Node typeNode = searchSchema(schemas, part_Local, partTypeQname,baseTypesNSs,nameSpaces,null);

			root = createTree(root, typeNode, baseTypesNSs,schemas,nameSpaces);
			if(root.isRootRenamed()){
				inputs.get(iii).setElementName(new QName(inputs.get(iii).getElementName().getNamespaceURI(), inputs.get(iii).getElementName().getLocalPart()+"E"));
			}

			for(int i=0;i<root.getChildren().size();i++){
				if( root.getChildren().get(i).getColor()==WHITE ){
					try{
						DFSVisitInput(root.getChildren().get(i), output);
					}catch(IOException ex){
						ex.printStackTrace();
					}
				}
			}
			root.setColor(BLACK);
			List<TreeNode> children = root.getChildren();

			output.write("\t\t"+Utilities.capitalizeFirst(root.getName())+" _"+root.getName()+" = new "+ Utilities.capitalizeFirst(root.getName())+"();\n");

			if(children !=null){
				if(children.size()>0){
					while(((TreeNode)children.get(0)).getType().equals("notArealType")){
						children = children.get(0).getChildren();
						if(children.size()==0){
							break;
						}
					}

					for(int k=0;k<children.size();k++){
						//prepei na arxikopoih8ei k to root me orismata ta children
						output.write("\t\t_"+root.getName()+".set"+Utilities.capitalizeFirst(children.get(k).getName())+"(_"+children.get(k).getFullName()+");\n");
					}
				}
			}
			if(!nullPartTypeQname){

				output.write("\t\t_"+operationName+".set"+Utilities.capitalizeFirst(root.getName())+"( _"+root.getName()+" );\n");
			}
		}

		/*
		 * Now we have to decode the output types.
		 * We do that as above
		 */
		Output opOutput = operation.getOutput();
		msg = opOutput.getMessage();
		msgParts = msg.getParts();
		Collection msgPartsCol =  msgParts.values();
		Iterator msgPartsIter = msgPartsCol.iterator();

		while(msgPartsIter.hasNext()){

			Part part = (Part)msgPartsIter.next();
			QName partTypeQname = part.getTypeName();
			String part_Local="";
			boolean nullPartTypeQname = false;
			if(partTypeQname==null){
				partTypeQname = part.getElementName();
				QName elementName = part.getElementName();
				part_Local = elementName.getLocalPart();
				nullPartTypeQname = true;
			}else{
				part_Local = partTypeQname.getLocalPart();
			}

			/*
			 * We invoke the service here
			 */
			output.write("\t\t"+Utilities.capitalizeFirst(part_Local)+" _"+part_Local+" = new "+Utilities.capitalizeFirst(part_Local)+"();\n");
			output.write("\t\t_"+part_Local+" = _stub."+Utilities.lowerFirst(operationName)+"(");
			for(int i=0;i<inputs.size();i++){
				output.write(" _"+inputs.get(i).getElementName().getLocalPart());
				if(i<inputs.size()-1){
					output.write(", ");
				}
			}
			output.write(" );\n");

			/*
			 * Now we have the Type node so 
			 * we create a tree to decode it
			 */			
			TreeNode root = new TreeNode(part_Local,part_Local,"",null, new LinkedList<TreeNode>(), NOT_INITIALIZED, null,null);

			Node typeNode = searchSchema(schemas, part_Local, partTypeQname,baseTypesNSs,nameSpaces,null);

			root = createTree(root, typeNode, baseTypesNSs,schemas,nameSpaces);

			//======this invokes a fake run if DFSVisitOutput to determine the depth of the arrays======
			File tmpFile = new File("tmp");
			Writer fakeOutput = new BufferedWriter(new FileWriter(tmpFile));
			tmpFile.deleteOnExit();
			TreeNode fakeRoot = TreeNode.copyNodes(root);
			for(int i=0;i<fakeRoot.getChildren().size();i++){
				DFSVisitOutput(fakeRoot, fakeOutput,false, false,NOT_INITIALIZED,false,true,service.getQName());
			}
			fakeOutput.close();
			//===========================================================================================

			//ROOT INITIALIZATION HERE
			if(!nullPartTypeQname){
				if( Utilities.isReserved(root.getName())){
					output.write("\t\t"+Utilities.capitalizeFirst(part_Local)+" _"+part_Local +"= _"+part_Local+".get_"+root.getName());
				}else{
					output.write("\t\t"+Utilities.capitalizeFirst(part_Local)+" _"+part_Local +" = _"+part_Local+".get"+Utilities.capitalizeFirst(part_Local)+"();\n");
				}
			}

			for(int i=0;i<root.getChildren().size();i++){
				DFSVisitOutput(root, output,false, false,NOT_INITIALIZED,false,false,service.getQName());
			}
		}
		if(!isToTempDB){
			output.write("\n");
			output.write("\t\ttry{\n");
			output.write("\t\t\tDataBaseTools.insertData(new DataProcessingInfo(ApplicationData.getTempDataBase().getDataBaseInfo(), ti.getTableName(), ti.getRecords(),ApplicationData.getTempJDBC()), data);\n");
			output.write("\t\t} catch (Exception e) {\n");
			output.write("\t\t\te.printStackTrace();\n");
			output.write("\t\t}\n");
		}
		output.write("\t}\n");
	}

	/**
	 * This method creates the package name from targetNameSpace
	 * 
	 * i.e. if targetNameSpace = "http://www.ecubicle.net/webservices/mine/"
	 * 		  package name = "net.ecubicle.www.webservices.mine"
	 */
	private static String createPackageName(String targetNameSpace){

		String[] nsp;
		String ns;
		String packageName = "";

		if(targetNameSpace.startsWith("urn")){	//it's a URN
			ns = targetNameSpace.substring(4);

			String[] colons = ns.split(":");

			if (colons != null){
				for (int i=colons.length-1;i>=0;i--){
					packageName = packageName+colons[i].toLowerCase();
					if(i!=0)
						packageName = packageName+".";
				}
			}

		}
		else{	//it's a URL
			if(targetNameSpace.contains("//")){
				nsp = targetNameSpace.split("//");
				ns = nsp[1];
			}else{
				ns = targetNameSpace;
			}

			String[] slashes = ns.split("/");

			if(slashes!=null){
				String[] dots = slashes[0].split("\\.");
				if(dots != null){	
					for(int i=dots.length-1;i>=0;i--){
						packageName = packageName+dots[i].toLowerCase();
						if(i!=0){
							packageName = packageName+".";
						}
					}
				}
				if(slashes.length>1){
					for(int i=1;i<slashes.length;i++){
						packageName = packageName+"."+slashes[i].toLowerCase();
					}
				}
			}
		}
		return packageName.replace("-", "_");
	}

	/*
	 * TODO: Need to handle Arrays enumerations etc etc
	 */
	private static TreeNode createTree(TreeNode root, Node node, LinkedList<String> baseTypesNSs,List<Schema> schemas,Map nameSpaces){

		boolean basetype = false;
		boolean realType = true;

		if(node==null)
			return root;

		Node nameNode = node.getAttributes().getNamedItem("name");
		Node typeNode = node.getAttributes().getNamedItem("type");
		if(nameNode!=null && typeNode!=null){
			if(typeNode.getNodeValue().split(":").length>1){
				if(nameNode.getNodeValue().equals(typeNode.getNodeValue().split(":")[1])){
					root.setName(nameNode.getNodeValue()+"E");
					root.setFullName(nameNode.getNodeValue()+"E");
					root.setRootRenamed(true);
				}
			}
			else{
				if(nameNode.getNodeValue().equals(typeNode.getNodeValue().split(":"))){
					root.setName(nameNode.getNodeValue()+"E");
					root.setFullName(nameNode.getNodeValue()+"E");
					root.setRootRenamed(true);
				}
			}
		}

		NodeList nl = node.getChildNodes();

		if(nl.getLength()==0 && typeNode!=null){
			TreeNode tr = new TreeNode(node.getAttributes().getNamedItem("name").getNodeValue(),
					root.getFullName()+"_"+node.getAttributes().getNamedItem("name").getNodeValue(),
					node.getAttributes().getNamedItem("type").getNodeValue(), 
					root, 
					new LinkedList<TreeNode>(), NOT_INITIALIZED, null, null);

			root.getChildren().add(tr);
			String type = "";
			if(tr.getType().split(":").length>1){
				type = tr.getType().split(":")[1];
			}
			else{
				type = tr.getType();
			}
			Node asasd = searchSchema(schemas, type, QName.valueOf(tr.getType()), baseTypesNSs, nameSpaces,node);
			tr = createTree(tr, asasd, baseTypesNSs, schemas, nameSpaces);
		}
		else{
			for (int i=0;i<nl.getLength();i++){
				basetype = false;
				realType = true;
				Node curNode = nl.item(i);
				Node extensionCurNode = nl.item(i); //This will be used later IF the element extends another element

				if(curNode.getNodeType()==ELEMENT_NODE){	//If node is Element node
					TreeNode tr = new TreeNode();

					TreeNode tmpNode = root;
					while(tmpNode.getType().equals("notArealType"))
						tmpNode = tmpNode.getFather();

					NamedNodeMap attrs = curNode.getAttributes();
					if( curNode.getLocalName().equals("complexType") || 
							attrs.getLength() < 1 ||	//??? right or wrong ???
							curNode.getLocalName().equals("complexContent") || curNode.getLocalName().equals("sequence") || 
							curNode.getLocalName().equals("any") || curNode.getLocalName().equals("extension")){
						tr.setName(curNode.getNodeName());
						tr.setType("notArealType");

						if(curNode.getLocalName().equals("any")){
							tr.setFullName(tmpNode.getFullName()+"_anyXML");
							tr.setFlag(XS_ANY);
						}
						realType = false;
					}
					else{
						realType = true;

						if(attrs.getNamedItem("ref")!=null){
							/*
							 * TODO: not very sure about this.. check it out
							 */
							curNode= searchSchema(schemas, attrs.getNamedItem("ref").getNodeValue().split(":")[1],QName.valueOf(attrs.getNamedItem("ref").getNodeValue()),baseTypesNSs,nameSpaces,node);

							tr.setFather(root);
							if(curNode.getAttributes().getNamedItem("name")!=null)
								tr.setName(curNode.getAttributes().getNamedItem("name").getNodeValue());
							if(curNode.getAttributes().getNamedItem("type")!=null)
								tr.setType(curNode.getAttributes().getNamedItem("type").getNodeValue());

							tr = createTree(tr,curNode,baseTypesNSs,schemas,nameSpaces);
							return root;
						}

						if(attrs.getNamedItem("name")!=null){
							tr.setName(attrs.getNamedItem("name").getNodeValue());
							tr.setFullName(tmpNode.getFullName()+"_"+attrs.getNamedItem("name").getNodeValue());
						}	

						if(attrs.getNamedItem("type")!=null){
							tr.setType(attrs.getNamedItem("type").getNodeValue());
						}

						if(tr.getType()!=null){
							if( (tr.getType().split(":").length>1 && tr.getType().split(":")[1].startsWith("ArrayOf")) || 
									tr.getType().split(":").length==1 && tr.getType().startsWith("ArrayOf") ){
								tr.setFlag(ARRAY);
							}
						}
						else{	//tr.getType==null	
							//THIS maybe WRONG!! MUST HANDLE ENUMERATIONS ARRAYS etc...
							if(curNode.getNodeName().contains("restriction")){

								if(curNode.getChildNodes().getLength()>1){
									/*
									 * TODO: the following doesn't look so good...
									 * 		 aka WTF are you doing you mpakal???????
									 */
									if(curNode.getChildNodes().item(1).getNodeName().split(":")[1].equalsIgnoreCase("enumeration")){
										tr.setFlag(ENUM);
										tr.setEnumIndex(new LinkedList<String>());
										enumHandler(curNode,tr);
									}
								}
								tr.setName(curNode.getNodeName());

								tmpNode = root;
								while(tmpNode.getType().equals("notArealType"))
									tmpNode = tmpNode.getFather();

								if(attrs.getNamedItem("name")!=null){
									tr.setFullName(tmpNode.getFullName()+"_"+attrs.getNamedItem("name").getNodeValue());
								}
								else if(curNode.getParentNode().getAttributes().getNamedItem("name")!=null){
									/*
									 * TODO: must fix this...
									 */
									tr.setFullName(tmpNode.getFullName()+"_"+curNode.getParentNode().getAttributes().getNamedItem("name").getNodeValue());
								}

								tr.setType(attrs.getNamedItem("base").getNodeValue());
							}
							else if(curNode.getChildNodes().getLength()>0){
								/*
								 * TODO: it's not allways a _type0 could be _type1 etc
								 * 
								 * use a hash map to monitor the overloads (key = node's name value = overload ctr)
								 */
								//							tr.setType(tr.getName());
								tr.setType(tr.getName()+"_type0");
								//							tr.setFullName(tr.getFullName()+"_type0");
								tr.setFlag(NESTED_ELEMENT);
							}
							else{
								tr.setType(tr.getName());
							}

						}

						String typeNamespace = tr.getType().split(":")[0];

						if(baseTypesNSs.contains(typeNamespace)){
							basetype=true;
							tr.setType(tr.getType().split(":")[1]);
						}
						if(realType && !basetype){
							QName qn = QName.valueOf(tr.getType());
							Node tempNode = null;
							if(tr.getType().split(":").length>1){
								tempNode =searchSchema(schemas, tr.getType().split(":")[1],QName.valueOf(tr.getType()),baseTypesNSs,nameSpaces,node);
								if(tempNode != null)
									curNode=tempNode;
							} 
							else{
								tempNode= searchSchema(schemas, tr.getType(),QName.valueOf(tr.getType()),baseTypesNSs,nameSpaces,node);
								if(tempNode != null)
									curNode = tempNode;
							}
						}
					}

					if(tr.getFlag()==ENUM){
						/*
						 * TODO: maybe I should check this tr.setName("Value");
						 */
						//					root.setType(tr.getType());

						root.setFlag(tr.getFlag());

						if(curNode.getAttributes().getNamedItem("base")!=null)
							root.setBase(curNode.getAttributes().getNamedItem("base").getNodeValue().split(":")[1]);

						tr.setName("Value");
					}

					tr.setFather(root);
					tr.setChildren(new LinkedList<TreeNode>());
					root.getChildren().add(tr);


					if(!basetype){	//if it's not a base type we have to decode the complex type
						if(tr.getFlag()==ENUM){
							root = createTree(root,curNode,baseTypesNSs,schemas,nameSpaces);

						}else{
							tr = createTree(tr,curNode,baseTypesNSs,schemas,nameSpaces);

							/*
							 * Handling extensions here
							 */
							if((extensionCurNode.getNodeName().split(":").length>1 && extensionCurNode.getNodeName().split(":")[1].equals("extension") ) ||
									extensionCurNode.getNodeName().equals("extension") ){

								String base ="";

								tr.setFlag(EXTENSION);

								TreeNode trFather = tr.getFather();
								while(trFather.getType().equals("notArealType")){
									trFather = trFather.getFather();
								}

								if(extensionCurNode.getAttributes().getNamedItem("base")!=null){
									if(extensionCurNode.getAttributes().getNamedItem("base").getNodeValue().split(":").length>1)
										base = extensionCurNode.getAttributes().getNamedItem("base").getNodeValue().split(":")[1];
									else
										base = extensionCurNode.getAttributes().getNamedItem("base").getNodeValue();
								}

								Node searchSchemaResult = searchSchema(schemas,base,QName.valueOf(base),baseTypesNSs,nameSpaces,node);
								TreeNode extRoot = new TreeNode(trFather.getName(), trFather.getFullName(), base, null, new LinkedList<TreeNode>(), NOT_INITIALIZED, new LinkedList<String>(),null);
								extRoot = createTree(extRoot,searchSchemaResult,baseTypesNSs,schemas,nameSpaces);

								//							tmpNode = extRoot;
								//							while(extRoot.getChildren().get(0).getType().equals("notArealType")){
								//								extRoot = extRoot.getChildren().get(0);
								//							}

								for(int c = 0;c<extRoot.getChildren().size();c++){
									extRoot.getChildren().get(c).setFather(tr);
									extRoot.getChildren().get(c).setFullName(tr.getFullName()+"_"+extRoot.getChildren().get(c).getName());
									tr.getChildren().add(extRoot.getChildren().get(c));	
								}
							}
						}
					}
				}
			}
		}
		return root;
	}

	private static void enumHandler(Node node, TreeNode treeNode){

		NodeList nl = node.getChildNodes();
		for(int i=0;i<nl.getLength();i++){
			if(nl.item(i).getNodeType()==ELEMENT_NODE){
				if(nl.item(i).getNodeName().split(":")[1].equalsIgnoreCase("enumeration")){
					NamedNodeMap attributes = nl.item(i).getAttributes();
					Node val = attributes.getNamedItem("value");
					if(val!=null)
						treeNode.getEnumIndex().add(val.getNodeValue());
				}
			}
		}
	}

	private static void arrayHandler(
			TreeNode node,
			Writer output, 
			String fatherName, 
			int arrayCtr, 
			boolean flag,
			boolean isFakeRun,
			QName serviceQName
	) throws IOException{

		if(flag){
			output.write("\t\t"+Utilities.capitalizeFirst(node.getType().split(":")[0])+"[]");
			output.write(" _"+node.getFullName()+"Array_ = _"+fatherName+".get"+Utilities.capitalizeFirst(node.getName())+"();\n");

			output.write("\t\tif( _"+node.getFullName()+"Array_ "+" != null ){\n");
			output.write("\t\t\tfor(int i_"+arrayCtr+"=0;i_"+arrayCtr+"<_"+node.getFullName()+"Array_.length;i_"+arrayCtr+"++){\n");

			if(Utilities.getAxisToJavaMapping(node.getType())!=null){
				Utilities.returns_typesMapingMap.put(node.getName(),Utilities.getAxisToJavaMapping(node.getType()));
				output.write("\t\t\t\t"+Utilities.getAxisToJavaMapping(node.getType()));
			}
			else{
				Utilities.returns_typesMapingMap.put(node.getName(),node.getType());
				output.write("\t\t\t\t"+Utilities.capitalizeFirst(node.getType()));
			}
			output.write(" _"+node.getFullName()+" = _"+node.getFullName()+"Array_[i_"+arrayCtr+"];\n");

			output.write("sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),\n\t\t\t\t\t" +
					"(java.lang.String)"+Utilities.capitalizeFirst(serviceQName.getLocalPart())+"TableList.getTableByName(tableName).getReturns().get(\""+node.getName()+"\")).getSqlType();\n");
			output.write("data.put(\""+node.getName()+"\", new JdbcObject(_"+node.getFullName()+", sqlType));\n");

			if( arrayCtr==maxArrayCtr ){
				output.write("\n");
				output.write("\t\ttry{\n");
				output.write("\t\t\tDataBaseTools.insertData(new DataProcessingInfo(ApplicationData.getTempDataBase().getDataBaseInfo(), ti.getTableName(), ti.getRecords(),ApplicationData.getTempJDBC()), data);\n");
				output.write("\t\t} catch (Exception e) {\n");
				output.write("\t\t\te.printStackTrace();\n");
				output.write("\t\t}\n");
				output.write("//remove from map here\n");
				isToTempDB = true;
			}

			output.write("\t\t\t}\n");
			output.write("\t\t}\n");

		}else{
			output.write("\t\t"+Utilities.capitalizeFirst(node.getType().split(":")[1]+"[]"));
			output.write(" _"+node.getFullName()+"= _"+fatherName+".get"+Utilities.capitalizeFirst(node.getName())+"();\n");
			output.write("\t\tif( _"+node.getFullName()+" != null ){\n");

			output.write("\t\t\tfor(int i_"+arrayCtr+"=0;i_"+arrayCtr+"<_"+node.getFullName()+".length;i_"+arrayCtr+"++){\n");

			LinkedList<TreeNode> children = new LinkedList<TreeNode>();
			LinkedList<TreeNode> tmpChildren = new LinkedList<TreeNode>();
			boolean extension = false;

			/*
			 * TODO: Not very sure about this
			 */
			TreeNode tmpTreeNode = node.getChildren().get(0);

			while(tmpTreeNode.getChildren().get(0).getType().equals("notArealType") && tmpTreeNode.getChildren().size()<2){
				tmpTreeNode = tmpTreeNode.getChildren().get(0);
				extension = true;
			}

			tmpChildren = tmpTreeNode.getChildren();

			if(extension){
				for(int a=0;a<tmpTreeNode.getChildren().size();a++){
					children.addAll(tmpTreeNode.getChildren().get(a).getChildren());
				}
			}
			else{
				children = tmpChildren;
			}

			for(int i=0;i<children.size();i++){
				DFSVisitOutput(children.get(i), output, true, true, arrayCtr,false,isFakeRun,serviceQName);
			}

			if( arrayCtr==maxArrayCtr ){
				output.write("\n");
				output.write("\t\ttry{\n");
				output.write("\t\t\tDataBaseTools.insertData(new DataProcessingInfo(ApplicationData.getTempDataBase().getDataBaseInfo(), ti.getTableName(), ti.getRecords(),ApplicationData.getTempJDBC()), data);\n");
				output.write("\t\t} catch (Exception e) {\n");
				output.write("\t\t\te.printStackTrace();\n");
				output.write("\t\t}\n");
				output.write("//remove from map here\n");
				isToTempDB = true;
			}

			output.write("\t\t\t}\n");
			output.write("\t\t}\n");
		}
	}

	private static Node searchSchema(List<Schema> schemas,String localName,QName partTypeQname,LinkedList<String> baseTypesNSs,Map nameSpaces,Node thisNode){

		/*
		 * Here we have to find the right schema for the part
		 */
		boolean schemaFound = false;
		Node typeNode = null;
		
		int schemaCtr = 0;
		
		while(!schemaFound && schemaCtr<schemas.size()){	//for each schema
			
			/*
			 * first look to the inline schema
			 */
			Element schemaElement = schemas.get(schemaCtr).getElement();
			
			NodeList nl = schemaElement.getChildNodes();

			for (int i=0;i<nl.getLength();i++){
				Node curNode = nl.item(i);
				if(curNode.getNodeType()==ELEMENT_NODE){
					NamedNodeMap attrs = curNode.getAttributes();
					if(attrs.getNamedItem("name")!=null && attrs.getNamedItem("name").getNodeValue().equals(localName) && curNode != thisNode){
						/*
						 * TODO: Check the condition (... & curNode != thisNode)
						 */
						schemaFound = true;
						typeNode = nl.item(i);
						break;
					}					
				}
			}
			/*
			 * then look to the imports
			 */
			if(!schemaFound){

				Schema refSchema = null;
				Map imports = schemas.get(schemaCtr).getImports();
				String NameSpaceURI =partTypeQname.getNamespaceURI();

				if(NameSpaceURI.equals("")){
					NameSpaceURI = (String)nameSpaces.get(partTypeQname.getLocalPart().split(":")[0]);
				}

				List ls = (List)imports.get(NameSpaceURI);

				if(ls!=null){	//If this is null then there are no imports for ths NamespaceURI!
					for(int i=0;i<ls.size();i++){
						SchemaImport schemaImp = (SchemaImport)ls.get(i);
						refSchema = schemaImp.getReferencedSchema();
					}
					if(refSchema!=null) schemaElement = refSchema.getElement();
					nl = schemaElement.getChildNodes();

					for (int i=0;i<nl.getLength();i++){
						Node curNode = nl.item(i);
						if(curNode.getNodeType()==ELEMENT_NODE){
							NamedNodeMap attrs = curNode.getAttributes();
							if(attrs.getNamedItem("name")!=null && attrs.getNamedItem("name").getNodeValue().equals(localName) && curNode != thisNode){
								/*
								 * TODO: Check the condition (... & curNode != thisNode)
								 */
								schemaFound=true;
								typeNode = nl.item(i);
								break;
							}					
						}
					}
					/*
					 * here we add undiscovered XMLSchemaNameSpace references
					 * 		 to baseTypesNSs
					 */
					boolean nsExists = false;
					NamedNodeMap attrs = schemaElement.getAttributes();
					for(int i=0;i<attrs.getLength();i++){
						Node curAtt = attrs.item(i);
						if(curAtt.getNodeName().split(":")[0].equalsIgnoreCase("xmlns")){
							if(curAtt.getNodeValue().equals(XMLSchemaNameSpace)){
								for(int j=0;j<baseTypesNSs.size();j++){
									if(baseTypesNSs.get(j).equals(curAtt.getNodeName().split(":")[1])){
										nsExists=true;
										break;
									}
								}
								if(!nsExists){
									baseTypesNSs.add(curAtt.getNodeName().split(":")[1]);
								}
							}
						}
					}
				}
			}
			schemaCtr++;
		}
		return typeNode;
	}

	/**
	 * this method visits each tree node and generates code
	 * @param node
	 * @return return true for success false for failure
	 * 
	 * @author apon
	 */
	private static boolean DFSVisitInput(TreeNode node,Writer output) throws IOException{

		node.setColor(GRAY);

		for(int i=0;i<node.getChildren().size();i++){
			if(node.getChildren().get(i).getColor()==WHITE){
				DFSVisitInput(node.getChildren().get(i),output);
			}
		}

		if(node.getChildren().size()==0){	//it's a leaf
			if(node.getType().equals("notArealType")){	//Operation has no arguments
				return true;
			}
			if(node.getFlag()!=ENUM){

				String axis2javaType = null;
				axis2javaType = Utilities.getAxisToJavaMapping(node.getType());

				if(axis2javaType!=null){

					Utilities.inputs_typesMapingMap.put(node.getName(), axis2javaType);

					if(axis2javaType.equals("java.util.Calendar")){
						/*
						 * TODO: This is a "preApatar" version and needs fixing!
						 */
						
						output.write("CALENDAR IS NOT READY YET!\n");
						output.write("\t\tjava.util.Calendar _"+node.getFullName()+" = java.util.Calendar.getInstance();\n");
						output.write("\t\t_"+node.getFullName()+".set(1,Integer.parseInt(args["+(argsCtr++)+"]));\n");
						output.write("\t\t_"+node.getFullName()+".set(2,Integer.parseInt(args["+(argsCtr++)+"]));\n");

						output.write("\t\t_"+node.getFullName()+".set(3,Integer.parseInt(args["+(argsCtr++)+"]));\n");
						output.write("\t\t_"+node.getFullName()+".set(4,Integer.parseInt(args["+(argsCtr++)+"]));\n");

						output.write("\t\t_"+node.getFullName()+".set(5,Integer.parseInt(args["+(argsCtr++)+"]));\n");
						output.write("\t\t_"+node.getFullName()+".set(6,Integer.parseInt(args["+(argsCtr++)+"]));\n");
					}
					else{
						output.write("\t\t"+axis2javaType);
						output.write(" _"+node.getFullName()+" = null;\n");
						output.write("\t\tif( argVals.get(\""+node.getName()+"\") != null)\n");
						output.write("\t\t\t _"+node.getFullName()+" = new "+ axis2javaType+"(argVals.get(\""+node.getName()+"\").toString()");							
						output.write(");\n");
					}
				}
				else{ //axis2javaType==null)
					Utilities.inputs_typesMapingMap.put(node.getName(), node.getType());

					output.write("\t\t"+Utilities.capitalizeFirst(node.getType()));
					output.write(" _"+node.getFullName()+" = null;\n");
					output.write("\t\tif( argVals.get(\""+node.getName()+"\") != null)\n");
					output.write("\t\t\t_"+node.getFullName()+" = new "+Utilities.capitalizeFirst(node.getType())+"(argVals.get(\""+node.getName()+"\")");
					if(Utilities.javaToApatarSQL_MapingMap.get(Utilities.capitalizeFirst(node.getType()))!=null)
						if(Utilities.javaToApatarSQL_MapingMap.get(Utilities.capitalizeFirst(node.getType())).equals("ERecordType.Numeric")){
							output.write(".toString());\n");
						}
						else
							output.write(");\n");
				}

			}
		}
		else if(!node.getType().equals("notArealType")){

			if(node.getFlag()==ENUM){
				Utilities.inputs_typesMapingMap.put(node.getName(), Utilities.getAxisToJavaMapping(node.getBase()));

				output.write("\t\t"+Utilities.capitalizeFirst(node.getType().split(":")[1]));
				output.write(" _"+node.getFullName()+" = null;\n");
				output.write("\t\tif( argVals.get(\""+node.getName()+"\") != null)\n");

				output.write("\t\t\t_"+node.getFullName()+"= new "+Utilities.capitalizeFirst(node.getType().split(":")[1])+"(("+Utilities.getAxisToJavaMapping(node.getBase())+")argVals.get(\""+node.getName()+"\"),true);\n");
			}
			else{
				output.write("\t\t"+Utilities.capitalizeFirst(node.getType().split(":")[1]));
				output.write(" _"+node.getFullName()+"= new "+Utilities.capitalizeFirst(node.getType().split(":")[1])+"();\n");

				/*
				 * TODO: FIX this PAPATZILIKI.
				 * if node is "notArealType" there is a problem so
				 * we do this "trick" to take "notArealType"'s children
				 * 
				 * Also, children.size must not be used. Should check
				 * child's type..
				 * 
				 * maybe all children must be checked
				 * ie
				 * <element: name="fooHead">
				 * 	<sequence>...</sequence>
				 * 	<element: name="foo" type="string" />
				 * </element>
				 * 
				 */
				LinkedList<TreeNode> children = new LinkedList<TreeNode>();
				children = node.getChildren();
				while(children.get(0).getType().equals("notArealType")){
					children = children.get(0).getChildren();
				}

				/*
				 * Here we handle arrays at the input
				 * We just fill with data the first
				 * array record (fooArray[0])
				 */
				if(node.getType().split(":")[1].startsWith("ArrayOf")){		

					/*
					 * The size of the children list must be 1
					 * TODO: check if this is correct
					 */
					if(children!=null && children.size()>0){
						output.write("\t\t"+Utilities.capitalizeFirst(children.get(0).getType().split(":")[1])+"[]");
						output.write(" _"+node.getFullName()+"_array = _"+node.getFullName()+".get"+Utilities.capitalizeFirst(children.get(0).getType().split(":")[1])+"();\n");

						//Continue from here

						TreeNode child = node;
						for(int i=0;i<1;i++){
							child = child.getChildren().get(0);
							while(child.getType().equalsIgnoreCase("notArealType")){
								child = child.getChildren().get(0);
							}
						}

						/*
						 * TODO: FIX this PAPATZILIKI.
						 * if node is "notArealType" there is a problem so
						 * we do this "trick" to take "notArealType"'s children
						 * 
						 * Also, children.size must not be used. Should check
						 * child's type..
						 * 
						 */
						if(child.getChildren().size()==1){
							children = child.getChildren().get(0).getChildren();
						}
						else{
							children = child.getChildren();
						}

						for(int i=0;i<children.size();i++){
							output.write("\t\t_"+node.getFullName()+"_array[0].set"+Utilities.capitalizeFirst(children.get(i).getName())+"(_"+children.get(i).getFullName()+");\n");
						}
						output.write("\t\t_"+node.getFullName()+".set"+Utilities.capitalizeFirst(child.getName())+"( _"+node.getFullName()+"_array );\n");
					}
				}else{
					output.write("\n");
					for(int i=0;i<children.size();i++){
						output.write("\t\t_"+node.getFullName()+".set"+Utilities.capitalizeFirst(children.get(i).getName())+"(_"+children.get(i).getFullName()+");\n");
					}
					output.write("\n");
				}
			}
		}

		node.setColor(BLACK);

		return true;
	}

	private static boolean DFSVisitOutput(
			TreeNode node,
			Writer output,
			boolean calledFromArrayHandler, 
			boolean handlingArray,
			int arrayCtr, 
			boolean flag, 
			boolean isFakeRun,
			QName serviceQName
	) throws IOException{

		if(!isFakeRun) node.setColor(GRAY);

		TreeNode tmpTreeNode = node;
		String fatherFullName = "";
		String fatherName = "";

		while(tmpTreeNode.getFather()!=null && tmpTreeNode.getFather().getType().equals("notArealType")){
			tmpTreeNode = tmpTreeNode.getFather();
		}
		if(tmpTreeNode.getFather()!=null){
			fatherName = tmpTreeNode.getFather().getName();
			fatherFullName=tmpTreeNode.getFather().getFullName();
		}

		if(node.getChildren().size()==0){	//it's a leaf
			if(flag){
				arrayHandler(node,output,fatherFullName,++arrayCtr,flag,isFakeRun,serviceQName);
			}
			else if(node.getFlag() == XS_ANY){
				Utilities.returns_typesMapingMap.put(fatherName, "java.lang.String");
				output.write("\t\tOMElement _"+node.getFullName()+" = _"+fatherFullName+".getExtraElement();\n");
				output.write("\t\tsqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),\n\t\t\t\t\t" +
						"(java.lang.String)"+Utilities.capitalizeFirst(serviceQName.getLocalPart())+"TableList.getTableByName(tableName).getReturns().get(\""+fatherName+"\")).getSqlType();\n");
				output.write("\t\tdata.put(\""+fatherName+"\", new JdbcObject(_"+node.getFullName()+".toString(), sqlType));\n");	//toString??

				if(calledFromArrayHandler) output.write("//add to map here\n");
			}
			else{

				if(handlingArray){
					output.write("\t");
				}

				if(Utilities.getAxisToJavaMapping(node.getType())!=null){
					Utilities.returns_typesMapingMap.put(node.getName(),Utilities.getAxisToJavaMapping(node.getType()));
					output.write("\t\t"+Utilities.getAxisToJavaMapping(node.getType()));
				}
				else{
					Utilities.returns_typesMapingMap.put(node.getName(),node.getType());
					output.write("\t\t"+Utilities.capitalizeFirst(node.getType()));
				}

				if(Utilities.isReserved(node.getName())){		//reserved word
					if(handlingArray)
						output.write(" _"+node.getFullName()+" = _"+fatherFullName+"[i_"+arrayCtr+"].get_"+node.getName()+"();\n");
					else
						output.write(" _"+node.getFullName()+" = _"+fatherFullName+".get_"+node.getName()+"();\n");
				}
				else{	//not reserved word
					if(handlingArray)
						output.write(" _"+node.getFullName()+" = _"+fatherFullName+"[i_"+arrayCtr+"].get"+Utilities.capitalizeFirst(node.getName())+"();\n");
					else
						output.write(" _"+node.getFullName()+" = _"+fatherFullName+".get"+Utilities.capitalizeFirst(node.getName())+"();\n");
				}

				if(Utilities.getAxisToJavaMapping(node.getType())!=null && Utilities.getAxisToJavaMapping(node.getType()).equals("java.util.Calendar")){
					output.write("\t\tjava.lang.Integer _"+node.getFullName()+"_year = _"+node.getFullName()+".get(1);\n");
					output.write("System.out.println(\"_"+node.getFullName()+"_year: \"+_"+node.getFullName()+"_year);\n");

					output.write("\t\tjava.lang.Integer _"+node.getFullName()+"_month = _"+node.getFullName()+".get(2);\n");
					output.write("System.out.println(\"_"+node.getFullName()+"_month: \"+_"+node.getFullName()+"_month);\n");

					output.write("\t\tjava.lang.Integer _"+node.getFullName()+"_day = _"+node.getFullName()+".get(3);\n");
					output.write("System.out.println(\"_"+node.getFullName()+"_day: \"+_"+node.getFullName()+"_day);\n");

					output.write("\t\tjava.lang.Integer _"+node.getFullName()+"_hour = _"+node.getFullName()+".get(4);\n");
					output.write("System.out.println(\"_"+node.getFullName()+"_hour: \"+_"+node.getFullName()+"_hour);\n");

					output.write("\t\tjava.lang.Integer _"+node.getFullName()+"_minute = _"+node.getFullName()+".get(5);\n");
					output.write("System.out.println(\"_"+node.getFullName()+"_minute: \"+_"+node.getFullName()+"_minute);\n");

					output.write("\t\tjava.lang.Integer _"+node.getFullName()+"_second = _"+node.getFullName()+".get(6);\n");
					output.write("System.out.println(\"_"+node.getFullName()+"_second: \"+_"+node.getFullName()+"_second);\n");
				}
				else{
					output.write("sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),\n\t\t\t\t\t" +
							"(java.lang.String)"+Utilities.capitalizeFirst(serviceQName.getLocalPart())+"TableList.getTableByName(tableName).getReturns().get(\""+node.getName()+"\")).getSqlType();\n");
					output.write("data.put(\""+node.getName()+"\", new JdbcObject(_"+node.getFullName()+", sqlType));\n");

					if(calledFromArrayHandler)output.write("//add to map here\n");
				}
			}
		}
		else if(!node.getType().equals("notArealType")){	//It's a real type
			boolean arrayInArray = false;

			if(!node.getType().equals("")){
				if( ((node.getType().split(":").length>1 && node.getType().split(":")[1].startsWith("ArrayOf")) || 
						node.getType().split(":").length==1 && node.getType().startsWith("ArrayOf")) && handlingArray == true){
					handlingArray = false;
					arrayInArray = true;
				}
				if(calledFromArrayHandler){
					output.write("\t\t"+Utilities.capitalizeFirst(node.getType().split(":")[1]));
					output.write(" _"+node.getFullName()+"= _"+fatherFullName+"[i_"+arrayCtr+"].get"+Utilities.capitalizeFirst(node.getName())+"();\n");
				}
				else if(handlingArray){
					arrayHandler(node,output,fatherFullName,++arrayCtr,flag,isFakeRun,serviceQName);					
				}
				else if(arrayInArray){
					output.write("\t\t"+Utilities.capitalizeFirst(node.getType().split(":")[1]));
					output.write(" _"+node.getFullName()+"= _"+fatherFullName+"[i_"+arrayCtr+"].get"+Utilities.capitalizeFirst(node.getName())+"();\n");
				}
				else{
					if(Utilities.isReserved(node.getName())){
						if(node.getType().split(":").length>1){
							output.write("\t\t"+Utilities.capitalizeFirst(node.getType().split(":")[1]));
						}else{
							output.write("\t\t"+Utilities.capitalizeFirst(node.getType()));
						}
						output.write(" _"+node.getFullName()+"= _"+fatherFullName+".get_"+node.getName()+"();\n");
					}
					else{
						if(node.getType().split(":").length>1){
							output.write("\t\t"+Utilities.capitalizeFirst(node.getType().split(":")[1]));
						}else{
							output.write("\t\t"+Utilities.capitalizeFirst(node.getType()));
						}
						output.write(" _"+node.getFullName()+"= _"+fatherFullName+".get"+Utilities.capitalizeFirst(node.getName())+"();\n");
					}

				}

				if( (node.getType().split(":").length>1 && node.getType().split(":")[1].startsWith("ArrayOf")) || 
						node.getType().split(":").length==1 && node.getType().startsWith("ArrayOf") ){

					handlingArray=true;
					TreeNode tmpN = node.getChildren().get(0);
					while(tmpN.getType().equals("notArealType"))
						tmpN=tmpN.getChildren().get(0);

					if((tmpN.getChildren()==null || tmpN.getChildren().size()==0)){
						flag = true;
					}
				}
				else{
					handlingArray=false;
				}
			}
		}

		for(int i=0;i<node.getChildren().size();i++){
			if(node.getChildren().get(i).getColor()==WHITE){
				DFSVisitOutput(node.getChildren().get(i),output,false,handlingArray,arrayCtr,flag,isFakeRun,serviceQName);
			}
		}
		if(!isFakeRun) node.setColor(BLACK);

		if(isFakeRun) maxArrayCtr = (maxArrayCtr > arrayCtr) ? maxArrayCtr : arrayCtr;

		return true;
	}

	private static void checkOverloads(Definition def){
		/*
		 * TODO: add code here!
		 */
	}

	/**
	 * This function creates the file structure 
	 * of the plugin's folder.
	 * It also creates the plugin.xml file and its contents
	 * 
	 * @param serviceQName
	 * @return plugin's root folder
	 */
	private static String createPluginFileStructure(QName serviceQName){
		String serviceName = serviceQName.getLocalPart();
		String folderName = "";
		String[] splittedQName = serviceQName.toString().split("//");

		if (splittedQName[1]!=null){
			folderName=splittedQName[1].replace(".", "_").replace("}", "_");
		}else{
			folderName=splittedQName[0].replace(".", "_").replace("}", "_").replace("{", "");
		}

		try {
			//create src folder
			File f = new File("plugins/connectors/"+folderName+"/src");
			f.mkdirs();
			//create bin folder
			f = new File("plugins/connectors/"+folderName+"/bin");
			f.mkdirs();

			f = new File("plugins/connectors/"+folderName+"/plugin.xml");

			Writer output = new BufferedWriter(new FileWriter(f));
			output.write("<?xml version=\"1.0\" ?>\n");
			output.write("<!DOCTYPE plugin PUBLIC \"-//JPF//Java Plug-in Manifest 1.0\" \"http://jpf.sourceforge.net/plugin_1_0.dtd\">\n");
			output.write("<plugin id=\"tuc.ws."+serviceName.toLowerCase()+"\" version=\"0.0.1\">\n");
			output.write("\t<requires>\n");
			output.write("\t\t<import plugin-id=\"com.apatar.core\"/>\n");
			output.write("\t\t<import plugin-id=\"com.apatar.ui\"/>\n");
			output.write("\t</requires>\n");
			output.write("\t<runtime>\n");
			output.write("\t\t<library id=\""+serviceName.toLowerCase()+"\" path=\"bin/\" type=\"code\">\n");
			output.write("\t\t\t<export prefix=\"*\"/>\n");
			output.write("\t\t</library>\n");
			output.write("\t</runtime>\n");
			output.write("\t<extension plugin-id=\"com.apatar.core\" point-id=\"Node\" id=\""+Utilities.capitalizeFirst(serviceName)+"Node\""+">\n");
			output.write("\t\t<parameter id=\"class\" value=\"tuc.ws."+Utilities.lowerFirst(serviceName)+"."+Utilities.capitalizeFirst(serviceName)+"NodeFactory\"/>\n");
			output.write("\t</extension>\n");
			output.write("</plugin>\n");

			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "plugins/connectors/"+folderName;
	}

	/**
	 * This function creates the code of 
	 * "SERVICE_LOCAL_NAME"ConnectorNodeFactory.java file
	 * 
	 * @param folderName
	 * @param serviceName
	 */
	private static void createNodeFactory(String folderName,String packName, String serviceName){

		try{
			File f = new File(folderName+"/src/"+packName.replace(".", "/"));
			f.mkdirs();
			f = new File(folderName+"/src/"+packName.replace(".", "/")+"/"+Utilities.capitalizeFirst(serviceName)+"NodeFactory.java");

			Writer output = new BufferedWriter(new FileWriter(f));

			output.write("/**\n");
			output.write(" *\n");
			output.write(" * this is auto generated code\n");
			output.write(" *\n");
			output.write(" */\n\n");

			output.write("package "+packName+";\n");
			output.write("\n");
			output.write("import java.awt.Color;\n");
			output.write("import java.util.ArrayList;\n");
			output.write("import java.util.List;\n");
			output.write("\n");
			output.write("import javax.swing.ImageIcon;\n");
			output.write("import javax.swing.JLabel;\n");
			output.write("\n");
			output.write("import com.apatar.core.AbstractNode;\n");
			output.write("import com.apatar.ui.NodeFactory;\n");
			output.write("\n");

			output.write("public class "+Utilities.capitalizeFirst(serviceName)+"NodeFactory extends NodeFactory{\n");
			output.write("\n");
			output.write("\tpublic boolean MainPaneNode() {\n");
			output.write("\t\treturn true;\n");
			output.write("\t}\n");

			output.write("\tpublic AbstractNode createNode() {\n");
			output.write("\t\treturn new "+Utilities.capitalizeFirst(serviceName)+"Node();\n");
			output.write("\t}\n");

			output.write("\tpublic List<String> getCategory() {\n");
			output.write("\t\tList<String> res = new ArrayList<String>();\n");
			output.write("\t\tres.add(\"Web Services\");\n");
			output.write("\t\treturn res;\n");
			output.write("\t}\n");

			output.write("\tpublic int getHorizontalTextPosition() {\n");
			output.write("\t\treturn JLabel.CENTER;\n");
			output.write("\t}\n");

			output.write("\tpublic ImageIcon getIcon() {\n");
			output.write("\t\treturn "+Utilities.capitalizeFirst(serviceName)+"Utils."+serviceName.toUpperCase()+"_ICON;\n");
			output.write("\t}\n");

			output.write("\tpublic String getNodeClass() {\n");
			output.write("\treturn "+Utilities.capitalizeFirst(serviceName)+"Node.class.getName();\n");
			output.write("\t}\n");

			output.write("\tpublic Color getTextColor() {\n");
			output.write("\t\treturn Color.BLACK;\n");
			output.write("\t}\n");

			output.write("\tpublic String getTitle() {\n");
			output.write("\t\treturn \"" +Utilities.lowerFirst(serviceName)+"\";\n");
			output.write("}\n");

			output.write("\tpublic int getVerticalTextPosition() {\n");
			output.write("\t\treturn JLabel.BOTTOM;\n");
			output.write("\t}\n");

			output.write("}\n");

			output.close();

		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private static void createUtils(String folderName, String packageName, String serviceName){

		try{
			File f = new File(folderName+"/src/"+packageName.replace(".", "/"));
			f.mkdirs();
			f = new File(folderName+"/src/"+packageName.replace(".", "/")+"/"+Utilities.capitalizeFirst(serviceName)+"Utils.java");

			Writer output = new BufferedWriter(new FileWriter(f));

			output.write("/**\n");
			output.write(" *\n");
			output.write(" * this is auto generated code\n");
			output.write(" *\n");
			output.write(" */\n\n");

			output.write("package "+packageName+";\n");
			output.write("\n");

			output.write("import javax.swing.ImageIcon;\n");
			output.write("\n");

			output.write("public class "+Utilities.capitalizeFirst(serviceName)+"Utils {\n");

			output.write("\tpublic static final ImageIcon "+serviceName.toUpperCase()+"_ICON = " +
					"\n\t\tnew ImageIcon("+Utilities.capitalizeFirst(serviceName)+"NodeFactory.class.getResource(\"16-"+serviceName+"Icon.png\"));\n");

			output.write("\tpublic static final ImageIcon "+serviceName.toUpperCase()+"_NODE_ICON = " +
					"\n\t\tnew ImageIcon("+Utilities.capitalizeFirst(serviceName)+"NodeFactory.class.getResource(\"32-"+serviceName+"Icon.png\"));\n");

			output.write("}\n");

			output.close();

		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private static void createTable(String folderName,String packageName, String serviceName){

		try{
			File f = new File(folderName+"/src/"+packageName.replace(".", "/"));
			f.mkdirs();
			f = new File(folderName+"/src/"+packageName.replace(".", "/")+"/"+Utilities.capitalizeFirst(serviceName)+"Table.java");

			Writer output = new BufferedWriter(new FileWriter(f));

			output.write("/**\n");
			output.write(" *\n");
			output.write(" * this is auto generated code\n");
			output.write(" *\n");
			output.write(" */\n\n");

			output.write("package "+packageName+";\n");
			output.write("\n");

			output.write("import java.util.HashMap;\n");
			output.write("import java.util.Map;\n");
			output.write("\n");

			output.write("import com.apatar.core.ETableMode;\n");
			output.write("\n");

			output.write("public class "+Utilities.capitalizeFirst(serviceName)+"Table {\n");
			output.write("\n");
			output.write("\tprivate String tableName = \"\";\n");

			output.write("\t/*\n");
			output.write("\t * will hold the arguments of webservice's operation\n");
			output.write("\t * key is arg's name (String), value is arg's type (String)\n");
			output.write("\t */\n");
			output.write("\tprivate Map<String, Object> arguments 	  = 	new HashMap<String, Object>();\n");

			output.write("\t/*\n");
			output.write("\t * will hold the returns of webservice's operation\n");
			output.write("\t * key is ret's name (String), value is ret's type (Object)\n");
			output.write("\t */\n");
			output.write("\tprivate Map<String, Object> returns      =		new HashMap<String, Object>();\n");

			output.write("\n");
			output.write("\tprivate ETableMode mode	= ETableMode.ReadWrite;\n");
			output.write("\n");
			output.write("\tpublic "+Utilities.capitalizeFirst(serviceName)+"Table(String tableName){\n");
			output.write("\t\tthis.tableName 	  = 	tableName;\n");
			output.write("\t}\n");
			output.write("\n");

			output.write("\tpublic "+Utilities.capitalizeFirst(serviceName)+"Table(String tableName, Map<String, Object> arguments, Map<String,Object> returns){\n");
			output.write("\t\tthis.tableName = tableName;\n");
			output.write("\t\tthis.arguments.putAll(arguments);\n");
			output.write("\t\tthis.returns.putAll(returns);\n");
			output.write("\t}\n");
			output.write("\n");

			output.write("\tpublic String getTableName() {\n");
			output.write("\t\treturn tableName;\n");
			output.write("\t}\n");
			output.write("\n");

			output.write("\tpublic void setTableName(String tableName) {\n");
			output.write("\t\tthis.tableName = tableName;\n");
			output.write("\t}\n");
			output.write("\n");

			output.write("\tpublic Map<String, Object> getArguments() {\n");
			output.write("\t\treturn arguments;\n");
			output.write("\t}\n");
			output.write("\n");

			output.write("\tpublic void setArguments(Map<String, Object> arguments) {\n");
			output.write("\t\tthis.arguments = arguments;\n");
			output.write("\t}\n");
			output.write("\n");

			output.write("\tpublic Map<String, Object> getReturns() {\n");
			output.write("\t\treturn returns;\n");
			output.write("\t}\n");
			output.write("\n");

			output.write("\tpublic void setReturns(Map<String, Object> returns) {\n");
			output.write("\t\tthis.returns = returns;\n");
			output.write("\t}\n");
			output.write("\n");

			output.write("\tpublic ETableMode getMode() {\n");
			output.write("\t\treturn mode;\n");
			output.write("\t}\n");
			output.write("\n");

			output.write("\tpublic void setMode(ETableMode mode) {\n");
			output.write("\t\tthis.mode = mode;\n");
			output.write("\t}\n");
			output.write("\n");

			output.write("}\n");

			output.close();

		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private static void createNode(String folderName,String packageName, String serviceName, HashSet<String> types){

		try{
			File f = new File(folderName+"/src/"+packageName.replace(".", "/"));
			f.mkdirs();
			f = new File(folderName+"/src/"+packageName.replace(".", "/")+"/"+Utilities.capitalizeFirst(serviceName)+"Node.java");

			Writer output = new BufferedWriter(new FileWriter(f));

			output.write("/**\n");
			output.write(" *\n");
			output.write(" * this is auto generated code\n");
			output.write(" *\n");
			output.write(" */\n\n");

			output.write("package "+packageName+";\n");
			output.write("\n");

			output.write("import java.lang.reflect.InvocationTargetException;\n");
			output.write("import java.lang.reflect.Method;\n");
			output.write("import java.sql.ResultSet;\n");
			output.write("import java.sql.SQLException;\n");
			output.write("import java.util.ArrayList;\n");
			output.write("import java.util.HashMap;\n");
			output.write("import java.util.List;\n");

			output.write("\n");

			output.write("import javax.swing.ImageIcon;\n");
			output.write("import javax.swing.JDialog;\n");
			
			output.write("\n");
			
			output.write("import org.jdom.Element;\n");

			output.write("\n");

			output.write("import com.apatar.core.AbstractApatarActions;\n");
			output.write("import com.apatar.core.AbstractNonJdbcDataBaseNode;\n");
			output.write("import com.apatar.core.ApplicationData;\n");
			output.write("import com.apatar.core.DBTypeRecord;\n");
			output.write("import com.apatar.core.DataBaseInfo;\n");
			output.write("import com.apatar.core.DataBaseTools;\n");
			output.write("import com.apatar.core.DataProcessingInfo;\n");
			output.write("import com.apatar.core.ERecordType;\n");
			output.write("import com.apatar.core.JdbcObject;\n");
			output.write("import com.apatar.core.KeyInsensitiveMap;\n");
			output.write("import com.apatar.core.RDBTable;\n");
			output.write("import com.apatar.core.Record;\n");
			output.write("import com.apatar.core.SchemaTable;\n");
			output.write("import com.apatar.core.TableInfo;\n");

			output.write("\n");

			output.write("import com.apatar.ui.wizard.DBConnectionDescriptor;\n");
			output.write("import com.apatar.ui.wizard.RecordSourceDescriptor;\n");
			output.write("import com.apatar.ui.wizard.TableModeDescriptor;\n");
			output.write("import com.apatar.ui.wizard.Wizard;\n");
			output.write("import com.apatar.ui.wizard.WizardPanelDescriptor;\n");

			output.write("\n");

			output.write("public class "+Utilities.capitalizeFirst(serviceName)+"Node extends AbstractNonJdbcDataBaseNode{\n");

			output.write("\n");
			output.write("\tstatic DataBaseInfo dbi = new DataBaseInfo(\"\", \"\", \"\", \"\", false, true,false, true, false);\n");

			output.write("\n");

			output.write("\t/*\n");
			output.write("\t * These maps will hold the values of the\n");
			output.write("\t * operations arguments and returns respectively.\n");
			output.write("\t * key 	 is arg/ret name  (String)\n");
			output.write("\t * value is arg/ret value (Object)\n");
			output.write("\t */\n");
			output.write("\tprivate HashMap<String, Object> argVals;\n");
			output.write("\tprivate HashMap<String, Object> retVals;\n");

			output.write("\n");

			output.write("\t/*\n");
			output.write("\t * Here availableTypes are added to DataBaseInfo\n");
			output.write("\t */\n");
			output.write("\tstatic {\n");
			output.write("\t\tList<DBTypeRecord> rcList = dbi.getAvailableTypes();\n");
			/*
			 * here goes the code for the types of the arguments
			 * or return values
			 */

			for(String type : types){
				if(Utilities.java2apatarSQL(type).split("\\.").length>1)
					output.write("\t\trcList.add(new DBTypeRecord("+Utilities.java2apatarSQL(type)+",\""+Utilities.java2apatarSQL(type).split("\\.")[1].toUpperCase()+"\",0,");
				else
					output.write("\t\trcList.add(new DBTypeRecord("+Utilities.java2apatarSQL(type)+",\""+Utilities.java2apatarSQL(type).toUpperCase()+"\",0,");
				if(Utilities.java2apatarSQL(type).equals("ERecordType.Numeric")){
					output.write("(int) Math.pow(2, 32)");
				}else{
					output.write("65000");
				}
				output.write(",false,false));\n");
			}

			output.write("\t}\n");

			output.write("\n");

			output.write("\tpublic "+Utilities.capitalizeFirst(serviceName)+"Node(){\n");
			output.write("\t\tsuper();\n");
			output.write("\t\ttitle = \""+serviceName.toLowerCase() +"\";\n");
			output.write("\n");
			output.write("\t\targVals = new HashMap<String, Object>();\n");
			output.write("\t\tretVals = new HashMap<String, Object>();\n");
			output.write("\t}\n");

			output.write("\tpublic ImageIcon getIcon(){\n");
			output.write("\t\treturn "+Utilities.capitalizeFirst(serviceName)+"Utils."+serviceName.toUpperCase()+"_NODE_ICON;\n");
			output.write("\t}\n");

			output.write("\n");

			output.write("\tprotected void TransformRDBtoTDB() {\n");
			//			output.write("\n");
			//			output.write("\t\tDataBaseTools.completeTransfer();\n");
			//			output.write("\t\tTableInfo ti = getTiForConnection(OUT_CONN_POINT_NAME);\n");
			//			output.write("\t\ttry {\n");
			//			output.write("\t\t\tti.getSchemaTable().updateRecords(getFieldList(null));\n");
			//			output.write("\t\t} catch (ClassNotFoundException e) {\n");
			//			output.write("\t\t\te.printStackTrace();\n");
			//			output.write("\t\t} catch (SQLException e) {\n");
			//			output.write("\t\t\te.printStackTrace();\n");
			//			output.write("\t\t} catch (Exception e) {\n");
			//			output.write("\t\t\te.printStackTrace();\n");
			//			output.write("\t\t}\n");
			//
			//			output.write("\n");
			//
			//			output.write("\t\tKeyInsensitiveMap data = new KeyInsensitiveMap();\n");
			//			output.write("\t\tfor(String key : retVals.keySet()){\n");
			//			output.write("\t\t\tint sqlType = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(),\n\t\t\t (String)"+Utilities.capitalizeFirst(serviceName)+"TableList.getTableByName(getTableName()).getReturns().get(key)).getSqlType();\n");
			//			output.write("\t\t\tdata.put(key, new JdbcObject(retVals.get(key), sqlType));\n");
			//			output.write("\t\t}\n");
			//
			//			output.write("\n");
			//
			//			output.write("\t\ttry{\n");
			//			output.write("\t\t\tDataBaseTools.insertData(new DataProcessingInfo(ApplicationData.getTempDataBase().getDataBaseInfo(), ti.getTableName(), ti.getRecords(),ApplicationData.getTempJDBC()), data);\n");
			//			output.write("\t\t} catch (Exception e) {\n");
			//			output.write("\t\t\te.printStackTrace();\n");
			//			output.write("\t\t}\n");
			//
			//			output.write("\n");
			//
			//			output.write("\t\tDataBaseTools.completeTransfer();\n");
			output.write("\t}\n");

			output.write("\n");

			output.write("\tprotected void TransformTDBtoRDB(int mode) {\n");
			output.write("\t\tDataBaseTools.completeTransfer();\n");
			output.write("\t\targVals.clear();\n");
			output.write("\t\tTableInfo ti = getTiForConnection(IN_CONN_POINT_NAME);\n");

			output.write("\t\tif(ti!=null){ //there are arguments\n");

			output.write("\t\t\tList<Record> recs = ti.getRecords();\n");

			output.write("\n");

			output.write("\t\t\ttry {\n");
			output.write("\t\t\t\tResultSet rs = DataBaseTools.getRSWithAllFields(ti.getTableName(),ApplicationData.getTempJDBC(), ApplicationData.getTempDataBaseInfo());\n");

			output.write("\n");

			output.write("\t\t\t\twhile (rs.next()) {\n");
			output.write("\t\t\t\t\tfor (Record rec : recs) {\n");
			output.write("\t\t\t\t\t\tObject obj = rs.getObject(rec.getFieldName());\n");
			output.write("\t\t\t\t\t\tif (obj != null) {\n");
			output.write("\t\t\t\t\t\t\targVals.put(rec.getFieldName(), obj);\n");
			output.write("\t\t\t\t\t\t}\n");
			output.write("\t\t\t\t\t}\n");
			output.write("\t\t\t\t}\n");
			output.write("\t\t\t} catch (SQLException e) {\n");
			output.write("\t\t\t\te.printStackTrace();\n");
			output.write("\t\t\t} catch (ClassNotFoundException e) {\n");
			output.write("\t\t\t\te.printStackTrace();\n");
			output.write("\t\t\t}\n");
			output.write("\t\t}\n");
			output.write("\t\tDataBaseTools.completeTransfer();\n");
			output.write("\t}\n");

			output.write("\n");

			output.write("\tpublic void Transform(){\n");
			output.write("\t\tTransformTDBtoRDB(INSERT_MODE);\n");

			output.write("\n");

			output.write("\t\ttry {\n");
			output.write("\t\t\t/*\n");
			output.write("\t\t\t * Here we select the right method to call\n");
			output.write("\t\t\t * from ConnectorFunctions,  according to the selected\n");
			output.write("\t\t\t * table (web service operation)\n");
			output.write("\t\t\t */\n");

			output.write("\t\t\tClass functionsClass = Class.forName(\""+packageName+"."+Utilities.capitalizeFirst(serviceName)+"Functions\");\n");			
			output.write("\t\t\tMethod method = functionsClass.getMethod(getTableName(),argVals.getClass()," +
			"\n\t\t\t\tgetTiForConnection(OUT_CONN_POINT_NAME).getClass(),getFieldList(null).getClass(),getTableName().getClass(),dbi.getClass());\n");
			output.write("\t\t\tmethod.invoke(functionsClass.newInstance(),argVals, getTiForConnection(OUT_CONN_POINT_NAME),getFieldList(null),getTableName(),dbi);\n");

			output.write("\t\t} catch (SecurityException e) {\n");
			output.write("\t\t\te.printStackTrace();\n");
			output.write("\t\t} catch (ClassNotFoundException e) {\n");
			output.write("\t\t\te.printStackTrace();\n");
			output.write("\t\t} catch (NoSuchMethodException e) {\n");
			output.write("\t\t\te.printStackTrace();\n");
			output.write("\t\t} catch (IllegalArgumentException e) {\n");
			output.write("\t\t\te.printStackTrace();\n");
			output.write("\t\t} catch (IllegalAccessException e) {\n");
			output.write("\t\t\te.printStackTrace();\n");
			output.write("\t\t} catch (InvocationTargetException e) {\n");
			output.write("\t\t\te.printStackTrace();\n");
			output.write("\t\t} catch (InstantiationException e) {\n");
			output.write("\t\t\te.printStackTrace();\n");
			output.write("\t\t} catch (Exception e) {\n");
			output.write("\t\t\te.printStackTrace();\n");
			output.write("\t\t} \n");

			output.write("\n");

			output.write("\t\tTransformRDBtoTDB();\n");
			output.write("\t}\n");

			output.write("\n");
	
			output.write("\t@Override\n");
			output.write("\tpublic Element saveToElement() {\n");
			output.write("\t\treturn super.saveToElement();\n");
			output.write("\t}\n");
			
			output.write("\n");
			
			output.write("\t@Override\n");
			output.write("\tpublic void initFromElement(Element e) {\n");
			output.write("\t\tsuper.initFromElement(e);\n");
			output.write("\t}\n");
			
			output.write("\n");
			
			output.write("\tpublic void createDatabaseParam(Wizard wizard) {\n");
			output.write("\t\tJDialog wizardDialog = wizard.getDialog();\n");
			output.write("\t\twizardDialog.setTitle(title + \" Property\");\n");

			output.write("\n");

			output.write("\t\ttry {\n");
			output.write("\t\t\tWizardPanelDescriptor descriptor1 = new RecordSourceDescriptor(this, DBConnectionDescriptor.IDENTIFIER,TableModeDescriptor.IDENTIFIER);\n");
			output.write("\t\t\twizard.registerWizardPanel(RecordSourceDescriptor.IDENTIFIER,descriptor1);\n");

			output.write("\n");

			output.write("\t\t\tWizardPanelDescriptor descriptor2 = new TableModeDescriptor(this,RecordSourceDescriptor.IDENTIFIER,WizardPanelDescriptor.FINISH);\n");
			output.write("\t\t\twizard.registerWizardPanel(TableModeDescriptor.IDENTIFIER,descriptor2);\n");
			output.write("\t\t\twizard.setCurrentPanel(RecordSourceDescriptor.IDENTIFIER, Wizard.NEXT_BUTTON_ACTION_COMMAND);\n");
			output.write("\t\t\twizard.showModalDialog();\n");

			output.write("\t\t} catch (Exception e) {\n");
			output.write("\t\t\te.printStackTrace();\n");
			output.write("\t\t}\n");
			output.write("\t}\n");

			output.write("\n");

			output.write("\tpublic SchemaTable getExpectedShemaTable() {\n");
			output.write("\t\tList<Record> dr = null;\n");
			output.write("\t\ttry {\n");
			output.write("\t\t\tdr = BuildDestinationRecordList();\n");
			output.write("\t\t} catch (Exception e) {\n");
			output.write("\t\t\te.printStackTrace();\n");
			output.write("\t\t\treturn null;\n");
			output.write("\t\t}\n");
			output.write("\t\tSchemaTable rv = new SchemaTable();\n");
			output.write("\t\trv.updateRecords(dr);\n");
			output.write("\t\treturn rv;\n");
			output.write("\t}\n");

			output.write("\n");

			output.write("\t// build record list to write to real database\n");
			output.write("\tprivate List<Record> BuildDestinationRecordList() throws Exception {\n");
			output.write("\t\t"+Utilities.capitalizeFirst(serviceName)+"Table table = "+Utilities.capitalizeFirst(serviceName)+"TableList.getTableByName(getTableName());\n");
			output.write("\t\tList<Record> rv = new ArrayList<Record>();\n");
			output.write("\t\tfor (Record rec : getFieldList(null)) {\n");
			output.write("\t\t\tif (table.getArguments().containsKey(rec.getFieldName())) {\n");
			output.write("\t\t\t\trv.add(rec);\n");
			output.write("\t\t\t}\n");
			output.write("\t\t}\n");
			output.write("\t\treturn rv;\n");
			output.write("\t}\n");

			output.write("\n");

			output.write("\tpublic void createSchemaTable(AbstractApatarActions actions)\n");
			output.write("\t\tthrows Exception {\n");

			output.write("\n");

			output.write("\t\tif (connectionDataId == -1) {\n");
			output.write("\t\t\treturn;\n");
			output.write("\t\t}\n");

			output.write("\n");

			output.write("\t\tSchemaTable st = getTiForConnection(OUT_CONN_POINT_NAME).getSchemaTable();\n");
			output.write("\t\tst.updateRecords(getFieldList(null));\n");
			output.write("\t}\n");

			output.write("\n");

			output.write("\tpublic void deleteAllRecordsInRDB() throws Exception {\n");
			output.write("\n");
			output.write("\t}\n");

			output.write("\n");

			output.write("\tpublic DataBaseInfo getDataBaseInfo() {\n");
			output.write("\t\treturn dbi;\n");
			output.write("\t}\n");

			output.write("\n");

			output.write("\tpublic List<Record> getFieldList(AbstractApatarActions action)\n");
			output.write("\t\tthrows Exception {\n");
			output.write("\n");
			output.write("\t\tList<Record> rl = new ArrayList<Record>();\n");
			output.write("\t\t"+Utilities.capitalizeFirst(serviceName)+"Table table = "+Utilities.capitalizeFirst(serviceName)+"TableList.getTableByName(getTableName());\n");
			output.write("\t\tHashMap<String,Object> merged = new HashMap<String, Object>();\n");
			output.write("\n");
			output.write("\t\tmerged.putAll(table.getArguments());\n");
			output.write("\t\tmerged.putAll(table.getReturns());\n");
			output.write("\n");
			output.write("\t\tfor(String srt : merged.keySet()){\n");
			output.write("\t\t\tDBTypeRecord rec = DBTypeRecord.getRecordByOriginalType(dbi.getAvailableTypes(), (String)merged.get(srt));\n");
			output.write("\t\t\trl.add(new Record(rec,srt,65000,true,true,false));\n");
			output.write("\t\t}\n");
			output.write("\n");
			output.write("\t\treturn rl;\n");
			output.write("\t}\n");

			output.write("\n");

			output.write("\tpublic List<RDBTable> getTableList() throws Exception {\n");
			output.write("\t\tList<RDBTable> list = new ArrayList<RDBTable>();\n");
			output.write("\t\tfor ("+Utilities.capitalizeFirst(serviceName)+"Table table : "+Utilities.capitalizeFirst(serviceName)+"TableList.get"+Utilities.capitalizeFirst(serviceName)+"Tables().values()) {\n");
			output.write("\t\t\tlist.add(new RDBTable(table.getTableName(), table.getMode()));\n");
			output.write("\t\t}\n");
			output.write("\t\treturn list;\n");
			output.write("\t}\n");

			output.write("\n");
			output.write("}\n");	

			output.close();

		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private static void createTableList(
			String targetNameSpace,
			List<Schema> schemas,
			Service service,
			LinkedList<String> baseTypesNSs,
			Map nameSpaces,Definition def,
			String pluginFolderName, //the folder where the plugin will be created
			String packageName,
			List<BindingOperation> opers

	) throws IOException{

		QName serviceQName = service.getQName();

		File f = new File(pluginFolderName+"/src/"+packageName.replace(".", "/"));
		f.mkdirs();
		f = new File(pluginFolderName+"/src/"+packageName.replace(".", "/")+"/"+Utilities.capitalizeFirst(serviceQName.getLocalPart())+"TableList.java");
		File functions = new File(pluginFolderName+"/src/"+packageName.replace(".", "/")+"/"+Utilities.capitalizeFirst(serviceQName.getLocalPart())+"Functions.java");

		try{
			Writer output = new BufferedWriter(new FileWriter(f));

			output.write("/**\n");
			output.write(" *\n");
			output.write(" * this is auto generated code\n");
			output.write(" *\n");
			output.write(" */\n\n");

			output.write("package "+packageName+";\n");
			output.write("\n");

			output.write("import java.util.HashMap;\n");
			output.write("import java.util.Map;\n");

			output.write("\n");

			output.write("public class "+Utilities.capitalizeFirst(service.getQName().getLocalPart())+"TableList {\n");
			output.write("\n");
			output.write("\tprivate static Map<String, "+Utilities.capitalizeFirst(service.getQName().getLocalPart())+"Table> "+Utilities.capitalizeFirst(service.getQName().getLocalPart())+"Tables =new HashMap<String, "+Utilities.capitalizeFirst(service.getQName().getLocalPart())+"Table>();\n");
			output.write("\n");
			output.write("\tprivate static void putAttributeList(HashMap<String, Object> map, String[] attList, String[] types){\n");
			output.write("\t\tmap.clear();\n");
			output.write("\t\tfor(int i=0;i<attList.length;i++){\n");
			output.write("\t\t\tmap.put(attList[i], types[i]);\n");
			output.write("\t\t}\n");
			output.write("\t}\n");

			output.write("\n");

			output.write("\tstatic{\n");
			output.write("\t\tHashMap<String, Object> arguments = new HashMap<String, Object>();\n");
			output.write("\t\tHashMap<String, Object> returns = new HashMap<String, Object>();\n");

			Writer functions_output = new BufferedWriter(new FileWriter(functions));
			functions_output.write("package "+packageName+";\n\n");
			functions_output.write("import "+packageName+"."+Utilities.capitalizeFirst(service.getQName().getLocalPart())+"Stub.*;\n");
			functions_output.write("import java.rmi.RemoteException;\n");
			functions_output.write("import java.util.*;\n");
			functions_output.write("import org.apache.axis2.AxisFault;\n");
			functions_output.write("import org.apache.axis2.databinding.types.*;\n\n");
			functions_output.write("import org.apache.axiom.om.OMElement;\n\n");

			functions_output.write("import com.apatar.core.ApplicationData;\n");
			functions_output.write("import com.apatar.core.DBTypeRecord;\n");
			functions_output.write("import com.apatar.core.DataBaseInfo;\n");
			functions_output.write("import com.apatar.core.DataBaseTools;\n");
			functions_output.write("import com.apatar.core.DataProcessingInfo;\n");
			functions_output.write("import com.apatar.core.JdbcObject;\n");
			functions_output.write("import com.apatar.core.KeyInsensitiveMap;\n");
			functions_output.write("import com.apatar.core.Record;\n");
			functions_output.write("import com.apatar.core.TableInfo;\n\n");

			functions_output.write("public class "+Utilities.capitalizeFirst(serviceQName.getLocalPart())+"Functions{\n\n");

			for(int o=0;o<opers.size();o++){

				maxArrayCtr=-1;

				BindingOperation bindingOper = opers.get(o);

				String operName = bindingOper.getOperation().getName();

				Utilities.inputs_typesMapingMap.clear();
				Utilities.returns_typesMapingMap.clear();
				try{
					createFunctions(def.getTargetNamespace(),schemas,service,bindingOper,baseTypesNSs,nameSpaces,def,pluginFolderName, functions_output);
				}catch(IOException ioe){
					ioe.printStackTrace();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				allTypesSet.addAll(Utilities.inputs_typesMapingMap.values());
				allTypesSet.addAll(Utilities.returns_typesMapingMap.values());


				output.write("\n");
				output.write("\t\t/* *************************************\n");
				output.write("\t\t * "+ operName +"table\n");
				output.write("\t\t * *************************************/\n");
				output.write("\n");

				output.write("\t\targuments.clear();\n");
				output.write("\t\treturns.clear();\n");

				output.write("\n");

				String[] names = new String[Utilities.inputs_typesMapingMap.size()];
				String[] types = new String[Utilities.inputs_typesMapingMap.size()];
				int i=0;

				for(String key : Utilities.inputs_typesMapingMap.keySet()){
					names[i] = key;
					//TODO:not sure about this one
					if(Utilities.javaToApatarSQL_MapingMap.get(Utilities.inputs_typesMapingMap.get(key))!=null)
						if(Utilities.javaToApatarSQL_MapingMap.get(Utilities.inputs_typesMapingMap.get(key)).split("\\.").length>1)
							types[i] = Utilities.javaToApatarSQL_MapingMap.get(Utilities.inputs_typesMapingMap.get(key)).split("\\.")[1].toUpperCase();
						else
							types[i] = Utilities.javaToApatarSQL_MapingMap.get(Utilities.inputs_typesMapingMap.get(key)).toUpperCase();
					i++;
				}
				output.write("\t\tputAttributeList(arguments, new String[]{");

				for(i=0;i<names.length;i++){
					output.write("\n\t\t\t\""+names[i]+"\"");
					if(i!=names.length-1)
						output.write(",");
					else
						output.write("\t\t");
				}
				output.write("\n\t\t}, new String[]{");

				for(i=0;i<types.length;i++){
					output.write("\n\t\t\t\""+types[i]+"\"");
					if(i!=types.length-1)
						output.write(",");
					else
						output.write("\t\t");
				}
				output.write("\n\t\t});\n");

				output.write("\n");

				/* *****************************
				 * Now the same for the outputs
				 * *****************************/
				names = new String[Utilities.returns_typesMapingMap.size()];
				types = new String[Utilities.returns_typesMapingMap.size()];
				i=0;

				for(String key : Utilities.returns_typesMapingMap.keySet()){
					names[i] = key;
					//TODO:not sure about this one
					if(Utilities.javaToApatarSQL_MapingMap.get(Utilities.returns_typesMapingMap.get(key))!=null)
						if(Utilities.javaToApatarSQL_MapingMap.get(Utilities.returns_typesMapingMap.get(key)).split("\\.").length>1)
							types[i] = Utilities.javaToApatarSQL_MapingMap.get(Utilities.returns_typesMapingMap.get(key)).split("\\.")[1].toUpperCase();
						else
							types[i] = Utilities.javaToApatarSQL_MapingMap.get(Utilities.returns_typesMapingMap.get(key)).toUpperCase();
					i++;
				}
				output.write("\t\tputAttributeList(returns, new String[]{");

				for(i=0;i<names.length;i++){
					output.write("\n\t\t\t\""+names[i]+"\"");
					if(i!=names.length-1)
						output.write(",");
					else
						output.write("\t\t");
				}
				output.write("\n\t\t}, new String[]{");

				for(i=0;i<types.length;i++){
					output.write("\n\t\t\t\""+types[i]+"\"");
					if(i!=types.length-1)
						output.write(",");
					else
						output.write("\t\t");
				}
				output.write("\n\t\t});\n");
				output.write("\n");
				output.write("\t\t"+Utilities.capitalizeFirst(service.getQName().getLocalPart())+"Tables.put(\""+operName+"\", new "+Utilities.capitalizeFirst(service.getQName().getLocalPart())+"Table(\""+operName+"\",arguments,returns));\n");
				output.write("\n");

			}

			functions_output.write("}\n");

			output.write("\n");
			output.write("\t}\n");

			output.write("\n");

			output.write("\tpublic static Map<String, "+Utilities.capitalizeFirst(service.getQName().getLocalPart())+"Table> get"+Utilities.capitalizeFirst(service.getQName().getLocalPart())+"Tables() {\n");
			output.write("\t\treturn "+Utilities.capitalizeFirst(service.getQName().getLocalPart())+"Tables;\n");
			output.write("\t}\n");

			output.write("\n");

			output.write("\tpublic static "+Utilities.capitalizeFirst(service.getQName().getLocalPart())+"Table getTableByName( String name ){\n");
			output.write("\t\treturn "+Utilities.capitalizeFirst(service.getQName().getLocalPart())+"Tables.get(name);\n");
			output.write("\t}\n");
			output.write("\n");

			output.write("}\n");

			output.write("\n");
			output.close();

			functions_output.close();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void generateStubs(String wsdl,String folderName,String packageName){

		try {
			WSDL2Java.main(new String[]{"-uri", wsdl,"-d","adb","-o",folderName,"-p",packageName});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
