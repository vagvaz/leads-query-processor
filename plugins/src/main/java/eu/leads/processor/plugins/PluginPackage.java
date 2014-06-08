package eu.leads.processor.plugins;

import com.google.common.base.Strings;
import eu.leads.processor.common.utils.FSUtilities;

import java.io.Serializable;

/**
 * Created by vagvaz on 6/3/14.
 */
public class PluginPackage implements Serializable {
    private byte[] jar;
    private String className;
    private String id;
    private byte[] config;


    public PluginPackage(String id, String className) {
        this.id = id;
        this.className = className;
    }

    public PluginPackage(String id, String className, String jarFileName) {
        this.id = id;
        this.className = className;
        if (!Strings.isNullOrEmpty(jarFileName)) {
            loadJarFromFile(jarFileName);
        }
    }

    public PluginPackage(String id, String className, String jarFileName, String configFileName) {
        this.id = id;
        this.className = className;
        if (!Strings.isNullOrEmpty(jarFileName)) {
            loadJarFromFile(jarFileName);
        }
        if (!Strings.isNullOrEmpty(configFileName)) {
            loadConfigFromFile(configFileName);
        }
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private void loadConfigFromFile(String configFileName) {
        config = FSUtilities.loadBytesFromFile(configFileName);
    }

    public byte[] getJar() {
        return jar;
    }


    public void setJar(byte[] jar) {
        this.jar = jar;
    }

    public byte[] getConfig() {
        return config;
    }

    public void setConfig(byte[] config) {
        this.config = config;
    }

    public void loadJarFromFile(String jarFileName) {

        jar = FSUtilities.loadBytesFromFile(jarFileName);
    }
}
