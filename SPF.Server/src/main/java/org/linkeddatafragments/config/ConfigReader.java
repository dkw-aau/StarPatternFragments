package org.linkeddatafragments.config;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.linkeddatafragments.datasource.IDataSourceType;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class ConfigReader {
    private final Map<String, IDataSourceType> dataSourceTypes = new HashMap<>();
    private final Map<String, JsonObject> dataSources = new HashMap<>();
    private final Map<String, String> prefixes = new HashMap<>();
    private final String baseURL;
    private final String metadatapath;
    /**
     * Creates a new configuration reader.
     *
     * @param configReader the configuration
     */
    public ConfigReader(Reader configReader) {
        JsonObject root = new JsonParser().parse(configReader).getAsJsonObject();
        this.baseURL = root.has("baseURL") ? root.getAsJsonPrimitive("baseURL").getAsString() : null;
        this.metadatapath = root.has("metadatapath") ? root.getAsJsonPrimitive("metadatapath").getAsString() : null;
        for (Entry<String, JsonElement> entry : root.getAsJsonObject("datasourcetypes").entrySet()) {
            final String className = entry.getValue().getAsString();
            dataSourceTypes.put(entry.getKey(), initDataSouceType(className) );
        }
        for (Entry<String, JsonElement> entry : root.getAsJsonObject("datasources").entrySet()) {
            JsonObject dataSource = entry.getValue().getAsJsonObject();
            this.dataSources.put(entry.getKey(), dataSource);
        }
        for (Entry<String, JsonElement> entry : root.getAsJsonObject("prefixes").entrySet()) {
            this.prefixes.put(entry.getKey(), entry.getValue().getAsString());
        }
    }

    /**
     * Gets the data source types.
     *
     * @return a mapping of names of data source types to these types  
     */
    public Map<String, IDataSourceType> getDataSourceTypes() {
        return dataSourceTypes;
    }

    /**
     * Gets the data sources.
     *
     * @return the data sources
     */
    public Map<String, JsonObject> getDataSources() {
        return dataSources;
    }

    /**
     * Gets the prefixes.
     *
     * @return the prefixes
     */
    public Map<String, String> getPrefixes() {
        return prefixes;
    }

    /**
     * Gets the base URL
     * 
     * @return the base URL
     */
    public String getBaseURL() {
        return baseURL;
    }
    
      /**
     * @return the metadatapath
     */
    public String getMetadatapath() {
        return metadatapath;
    }

    /**
     * Loads a certain {@link IDataSourceType} class at runtime
     * 
     * @param className IDataSourceType class
     * @return the created IDataSourceType object
     */
    protected IDataSourceType initDataSouceType( final String className )
    {
        final Class<?> c;
        try {
            c = Class.forName( className ); 
        }
        catch ( ClassNotFoundException e ) {
            throw new IllegalArgumentException( "Class not found: " + className,
                                                e );
        }

        final Object o;
        try {
            o = c.newInstance();
        }
        catch ( Exception e ) {
            throw new IllegalArgumentException(
                        "Creating an instance of class '" + className + "' " +
                        "caused a " + e.getClass().getSimpleName() + ": " +
                        e.getMessage(), e );
        }

        if ( ! (o instanceof IDataSourceType) )
            throw new IllegalArgumentException(
                        "Class '" + className + "' is not an implementation " +
                        "of IDataSourceType." );

        return (IDataSourceType) o;
    }

  

}
