package org.ow2.jonas.ipojo.guice.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Controller;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;
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
    private Map<String, Module> modules;

    /**
     * Constructed Injector from the modules.
     */
    private Injector injector;

    /**
     * Controller attribute (set when all dependent modules are availables).
     */
    @Controller
    private boolean valid;
    
    public InjectorComponent() {
        modules = new HashMap<String, Module>();
    }
    
    @Property(name = "modules",
              mandatory = true)
    public void setModuleNames(String[] names) {
        for (String name : names) {
            modules.put(name, null);
        }
    }
    
    public Injector getInjector() {
        if (injector == null) {
            injector = Guice.createInjector(modules.values());
        }
        return injector;
    }

    @Bind(optional = false)
    public void bindModule(Module service, ServiceReference ref) {
        String name = (String) ref.getProperty("instance.name");
        System.out.println("bindModule: " + name);
        if (modules.containsKey(name)) {
            modules.put(name, service);
        }
        updateStatus();
    }

    private void updateStatus() {
        boolean allModulesAreAvailables = true;
        for (Map.Entry<String, Module> entry : modules.entrySet()) {
            System.out.println("updateStatus >" +entry.getKey()+ "< = >" +entry.getValue()+ "<");
            if (entry.getValue() == null) {
                allModulesAreAvailables = false;
            }
        }

        valid = allModulesAreAvailables;
    }

    @Unbind
    public void unbindModule(Module service, ServiceReference ref) {
        String name = (String) ref.getProperty("instance.name");
        System.out.println("unbindModule: " + name);
        if (modules.containsKey(name)) {
            modules.remove(name);
        }
        updateStatus();
    }
}
