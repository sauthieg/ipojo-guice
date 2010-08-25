package org.ow2.jonas.ipojo.guice.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Pojo;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Controller;
import org.apache.felix.ipojo.annotations.Handler;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.metadata.Element;
import org.osgi.framework.ServiceReference;
import org.ow2.jonas.ipojo.guice.GuiceInjector;

@Handler(name = GuiceHandler.GUICE_SUPPORT,
         namespace = GuiceHandler.NAMESPACE,
         architecture = true)
public class GuiceHandler extends PrimitiveHandler {

    public static final String                   GUICE_SUPPORT = "guicesupport";
    public static final String                   NAMESPACE     = "org.ow2.jonas.ipojo.guice";

    private GuiceInjector                        injector;

    /**
     * Name of the required injector.
     */
    private String                               injectorName;

    private Map<ServiceReference, GuiceInjector> injectors;
    
    public GuiceHandler() {
        injectors = new Hashtable<ServiceReference, GuiceInjector>();
    }

    @Override
    public void configure(Element element, Dictionary configuration)
            throws ConfigurationException {

        Element[] elements = element.getElements(GUICE_SUPPORT, NAMESPACE);
        if (elements.length != 1) {
            throw new ConfigurationException(
                    "Too many 'guicesupport' elements.");
        }

        // Get the main element
        Element guice = elements[0];

        injectorName = (String) configuration.get("injector.name");
        if (injectorName == null || "".equals(injectorName)) {
            injectorName = guice.getAttribute("name");

            if (injectorName == null || "".equals(injectorName)) {
                throw new ConfigurationException("Missing 'name' attribute.");
            }
        }
        
        System.out.println("configure : requires injector " + injectorName);

        // Update validity of this Handler
        updateValidity();
    }

    private void updateValidity() {
        // It is possible that this method is called BEFORE configure()
        System.out.println("entering:updateValidity()");
        GuiceInjector found = null;
        if (injectorName != null) {
            System.out.println("updateValidity() -> searching " + injectorName);
            for (Entry<ServiceReference, GuiceInjector> guice : injectors.entrySet()) {
                String name = (String) guice.getKey().getProperty("instance.name");
                System.out.println("updateValidity() proposed injector " + name);
                if (injectorName.equals(name)) {
                    found = guice.getValue();
                }
            }

            Pojo me = (Pojo) this;
            System.out.println("description " + me.getComponentInstance().getInstanceDescription().getDescription());

            if (found != null) {
                System.out.println("updateValidity() found injector " + found);
                System.out.println("updateValidity() state (old) " + getInstanceManager().getState());
                injector = found;
                setValidity(true);
                System.out.println("updateValidity() state (new) " + getInstanceManager().getState());

            } else {
                System.out.println("updateValidity() injector not found");
                setValidity(false);
            }
        }
        System.out.println("exiting:updateValidity() with state: " + getValidity());
    }

    @Bind(optional = true, aggregate = true)
    public void bindGuiceInjector(GuiceInjector service, ServiceReference ref) {
        String name = (String) ref.getProperty("instance.name");
        System.out.println("bindGuiceInjector: " + name);
        // services are injected BEFORE configure() is called
        injectors.put(ref, service);
        updateValidity();
    }

    @Unbind
    public void unbindGuiceInjector(GuiceInjector service, ServiceReference ref) {
        String name = (String) ref.getProperty("instance.name");
        System.out.println("unbindGuiceInjector: " + name);
        injectors.remove(ref);
        updateValidity();
    }

    @Override
    public void start() {
        System.out.println("Starting Guice Handler ...");
    }

    @Override
    public void stop() {
        System.out.println("Stopping Guice Handler ...");
    }

    @Override
    public void onCreation(Object instance) {
        // Inject the created instance
        if (isValid()) {
            injector.getInjector().injectMembers(instance);
        } else {
            System.out.println("onCreation, but Handler is invalid ");
        }
    }
}
