package eu.leads.processor.common.test;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;

import eu.leads.processor.common.LeadsCollector;
import eu.leads.processor.common.LeadsMapper;
import eu.leads.processor.common.LeadsMapperCallable;
import eu.leads.processor.common.LeadsReduceCallable;
import eu.leads.processor.common.LeadsReducer;

import org.infinispan.Cache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.Transport;
import org.infinispan.commons.util.Util;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.locks.LockSupport;

/**
 * Infinispan distributed executors demo using pi approximation.
 */
public class LeadsDistMapReduceTest  {

	private String textFile;
	protected final boolean isMaster;
	protected final String cfgFile;
	protected final JSAPResult commandLineOptions;

	private static String[] loc = { "a", "b,", "c", "d", "asa", "aasd",
			"pp", "kasd", "gadfa", "aerw", "oead", "ddsfa", "ewrwa",
			"cvaa", "dfa" };
	transient protected static Random r;

	protected static long wordsC = 60000;

	protected String getWord() {
		int l = 10;
		String result = "";
		for (int i = 0; i < l; i++) {
			result += loc[r.nextInt(loc.length)];
		}
		return result;
	}

	protected String getLine() {
		int l = 10;
		String result = "";
		for (int i = 0; i < l; i++) {
			result += " " + getWord();
		}
		return result;
	}
 
	protected JSAPResult parseParameters(String[] args) throws Exception {
		SimpleJSAP jsap = buildCommandLineOptions();

		JSAPResult config = jsap.parse(args);
		if (!config.success() || jsap.messagePrinted()) {
			Iterator<?> messageIterator = config.getErrorMessageIterator();
			while (messageIterator.hasNext())
				System.err.println(messageIterator.next());
			System.err.println(jsap.getHelp());
			return null;
		}

		return config;
	}

	protected Cache<String, String> startCache() throws IOException {
		CacheBuilder cb = new CacheBuilder(cfgFile);
		EmbeddedCacheManager cacheManager = cb.getCacheManager();
		Configuration dcc = cacheManager.getDefaultCacheConfiguration();

		cacheManager.defineConfiguration("wordcount",
				new ConfigurationBuilder().read(dcc).clustering().l1()
						.disable().clustering().cacheMode(CacheMode.DIST_SYNC)
						.hash().numOwners(1).build());
		Cache<String, String> cache = cacheManager.getCache();

		Transport transport = cache.getAdvancedCache().getRpcManager()
				.getTransport();
		if (isMaster)
			System.out.printf("Node %s joined as master. View is %s.%n",
					transport.getAddress(), transport.getMembers());
		else
			System.out.printf("Node %s joined as slave. View is %s.%n",
					transport.getAddress(), transport.getMembers());

		return cache;
	}

	public static void main(String... args) throws Exception {
		new LeadsDistMapReduceTest(args).run();
	}

	public LeadsDistMapReduceTest(String[] args) throws Exception {
		commandLineOptions = parseParameters(args);
		String nodeType = commandLineOptions.getString("nodeType");
		isMaster = nodeType != null && nodeType.equals("master");
		cfgFile = "/opt/Projects/infinispan/demos/distexec/src/main/release/etc/config-samples/minimal.xml";// leads_test_configuration.xml";//minimal.xml"
																											// ;//
																											// commandLineOptions.getString("configFile");
		r = new Random(0);
		textFile = commandLineOptions.getString("textFile");
	}

	public void run() throws Exception {

		// Step 1: start cache.
		// Cache<String, String> cache = startCache();
		// String cfgFile
		// ="/opt/Projects/leads-query-processor/common/src/main/resources/conf/infinispan.xml";
		// "/opt/Projects/infinispan/demos/distexec/src/main/release/etc/config-samples/leads_test_configuration.xml";//minimal.xml" ;// commandLineOptions.getString("configFile");
		//
		// CacheBuilder cb = new CacheBuilder(cfgFile);

		EmbeddedCacheManager manager = new DefaultCacheManager();
		manager.defineConfiguration("InCache", new ConfigurationBuilder()
		// .eviction().strategy(EvictionStrategy.LIRS ).maxEntries(1000)
				.build());
		manager.defineConfiguration("CollatorCache", new ConfigurationBuilder()
		// .eviction().strategy(EvictionStrategy.LIRS ).maxEntries(1000)
				.build());
		manager.defineConfiguration("OutCache", new ConfigurationBuilder()
		// .eviction().strategy(EvictionStrategy.LIRS ).maxEntries(1000)
				.build());

		// Cache<Object, Object> c = manager.getCache("custom-cache");

		Cache<String, String> InCache = manager.getCache("InCache");
		Cache<String, List<Integer>> CollectorCache = manager
				.getCache("CollectorCache");
		Cache<String, Integer> OutCache = manager.getCache("OutCache");

		if (textFile != null)
			loadData(InCache);

		for (long word = 0; word < wordsC; word++) {

			InCache.put("rndwd" + word, getLine());
			// collector.emit((kOut)w, 1);
		}

		try {
			if (isMaster) {

				DistributedExecutorService des = new DefaultExecutorService(
						InCache);

				long start = System.currentTimeMillis();
				Properties configuration = new Properties();
				LeadsMapper<String, String, String, Integer> testMapper = new WordCountMapper(
						configuration);
				LeadsCollector<String, Integer> testCollector = new LeadsCollector<String, Integer>(
						5000, CollectorCache);
				LeadsMapperCallable<String, String, String, Integer> testMapperCAll = new LeadsMapperCallable<String, String, String, Integer>(
						InCache, testCollector, testMapper);

				LeadsReducer<String, Integer> testReducer = new WordCountReducer(
						configuration);
				LeadsReduceCallable<String, Integer> testReducerCAll = new LeadsReduceCallable<String, Integer>(
						OutCache, testCollector, testReducer);

				System.out.println("InCache Cache Size:" + InCache.size());

				Future<List<String>> res = des.submit(testMapperCAll);

				if (res.get() != null)
					System.out.println("Mapper Execution is done");
				else
					System.out.println("Mapper Execution not done");
				System.out.println("testCollector Cache Size:"
						+ testCollector.getCache().size());

				Future<List<Integer>> reducer_res = des.submit(testReducerCAll);

				if (reducer_res.get() != null) {
					System.out.println("Reducer Execution is done");
					// List<Integer> wordCountList = reducer_res.get();
					// System.out.println("result " + wordCountList.toString());
				} else
					System.out.println("Reducer Execution not done");
				
				System.out.println("Results: OutCache Size" + OutCache.size());
//				for (Entry<String, Integer> entry : OutCache.entrySet()) {
//					System.out.println("Key: " + entry.getKey() + " Value: " +
//				 entry.getValue() );
//				 }

				System.out.printf("%nCompleted in %s%n%n", Util
						.prettyPrintTime(System.currentTimeMillis() - start));
			} else {
				System.out
						.println("Slave node waiting for Map/Reduce tasks.  Ctrl-C to exit.");
				LockSupport.park();
				System.out.println("Unparked Doing someting.");

			}
		} finally {
			// InCache.getCacheManager().stop();
			manager.stop();

		}
	}
 

	protected SimpleJSAP buildCommandLineOptions() throws JSAPException {
		return new SimpleJSAP("WordCountDemo",
				"Count words in Infinispan cache usin MapReduceTask ",
				new Parameter[] {
						new FlaggedOption("configFile", JSAP.STRING_PARSER,
								"config-samples/distributed-udp.xml",
								JSAP.NOT_REQUIRED, 'c', "configFile",
								"Infinispan transport config file"),
						new FlaggedOption("nodeType", JSAP.STRING_PARSER,
								"slave", JSAP.REQUIRED, 't', "nodeType",
								"Node type as either master or slave"),
						new FlaggedOption("textFile", JSAP.STRING_PARSER, null,
								JSAP.NOT_REQUIRED, 'f', "textFile",
								"Input text file to distribute onto grid"),
						new FlaggedOption("mostPopularWords",
								JSAP.INTEGER_PARSER, "15", JSAP.NOT_REQUIRED,
								'n', "mostPopularWords",
								"Number of most popular words to find") });
	}

	private void loadData(Cache<String, String> cache) throws IOException {
		FileReader in = new FileReader(textFile);
		try {
			BufferedReader bufferedReader = new BufferedReader(in);

			// chunk and insert into cache
			int chunkSize = 10; // 10K
			int chunkId = 0;

			CharBuffer cbuf = CharBuffer.allocate(1024 * chunkSize);
			while (bufferedReader.read(cbuf) >= 0) {
				Buffer buffer = cbuf.flip();
				String textChunk = buffer.toString();
				cache.put(textFile + (chunkId++), textChunk);
				cbuf.clear();
				if (chunkId % 100 == 0)
					System.out.printf(
							"  Inserted %s chunks from %s into grid%n",
							chunkId, textFile);
			}
		} finally {
			Util.close(in);
		}
	}
}
