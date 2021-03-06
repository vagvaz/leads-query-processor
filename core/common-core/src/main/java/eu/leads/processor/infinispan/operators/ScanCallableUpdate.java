package eu.leads.processor.infinispan.operators;

import eu.leads.processor.common.StringConstants;
import eu.leads.processor.common.infinispan.AcceptAllFilter;
import eu.leads.processor.common.utils.ProfileEvent;
import eu.leads.processor.conf.LQPConfiguration;
import eu.leads.processor.core.Tuple;
import eu.leads.processor.infinispan.LeadsCollector;
import eu.leads.processor.math.FilterOperatorTree;
import eu.leads.processor.plugins.pagerank.node.DSPMNode;
import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIterable;
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
	transient protected double totalSum;
	transient protected Cache approxSumCache;
 	transient boolean versioning;
	boolean onVersionedCache;
	transient protected long versionStart = -1, versionFinish = -1, range = -1;
	protected LeadsCollector collector;

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


	@Override
	public void initialize() {
		super.initialize();
		luceneKeys = null;

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
			if (checkIndex_usage())
				System.out.println("Will try to use Indexes");
			else
				System.err.println("No indexes");

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

	private boolean checkIndex_usage() {
		// System.out.println("Check if fields are indexed");
		if (conf.getBoolean("useIndex")) {
			System.out.println("Check Callable Use indexes!!");
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

//		ProfileEvent scanExecute = new ProfileEvent("ScanExecute", profilerLog);

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
//					scanExecute.end();
					return;
				}
				Object objectValue = versionedCache.get(ikey);
				toRunValue = (Tuple) objectValue;

//        toRunValue = (String) objectValue;
      }
    } else {
      toRunValue = (Tuple) ivalue;
      if(attributeFunctions.size() > 0){
        executeFunctions(toRunValue);
      }
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
//    scanExecute.end();
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
   *
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

	@Override public void finalizeCallable() {
		fullProcessing.end();
		collector.finalizeCollector();
		super.finalizeCallable();
	}

}
