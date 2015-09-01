package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.AcceptAllFilter;
import eu.leads.processor.common.infinispan.EnsembleCacheUtils;
import eu.leads.processor.common.utils.ProfileEvent;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.core.index.*;
import eu.leads.processor.math.FilterOperatorNode;
import eu.leads.processor.infinispan.LeadsCollector;
import eu.leads.processor.math.FilterOperatorTree;
import eu.leads.processor.math.MathUtils;
import eu.leads.processor.plugins.pagerank.node.DSPMNode;
import org.infinispan.query.dsl.Query;
import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIterable;
import org.infinispan.query.SearchManager;
import org.infinispan.query.dsl.FilterConditionContext;
import org.infinispan.query.dsl.QueryBuilder;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.versioning.VersionedCache;
import org.infinispan.versioning.utils.version.Version;
import org.infinispan.versioning.utils.version.VersionScalar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.io.Serializable;

import java.util.*;


/**
 * Created by vagvaz on 2/20/15.
 */
public class ScanCallableUpdate<K, V> extends LeadsSQLCallable<K, V> implements Serializable {

	transient protected VersionedCache versionedCache;

	transient protected Cache pageRankCache;
	transient protected FilterOperatorTree tree;
	transient protected double totalSum;
	transient protected Cache approxSumCache;
	protected String qualString;
	transient boolean versioning;
	boolean onVersionedCache;
	transient protected long versionStart = -1, versionFinish = -1, range = -1;
	protected LeadsCollector collector;


	//  transient protected InfinispanManager manager;
	protected Logger log = LoggerFactory.getLogger(ScanCallableUpdate.class.toString());
	private VersionScalar minVersion = null;
	private VersionScalar maxVersion = null;
	transient protected boolean renameTableInTree;
	transient private String toRename;
	transient private String tableName;
	transient Logger profilerLog;
	private ProfileEvent fullProcessing;


	public ScanCallableUpdate(String configString, String output, LeadsCollector collector) {
		super(configString, collector.getCacheName());
		this.collector = collector;
	}
//  public ScanCallableUpdate(String configString, String output,boolean onVersionedCache) {
//    super(configString, output);
//    this.onVersionedCache = onVersionedCache;
//  }

	@Override
	public void initialize() {
		super.initialize();
		lqueries = null;
//    versionedCache = new VersionedCacheTreeMapImpl(inputCache,new VersionScalarGenerator(),inputCache.getName());
		profilerLog = LoggerFactory.getLogger("###PROF###" + this.getClass().toString());

		pageRankCache = (Cache) imanager.getPersisentCache("pagerankCache");
		log.info("--------------------    get approxSum cache ------------------------");
		approxSumCache = (Cache) imanager.getPersisentCache("approx_sum_cache");
		totalSum = -1f;

		if (conf.getObject("body").containsField("versionStart")) {
			versionStart = conf.getObject("body").getLong("versionStart");
			if (versionStart > 0) {
				versioning = true;
				minVersion = new VersionScalar(versionStart);
			}
		}
		if (conf.getObject("body").containsField("versionFinish")) {
			versionFinish = conf.getObject("body").getLong("versionFinish");
			if (versionFinish > 0) {
				versioning = true;
				maxVersion = new VersionScalar(versionFinish);
			}
		}


		if (conf.getObject("body").containsField("qual")) {
			tree = new FilterOperatorTree(conf.getObject("body").getObject("qual"));
			//System.out.print("Quaaal : " + conf.getObject("body").getObject("qual").toString());
			toRename = getRenamingTableFromSchema(inputSchema);
			tableName = conf.getObject("body").getObject("tableDesc").getString("tableName");
			tableName = tableName.replace(StringConstants.DEFAULT_DATABASE_NAME + ".", "");
			if (tableName.equals(toRename)) {
				renameTableInTree = false;
			} else {
				renameTableInTree = true;
			}
			if (checkIndex_usage()) {
				// create query
				lqueries = createLuceneQuerys(indexCaches, tree.getRoot());
			}
		} else {
			tree = null;
		}

		versioning = getVersionPredicate(conf);
		fullProcessing = new ProfileEvent("FullScanProcessing", profilerLog);

		collector.setOnMap(getHasNext());
		collector.setManager(this.embeddedCacheManager);
		collector.setEmanager(emanager);
		collector.setSite(LQPConfiguration.getInstance().getMicroClusterName());
		collector.initializeCache(inputCache.getName(), imanager);
		if (getHasNext()) {
			collector.setEnsembleHost(ensembleHost);
			collector.setInputCache(inputCache);
			collector.initializeNextCallable(conf);
		}
	}

	private boolean getHasNext() {
		boolean result = conf.containsField("next");
		return result;
	}


	Object getSubLucene(HashMap<String, Cache> indexCaches, FilterOperatorNode root) {
		FilterConditionContext result = null;
		FilterConditionContext left = null;
		FilterConditionContext right = null;
		QueryBuilder qleft = null;
		QueryBuilder qrigth = null;

		if (root == null)
			return null;
		Object oleft = getSubLucene(indexCaches, root.getLeft());
		Object oright = getSubLucene(indexCaches, root.getRight());

		if (oleft instanceof FilterConditionContext)
			left = (FilterConditionContext) oleft;
		if (oright instanceof FilterConditionContext)
			right = (FilterConditionContext) oright;

		if (oleft instanceof QueryBuilder)
			qleft = (QueryBuilder) oleft;

		switch (root.getType()) {
			case EQUAL:
				if (qleft != null && oright != null)
					return qleft.having("attributeValue").eq(oright);//,right.getValueAsJson());
				if (left != null && oright != null)
					return left.and().having("attributeValue").eq(oright);//,right.getValueAsJson());
				break;
			case IS_NULL:
				// result = left.isValueNull();
				break;
			case NOT_EQUAL:
				//  result = !(MathUtils.equals(left.getValueAsJson(), right.getValueAsJson()));
				break;
			case FIELD:
				String collumnName = root.getValueAsJson().getObject("body").getObject("column").getString("name");
				String type = root.getValueAsJson().getObject("body").getObject("column").getObject("dataType").getString("type");
				//MathUtils.getTextFrom(root.getValueAsJson());

				if (indexCaches.containsKey(collumnName)) {
					System.out.println("Found Cache for: " + collumnName);
					SearchManager sm = org.infinispan.query.Search.getSearchManager(indexCaches.get(collumnName));
					QueryFactory qf = sm.getQueryFactory();
					QueryBuilder Qb;
					if (type.equals("TEXT"))
						Qb = qf.from(LeadsIndexString.class);
					else if (type.startsWith("FLOAT"))
						Qb = qf.from(LeadsIndexFloat.class);
					else if (type.startsWith("DOUBLE"))
						Qb = qf.from(LeadsIndexDouble.class);
					else if (type.startsWith("INT"))
						Qb = qf.from(LeadsIndexInteger.class);
					else if (type.startsWith("LONG"))
						Qb = qf.from(LeadsIndexLong.class);
					else
						Qb = qf.from(LeadsIndex.class);

					return Qb;
				}
				break;

			case CONST:
				JsonObject datum = root.getValueAsJson().getObject("body").getObject("datum");
				type = datum.getObject("body").getString("type");
				String ret ="";
				System.out.println("Fount Const: " + datum.getObject("body").toString());


				try {
					if (type.equals("TEXT"))
						return  MathUtils.getTextFrom(root.getValueAsJson());
					else {
						//ret=datum.getObject("body").getNumber("val");
						if (type.startsWith("FLOAT"))
							return (float)datum.getObject("body").getNumber("val");

						else if (type.startsWith("DOUBLE"))
							return (double)datum.getObject("body").getNumber("val");

						else if (type.startsWith("INT"))
							return  datum.getObject("body").getInteger("val");

						else if (type.startsWith("LONG"))
							return  datum.getObject("body").getLong("val");
					}
				} catch (Exception e) {
					System.err.print("Unable to parse " + ret + " to type " + type);
				}
				return null;

			case LTH:
				if (qleft != null && oright != null)

					return qleft.having("attributeValue").lt(oright);
				if (left != null && oright != null)
					return left.and().having("attributeValue").lt(oright);//,right.getValueAsJson());

				break;
			case LEQ:
				if (qleft != null && oright != null)
					return qleft.having("attributeValue").lte(oright);
				if (left != null && oright != null)
					return left.and().having("attributeValue").lte(oright);//,right.getValueAsJson());
				break;
			case GTH:
				if (qleft != null && oright != null)
					return qleft.having("attributeValue").gt(oright);
				if (left != null && oright != null)
					return left.and().having("attributeValue").gt(oright);//,right.getValueAsJson());
				break;
			case GEQ:
				if (qleft != null && oright != null)
					return qleft.having("attributeValue").gte(oright);
				if (left != null && oright != null)
					return left.and().having("attributeValue").gte(oright);//,right.getValueAsJson());
				break;

			case LIKE:
				if (qleft != null && oright != null)
					return qleft.having("attributeValue").like((String) oright);
				if (left != null && oright != null) {
					return left.and().having("attributeValue").like((String) oright);//,right.getValueAsJson());
				}
				break;


			case ROW_CONSTANT:
				//TODO
				break;
		}
		return null;
	}


	ArrayList<FilterConditionContext> createLuceneQuerys(HashMap<String, Cache> indexCaches, FilterOperatorNode root) {
		ArrayList<FilterConditionContext> result = new ArrayList<>();
		ArrayList<FilterConditionContext> left = null;
		ArrayList<FilterConditionContext> right = null;
		switch (root.getType()) {
			case AND: {
				left = createLuceneQuerys(indexCaches, root.getLeft());
				right = createLuceneQuerys(indexCaches, root.getRight());
				System.out.println("Fix AND with multiple indexes");
			}
			break;
			case OR: {
				left = createLuceneQuerys(indexCaches, root.getLeft());
				right = createLuceneQuerys(indexCaches, root.getRight());
				//if(left !=null && right !=null){
				//use sketches to check
				System.out.println("Fix OR with multiple indexes");
			}
			break;
			default: {
				System.out.println("SubQual " + root.getType());
				FilterConditionContext qual = (FilterConditionContext) getSubLucene(indexCaches, root);
				if (qual != null)
					result.add(qual);
			}
			if (left != null)
				result.addAll(left);
			if (right != null)
				result.addAll(right);
		}
		return (result.isEmpty()) ? null : result;
	}


	private boolean checkIndex_usage() {
		// System.out.println("Check if fields are indexed");
		if (conf.getBoolean("useIndex")) {
			System.out.println("Scan Callable Use indexes!!");
			indexCaches = new HashMap<>();
			String columnName;
			JsonArray fields = inputSchema.getArray("fields");
			Iterator<Object> iterator = fields.iterator();
			while (iterator.hasNext()) {
				JsonObject tmp = (JsonObject) iterator.next();
				columnName = tmp.getString("name");
				//System.out.print("Check if exists: " + "." + columnName + " ");
				if (imanager.getCacheManager().cacheExists(columnName)) {
					indexCaches.put(columnName, (Cache) imanager.getIndexedPersistentCache(columnName));
					System.out.println(columnName + " indexed!");
				}

			}
			return indexCaches.size() > 0;
		}
		return false;
	}

	/**
	 * This method shoul read the Versions if any , from the configuration of the Scan operator and return true
	 * if there are specific versions required, false otherwise
	 *
	 * @param conf the configuration of the operator
	 * @return returns true if there is a query on specific versions false otherwise
	 */
	private boolean getVersionPredicate(JsonObject conf) {
		return false;
	}

	@Override
	public void executeOn(K key, V ivalue) {

		ProfileEvent scanExecute = new ProfileEvent("ScanExecute", profilerLog);

		//         System.err.println(manager.getCacheManager().getAddress().toString() + " "+ entry.getKey() + "       " + entry.getValue());
		Tuple toRunValue = null;
		if (onVersionedCache) {
			String versionedKey = (String) key;
			String ikey = pruneVersion(versionedKey);
			Version currentVersion = getVersion(versionedKey);
			if (versioning) {
				if (isInVersionRange(currentVersion)) {
					toRunValue = (Tuple) ivalue;
				}
			} else {
				Version latestVersion = versionedCache.getLatestVersion(ikey);
				if (latestVersion == null) {
					scanExecute.end();
					return;
				}
				Object objectValue = versionedCache.get(ikey);
				toRunValue = (Tuple) objectValue;
//        toRunValue = (String) objectValue;
			}
		} else {
			toRunValue = (Tuple) ivalue;
		}
		Tuple tuple = toRunValue;//new Tuple(toRunValue);
		if (tree != null) {
			if (renameTableInTree) {
				tree.renameTableDatum(tableName, toRename);
			}

//        profExecute.start("tree.accept");
			boolean accept = tree.accept(tuple);
//        profExecute.end();
			if (accept) {
//          profExecute.start("prepareOutput");

				tuple = prepareOutput(tuple);
//          profExecute.end();
				//               log.info("--------------------    put into output with filter ------------------------");
				if (key != null && tuple != null) {
//            profExecute.start("Scan_Put");
					collector.emit(key.toString(), tuple);
//            EnsembleCacheUtils.putToCache(outputCache,key.toString(), tuple);
//            profExecute.end();
				}
			}
		} else {
//        profExecute.start("prepareOutput");
			tuple = prepareOutput(tuple);
//        profExecute.end();
			//            log.info("--------------------    put into output without tree ------------------------");
			if (key != null && tuple != null) {
//          profExecute.start("Scan_outputToCache");
//          EnsembleCacheUtils.putToCache(outputCache,key, tuple);
				collector.emit(key.toString(), tuple);
//          profExecute.end();
			}

		}
		scanExecute.end();
	}

	private boolean needsREnaming() {
		return !outputSchema.toString().equals(inputSchema.toString());
	}

	private String getRenamingTableFromSchema(JsonObject inputSchema) {
		if (inputSchema != null) {

			String fieldname = ((JsonObject) (inputSchema.getArray("fields").iterator().next())).getString("name");
			//fieldname database.table.collumncolumnName = tmp.getString("name");
			String result = fieldname.substring(fieldname.indexOf(".") + 1, fieldname.lastIndexOf("."));
			if (result != null && !result.equals(""))
				return result;
		}
		return null;
	}

	//TODO write checks
	private String getTableNameFromTuple(Tuple tuple) {
		if (tuple != null) {

			String fieldname = tuple.getFieldNames().iterator().next();
			//fieldname database.table.collumn
			String result = fieldname.substring(fieldname.indexOf(".") + 1, fieldname.lastIndexOf("."));
			if (result != null && !result.equals(""))
				return result;
		}

		return null;
	}


	/**
	 * @param currentVersion the version of the tuple currently processed by the operator
	 * @return true if it satisfies the version range defined in the operator false otherwise
	 */
	private boolean isInVersionRange(Version currentVersion) {
		//SAMPLE CODE NOT NECESSARILY exactly like that
		if (minVersion != null)
			if (currentVersion.compareTo(minVersion) < 0) {
				return false;
			}
		if (maxVersion != null)
			if (currentVersion.compareTo(maxVersion) > 0)
				return false;
		return true;
	}

	private Version getVersion(String versionedKey) {
		Version result = null;
		String stringVersion = versionedKey.substring(versionedKey.lastIndexOf(":") + 1);
		result = new VersionScalar(Long.parseLong(stringVersion));
		return result;
	}

	private String pruneVersion(String versionedKey) {
		String result = versionedKey.substring(0, versionedKey.lastIndexOf(":"));
		return result;
	}


	private void namesToLowerCase(Tuple tuple) {
		Set<String> fieldNames = new HashSet<>(tuple.getFieldNames());
		for (String field : fieldNames) {
			tuple.renameAttribute(field, field.toLowerCase());
		}
	}

//  private void renameAllTupleAttributes(Tuple tuple) {
//    JsonArray fields = inputSchema.getArray("fields");
//    Iterator<Object> iterator = fields.iterator();
//    String columnName = null;
//    while(iterator.hasNext()){
//      JsonObject tmp = (JsonObject) iterator.next();
//      columnName = tmp.getString("name");
//      int lastPeriod = columnName.lastIndexOf(".");
//      String attributeName = columnName.substring(lastPeriod+1);
//      tuple.renameAttribute(attributeName,columnName);
//    }

//    handlePagerank(columnName.substring(0,columnName.lastIndexOf(".")),tuple);
//  }

	protected void handlePagerank(String substring, Tuple t) {
		if (conf.getObject("body").getObject("tableDesc").getString("tableName").equals("default.webpages")) {
			if (totalSum < 0) {
				computeTotalSum();
			}
			String url = t.getAttribute("default.webpages.url");
			DSPMNode currentPagerank = (DSPMNode) pageRankCache.get(url);
			if (currentPagerank == null || totalSum <= 0) {
//        t.setAttribute("default.webpages.pagerank",0f);
				t.setAttribute("default.webpages.pagerank", Double.toString((10000 / url.length()) / 10000));

				return;
			}
			//            t.setNumberAttribute("default.webpages.pagerank",0.032342);
			t.setNumberAttribute("default.webpages.pagerank", currentPagerank.getVisitCount() / totalSum);

			//READ PAGERANK FROM PAGERANK CACHE;
			//READ TOTAL ONCE
			//compute value update it to tuple


			//      if (t.hasField("default.webpages.pagerank")) {
			//         if (!t.hasField("url"))
			//            return;
			//         String pagerankStr = t.getAttribute("pagerank");
			//            Double d = Double.parseDouble(pagerankStr);
			//            if (d < 0.0) {
			//
			//                try {
			////                    d = LeadsPrGraph.getPageDistr(t.getAttribute("url"));
			//                    d = (double) LeadsPrGraph.getPageVisitCount(t.getAttribute("url"));
			//
			//                } catch (IOException e) {
			//                    e.printStackTrace();
			//                }
			//                t.setAttribute("pagerank", d.toString());
			//        }
		}
	}

	private void computeTotalSum() {
		log.info(
				"--------------------   Creating iterable over approx sum entries ------------------------");
		CloseableIterable<Map.Entry<String, Integer>> iterable =
				approxSumCache.getAdvancedCache().filterEntries(new AcceptAllFilter());
		log.info("--------------------    Iterating over approx sum entries cache ------------------------");
		for (Map.Entry<String, Integer> outerEntry : iterable) {
			totalSum += outerEntry.getValue();
		}
		iterable.close();

		if (totalSum > 0) {
			totalSum += 1;
		}
	}

	@Override
	public void finalizeCallable() {
		fullProcessing.end();
		collector.finalizeCollector();
		super.finalizeCallable();
	}
}
