/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 */
package chronic.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.datatype.Millis;
import vellum.util.ExtendedProperties;

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
        
    public JsonObjectWrapper(String fileName) throws FileNotFoundException {
        this(new JsonParser().parse(new FileReader(fileName)).getAsJsonObject());
    }

    public boolean hasProperties(String key) {
        return object.get(key) != null && object.get(key).isJsonObject();
    }
    
    public ExtendedProperties getProperties(String key) {
        ExtendedProperties properties = new ExtendedProperties();
        for (Entry<String, JsonElement> entry : object.get(key).getAsJsonObject().entrySet()) {
            properties.put(entry.getKey(), entry.getValue().getAsString());
        }
        return properties;
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
    
}
