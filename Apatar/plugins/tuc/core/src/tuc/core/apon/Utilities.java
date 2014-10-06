package tuc.core.apon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.apatar.ui.ApatarUiMain;

/**
 * Auxiliary class.
 * 
 * @author apon
 */
public class Utilities {

	public static ArrayList<String> reservedWords = new ArrayList<String>();
	
	public static HashMap<String, String> javaToApatarSQL_MapingMap = new HashMap<String, String>();
	public static HashMap<String, String> inputs_typesMapingMap = new HashMap<String, String>();
	public static HashMap<String, String> returns_typesMapingMap = new HashMap<String, String>();
	
	public static HashMap<String, String> axisToJava_MappingMap = new HashMap<String, String>();
	
	/**
	 * This method is used to capitalize
	 * the first letter of the input argument
	 * 
	 * @param String toCap
	 * @return String ToCap
	 */
	public static String capitalizeFirst(String toCap){
		return toCap.substring(0,1).toUpperCase()+toCap.substring(1);
	}
	
	public static String lowerFirst(String toLow){
		return toLow.substring(0,1).toLowerCase()+toLow.substring(1);
	}
	
	/** This method instantiates type mapping according
	 * to the following 
	 * 
	 * <b>Standard mappings from WSDL to Java</b>
	 * xsd:base64Binary	byte[]
	 * xsd:boolean			boolean
	 * xsd:byte			byte
	 * xsd:dateTime		java.util.Calendar
	 * xsd:decimal			java.math.BigDecimal
	 * xsd:double			double
	 * xsd:float			float
	 * xsd:hexBinary		byte[]
	 * xsd:int				int
	 * xsd:integer			java.math.BigInteger
	 * xsd:long			long
	 * xsd:QName			javax.xml.namespace.QName
	 * xsd:short			short
	 * xsd:string			java.lang.String
	 */
	public static void initAxisToJava_mapping(){
		axisToJava_MappingMap.put("base64Binary", "java.lang.Byte[]");
		axisToJava_MappingMap.put("boolean", "java.lang.Boolean");
		axisToJava_MappingMap.put("byte", "java.lang.Byte");
		axisToJava_MappingMap.put("dateTime", "java.util.Calendar");
		axisToJava_MappingMap.put("decimal", "java.math.BigDecimal");
		axisToJava_MappingMap.put("double", "java.lang.Double");
		axisToJava_MappingMap.put("float", "java.lang.Float");
		axisToJava_MappingMap.put("hexBinary", "java.lang.Byte[]");
		axisToJava_MappingMap.put("int", "java.lang.Integer");
		axisToJava_MappingMap.put("integer", "java.math.BigInteger");
		axisToJava_MappingMap.put("long", "java.lang.Long");
		axisToJava_MappingMap.put("QName", "javax.xml.namespace.QName");
		axisToJava_MappingMap.put("short", "java.lang.Short");
		axisToJava_MappingMap.put("string", "java.lang.String");
		axisToJava_MappingMap.put("date", "java.util.Date");
		axisToJava_MappingMap.put("time", "org.apache.axis2.databinding.types.Time");
	}
	/**
	 * This method returns the correct mapping
	 * according to the Standard Axis2 mapping from WSDL to Java
	 * 
	 * @param xsdType
	 * @return javaType
	 */
	public static String getAxisToJavaMapping(String xsdType){
		return axisToJava_MappingMap.get(xsdType);
	}
	
	public static void initJavaToApatarSQL_mapping(){
		javaToApatarSQL_MapingMap.put("java.lang.Boolean", "ERecordType.Boolean");
		javaToApatarSQL_MapingMap.put("java.util.Calendar", "dateTime");
		javaToApatarSQL_MapingMap.put("java.lang.Decimal","ERecordType.Numeric");
		javaToApatarSQL_MapingMap.put("java.lang.Double","ERecordType.Numeric");
		javaToApatarSQL_MapingMap.put("java.lang.Float","ERecordType.Decimal");
		javaToApatarSQL_MapingMap.put("java.lang.Integer","ERecordType.Numeric");
		javaToApatarSQL_MapingMap.put("java.math.BigInteger","ERecordType.Numeric");
		javaToApatarSQL_MapingMap.put("java.lang.Long","ERecordType.Numeric");
		javaToApatarSQL_MapingMap.put("java.lang.Short", "ERecordType.Numeric");
		javaToApatarSQL_MapingMap.put("java.lang.String", "ERecordType.Text");
		javaToApatarSQL_MapingMap.put("javax.xml.namespace.QName", "ERecordType.Text"); //??
		javaToApatarSQL_MapingMap.put("java.util.Date","ERecordType.Date");
		javaToApatarSQL_MapingMap.put("org.apache.axis2.databinding.types.Time","ERecordType.Time");
	}
	
	public static String java2apatarSQL(String type){
		return javaToApatarSQL_MapingMap.get(type);
	}
	
	/*
	 * java reserved words
	 */
	public static void initReserved(){

		String[] rev = {
				"abstract","continue","for","new","switch",
				"assert","default",	"goto","package","synchronized",
				"boolean","do","if","private","this",
				"break","double","implements","protected","throw",
				"byte","else","import","public","throws",
				"case","enum","instanceof",	"return","transient",
				"catch","extends","int","short","try",
				"char","final",	"interface","static","void",
				"class","finally","long","strictfp","volatile",
				"const","float","native","super","while"
		};

		for(int i=0;i<rev.length;i++){
			reservedWords.add(rev[i]);
		}
	}
	
	public static boolean isReserved(String key){
		if(reservedWords.contains(key))
			return true;
		else
			return false;
	}
		
	/**
	 * This method compiles the generated client's code
	 * 
	 * @param the file names of the classes to be compiled
	 * @return true for success, false for failure
	 * 
	 * @author apon
	 */
	public static boolean compile(String pluginFolderName,String packageName, String serviceName){
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();	//System's compiler
		
		String curDir = System.getProperty("user.dir");
		String libDir = curDir+"/plugins/tuc/core/lib";
		
		packageName = packageName.replace(".", "/");
		
		/* compiler's arguments */
		String[] arguments = new String[]{
				
				/* Libraries */
				
				"-classpath" ,
				libDir+"/activation-1.1.jar:" +
				libDir+"/XmlSchema-1.4.3.jar:" +
				libDir+"/axiom-api-1.2.8.jar:" +
				libDir+"/axiom-dom-1.2.8.jar:" +
				libDir+"/axiom-impl-1.2.8.jar:" +
				libDir+"/axis2-adb-1.5.1.jar:" +
				libDir+"/axis2-adb-codegen-1.5.1.jar:" +
				libDir+"/axis2-ant-plugin-1.5.1.jar:" +
				libDir+"/axis2-clustering-1.5.1.jar:" +
				libDir+"/axis2-codegen-1.5.1.jar:" +
				libDir+"/axis2-corba-1.5.1.jar:" +
				libDir+"/axis2-fastinfoset-1.5.1.jar:" +
				libDir+"/axis2-java2wsdl-1.5.1.jar:" +
				libDir+"/axis2-jaxbri-1.5.1.jar:" +
				libDir+"/axis2-jaxws-1.5.1.jar:" +
				libDir+"/axis2-jibx-1.5.1.jar:" +
				libDir+"/axis2-json-1.5.1.jar:" +
				libDir+"/axis2-kernel-1.5.1.jar:" +
				libDir+"/axis2-metadata-1.5.1.jar:" +
				libDir+"/axis2-mtompolicy-1.5.1.jar:" +
				libDir+"/axis2-saaj-1.5.1.jar:" +
				libDir+"/axis2-spring-1.5.1.jar:" +
				libDir+"/axis2-transport-http-1.5.1.jar:" +
				libDir+"/axis2-transport-local-1.5.1.jar:" +
				libDir+"/axis2-xmlbeans-1.5.1.jar:" +
				libDir+"/bcel-5.1.jar:" +
				libDir+"/commons-codec-1.3.jar:" +
				libDir+"/commons-fileupload-1.2.jar:" +
				libDir+"/commons-httpclient-3.1.jar:" +
				libDir+"/commons-io-1.4.jar:" +
				libDir+"/commons-lang-2.3.jar:" +
				libDir+"/commons-logging-1.1.1.jar:" +
				libDir+"/geronimo-annotation_1.0_spec-1.1.jar:" +
				libDir+"/geronimo-jaxws_2.1_spec-1.0.jar:" +
				libDir+"/geronimo-saaj_1.3_spec-1.0.1.jar:" +
				libDir+"/geronimo-stax-api_1.0_spec-1.0.1.jar:" +
				libDir+"/geronimo-ws-metadata_2.0_spec-1.1.2.jar:" +
				libDir+"/httpcore-4.0.jar:" +
				libDir+"/jalopy-1.5rc3.jar:" +
				libDir+"/jaxb-api-2.1.jar:" +
				libDir+"/jaxb-impl-2.1.7.jar:" +
				libDir+"/jaxb-xjc-2.1.7.jar:" +
				libDir+"/jaxen-1.1.1.jar:" +
				libDir+"/jdom.jar:"+
				libDir+"/jettison-1.0-RC2.jar:" +
				libDir+"/jibx-bind-1.2.1.jar:" +
				libDir+"/jibx-run-1.2.1.jar:" +
				libDir+"/log4j-1.2.15.jar:" +
				libDir+"/mail-1.4.jar:" +
				libDir+"/mex-1.5.1.jar:" +
				libDir+"/neethi-2.0.4.jar:" +
				libDir+"/smack-3.0.4.jar:" +
				libDir+"/smackx-3.0.4.jar:" +
				libDir+"/soapmonitor-1.5.1.jar:" +
				libDir+"/woden-api-1.0M8.jar:" +
				libDir+"/woden-impl-dom-1.0M8.jar:" +
				libDir+"/wsdl4j-1.6.2.jar:" +
				libDir+"/wstx-asl-3.2.4.jar:" +
				libDir+"/xalan-2.7.0.jar:" +
				libDir+"/xercesImpl-2.6.2.jar:" +
				libDir+"/xml-apis-1.3.02.jar:" +
				libDir+"/xml-resolver-1.2.jar:" +
				libDir+"/xmlbeans-2.3.0.jar:" +
				
				curDir+"/plugins/core/core/bin:" +
				curDir+"/plugins/rich_ui_framework/ui/bin" ,
				
				/* Files to be compiled */
				
				curDir+"/"+pluginFolderName+"/src/"+packageName+"/"+serviceName+"CallbackHandler.java",
				curDir+"/"+pluginFolderName+"/src/"+packageName+"/"+serviceName+"Stub.java",
				curDir+"/"+pluginFolderName+"/src/"+packageName+"/"+serviceName+"Utils.java",
				curDir+"/"+pluginFolderName+"/src/"+packageName+"/"+serviceName+"Node.java",
				curDir+"/"+pluginFolderName+"/src/"+packageName+"/"+serviceName+"NodeFactory.java",
				curDir+"/"+pluginFolderName+"/src/"+packageName+"/"+serviceName+"Table.java",
				curDir+"/"+pluginFolderName+"/src/"+packageName+"/"+serviceName+"TableList.java",
				curDir+"/"+pluginFolderName+"/src/"+packageName+"/"+serviceName+"Functions.java",
				
				/* output directory */
				
				"-d" , 
				curDir+"/"+pluginFolderName+"/bin"
				};
		
		int result = compiler.run(null, null, null, arguments);
		
		if(result==0){
			JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, "Plugin generation completed. Please, restart");
			return true;
		}
		else{
			JOptionPane.showMessageDialog(ApatarUiMain.MAIN_FRAME, "Error happend");
			deleteDirectory(new File(pluginFolderName));
			return false;
		}
	}
	
	/**
	 * Copies icon files from storageFolder (wsIcons) to plug-in folder
	 * 
	 * @param pluginFolder
	 * @param serviceName
	 */
	public static void copyIcons(String pluginFolder, String serviceName){
		
		String packName = "tuc.ws."+Utilities.lowerFirst(serviceName);
		
		File srcIcon_16 = new File("wsIcons/16-webService.png");
		File srcIcon_32 = new File("wsIcons/32-webService.png");
		
		File dstIcon_16 = new File(pluginFolder+"/bin/"+packName.replace(".", "/")+"/16-"+serviceName+"Icon.png");
		File dstIcon_32 = new File(pluginFolder+"/bin/"+packName.replace(".", "/")+"/32-"+serviceName+"Icon.png");
		
		File dstIcon_16_src = new File(pluginFolder+"/src/"+packName.replace(".", "/")+"/16-"+serviceName+"Icon.png");
		File dstIcon_32_src = new File(pluginFolder+"/src/"+packName.replace(".", "/")+"/32-"+serviceName+"Icon.png");
		
		try {
			InputStream srcStream_16 = new FileInputStream(srcIcon_16);
			OutputStream dstStream_16 = new FileOutputStream(dstIcon_16);
			
			InputStream srcStream_32= new FileInputStream(srcIcon_32);
			OutputStream dstStream_32 = new FileOutputStream(dstIcon_32);
			
			OutputStream dstStream_16_src = new FileOutputStream(dstIcon_16_src);
			OutputStream dstStream_32_src = new FileOutputStream(dstIcon_32_src);
			
			byte[] buf = new byte[1024];
			int len;
			
			while((len=srcStream_16.read(buf))>0){
				dstStream_16.write(buf, 0, len);
				dstStream_16_src.write(buf,0,len);
			}
			
			
			while((len=srcStream_32.read(buf))>0){
				dstStream_32.write(buf, 0, len);
				dstStream_32_src.write(buf,0,len);
			}
			srcStream_16.close();
			srcStream_32.close();
			dstStream_16.close();
			dstStream_32.close();
			dstStream_16_src.close();
			dstStream_32_src.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This function deletes the plug-in folder
	 * in case of compilation errors
	 * 
	 * @param path to plug-in folder
	 */
	private static void deleteDirectory(File path){
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
		}
		path.delete();
	}
	
}
