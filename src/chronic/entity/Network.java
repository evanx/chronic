/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package chronic.entity;

import vellum.logr.Logr;
import vellum.logr.LogrFactory;
import java.util.List;

/**
 *
 * @author evan.summers
 */
public class Network {
    static Logr logger = LogrFactory.getLogger(Network.class);
    
    String name;
    String label;
    String description;
    boolean enabled = true;
    
    public Network() {
    }

    public Network(String name) {
        this.name = name;
    }
    
    public Comparable getKey() {
        return name;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
