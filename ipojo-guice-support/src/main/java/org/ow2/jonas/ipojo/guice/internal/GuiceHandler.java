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
    }

    private void updateValidity() {
        // It is possible that this method is called BEFORE configure()
        GuiceInjector found = null;
        if (injectorName != null) {
            for (Entry<ServiceReference, GuiceInjector> guice : injectors.entrySet()) {
                String name = (String) guice.getKey().getProperty("instance.name");
                if (injectorName.equals(name)) {
                    found = guice.getValue();
                }
            }

            if (found != null) {
                injector = found;
                setValidity(true);
            } else {
                setValidity(false);
            }
        }
    }

    @Bind(optional = true, aggregate = true)
    public void bindGuiceInjector(GuiceInjector service, ServiceReference ref) {
        // services are injected BEFORE configure() is called
        injectors.put(ref, service);
        updateValidity();
    }

    @Unbind
    public void unbindGuiceInjector(GuiceInjector service, ServiceReference ref) {
        injectors.remove(ref);
        updateValidity();
    }

    @Override
    public void start() {
        // Update validity of this Handler
        updateValidity();
    }

    @Override
    public void stop() { }

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
