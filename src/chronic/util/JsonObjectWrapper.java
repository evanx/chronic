/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.datatype.Millis;
import vellum.jx.JMap;
import vellum.jx.JMapException;
import vellum.jx.JMaps;
import vellum.util.ExtendedProperties;
import vellum.util.Strings;

/**
 *
 * @author evan.summers
 */
public class JsonObjectWrapper {
    private JsonObject object;
    private static final Logger logger = LoggerFactory.getLogger(JsonObjectWrapper.class);

    public JsonObjectWrapper(JsonObject object) {
        this.object = object;
    }

    public JsonObjectWrapper(Reader reader) throws FileNotFoundException {
        this(new JsonParser().parse(reader).getAsJsonObject());
    }
    
    public JsonObjectWrapper(File file) throws FileNotFoundException {
        this(new FileReader(file));
    }

    public JsonObjectWrapper(InputStream inputStream) throws FileNotFoundException {
        this(new JsonParser().parse(new InputStreamReader(inputStream)).getAsJsonObject());
    }

    public boolean hasProperty(String key) {
        return object.get(key) != null && object.get(key).isJsonPrimitive();
    }
    
    public boolean hasProperties(String key) {
        return object.get(key) != null && object.get(key).isJsonObject();
    }

    public JMap getMap() {
        return JMaps.parse(object);
    }
    
    public ExtendedProperties getProperties() {
        ExtendedProperties properties = new ExtendedProperties();
        for (Entry<String, JsonElement> entry : object.entrySet()) {
            logger.info("get {} {}", entry.getKey(), 
                    Strings.truncate(32, entry.getValue().getAsString()));
            properties.put(entry.getKey(), entry.getValue().getAsString());
        }
        return properties;
    }
    
    public ExtendedProperties getProperties(String key) {
        ExtendedProperties properties = new ExtendedProperties();
        for (Entry<String, JsonElement> entry : object.get(key).getAsJsonObject().entrySet()) {
            properties.put(entry.getKey(), entry.getValue().getAsString());
        }
        return properties;
    }

    public Collection<String> keySet() {
        List<String> list = new ArrayList();
        for (Entry<String, JsonElement> entry : object.entrySet()) {
            list.add(entry.getKey());
        }
        return list;
    }
    
    public Collection<String> keySet(String key) {
        List<String> list = new ArrayList();
        for (Entry<String, JsonElement> entry : object.get(key).getAsJsonObject().entrySet()) {
            list.add(entry.getKey());
        }
        return list;
    }
    
    public String getString(String key) throws JMapException {
        JsonElement element = get(key);
        if (element == null) {
            throw new JMapException(key);
        }
        return element.getAsString();
    }
    
    public String getString(String key, String defaultValue) {
        JsonElement element = get(key);
        if (element == null) {
            return defaultValue;
        }
        return element.getAsString();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        JsonElement element = get(key);
        if (element == null) {
            return defaultValue;
        }
        return element.getAsBoolean();
    }

    public long getLong(String key, long defaultValue) {
        JsonElement element = get(key);
        if (element == null) {
            return defaultValue;
        }
        return element.getAsLong();
    }    
    
    public long getMillis(String key, long defaultValue) {
        JsonElement element = get(key);
        if (element == null) {
            return defaultValue;
        }
        return Millis.parse(element.getAsString());
    }    
    
    private JsonElement get(String key) {
        JsonElement element = object.get(key);
        logger.info("get {} {}", key, element);
        return element;
    }

    @Override
    public String toString() {
        return object.getAsString();
    }

    public Set<String> getStringSet(String key) {
        Set<String> set = new HashSet();
        JsonArray array = get(key).getAsJsonArray();
        for (JsonElement element : array) {
            set.add(element.getAsString());
        }
        return set;
    }
    
}
