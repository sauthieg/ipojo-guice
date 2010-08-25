package org.ow2.jonas.ipojo.guice.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Controller;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.ServiceReference;
import org.ow2.jonas.ipojo.guice.GuiceInjector;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

@Component
@Provides
public class InjectorComponent implements GuiceInjector {

    /**
     * Dependent modules
     */
    private Map<ServiceReference, Module> modules;

    /**
     * Constructed Injector from the modules.
     */
    private Injector injector;
    
    @Property(name = "modules",
              mandatory = true)
    private List<String> required;

    /**
     * Controller attribute (set when all dependent modules are availables).
     */
    @Controller
    private boolean valid;
    
    public InjectorComponent() {
        modules = new HashMap<ServiceReference, Module>();
    }
    
    public Injector getInjector() {
        if (injector == null) {
            injector = Guice.createInjector(getRequiredModules());
        }
        return injector;
    }

    @Bind(optional = false, aggregate = true)
    public void bindModule(Module service, ServiceReference ref) {
        modules.put(ref, service);
        valid = (getRequiredModules().size() == required.size()); 
    }

    private List<Module> getRequiredModules() {
        List<Module> availables = new ArrayList<Module>();
        for (Map.Entry<ServiceReference, Module> entry : modules.entrySet()) {
            if (required.contains(entry.getKey().getProperty("instance.name"))) {
                availables.add(entry.getValue());
            }
        }

        return availables;
    }

    @Unbind
    public void unbindModule(Module service, ServiceReference ref) {
        modules.remove(ref);
        valid = (getRequiredModules().size() == required.size());
    }
    
    @Validate
    public void start() { }

    @Invalidate
    public void stop() { }

}
