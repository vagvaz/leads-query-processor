import org.infinispan.ensemble.EnsembleCacheManager;
import org.infinispan.ensemble.cache.EnsembleCache;

import java.io.ObjectInputStream;

/**
 * Created by vagvaz on 4/13/15.
 */
public class ReplayTool {
   private String baseDir;
   private String webpagePrefix;
   private String nutchDataPrefix;
   private String ensembleString;
   private EnsembleCacheManager emanager;
   private EnsembleCache  nutchCache;
   private EnsembleCache webpageCache;

   public ReplayTool(String baseDir, String webpagePrefix, String nutchDataPrefix, String ensembleString){
      this.baseDir = baseDir;
      this.webpagePrefix = webpagePrefix;
      this.nutchDataPrefix = nutchDataPrefix;
      this.ensembleString = ensembleString;
      emanager = new EnsembleCacheManager(ensembleString);
      nutchCache = emanager.getCache("WebPage");
      webpageCache = emanager.getCache("default.webpages");

   }

   public void replayNutch(){
//      ObjectInputStream keyInputStream =
   }

}
