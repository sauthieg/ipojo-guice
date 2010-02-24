package org.ow2.jonas.ipojo.guice.internal;

import java.util.Dictionary;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Controller;
import org.apache.felix.ipojo.annotations.Handler;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.metadata.Element;
import org.osgi.framework.ServiceReference;
import org.ow2.jonas.ipojo.guice.GuiceInjector;

@Handler(name = GuiceHandler.GUICE_SUPPORT,
		 namespace = GuiceHandler.NAMESPACE)
public class GuiceHandler extends PrimitiveHandler {

	public static final String GUICE_SUPPORT = "guicesupport";
	public static final String NAMESPACE = "org.ow2.jonas.ipojo.guice";
	
	private GuiceInjector injector;
	
	@Controller
	private boolean isValid = false;
	
	/**
	 * Name of the required injector.
	 */
	private String injectorName;
	
	@Override
	public void configure(Element element, Dictionary configuration)
			throws ConfigurationException {

		Element[] elements = element.getElements(GUICE_SUPPORT, NAMESPACE);
		if (elements.length != 1) {
			throw new ConfigurationException("Too many 'guicesupport' elements.");
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
	
	@Bind(optional = false)
	public void bindGuiceInjector(GuiceInjector service, ServiceReference ref) {
		String name = (String) ref.getProperty("instance.name");
		System.out.println("bindGuiceInjector: " + name);
		if (injectorName.equals(name)) {
		    injector = service;
		    isValid = true;
		}
	}
		
	@Unbind
	public void unbindGuiceInjector(GuiceInjector service, ServiceReference ref) {
		String name = (String) ref.getProperty("instance.name");
		System.out.println("unbindGuiceInjector: " + name);
		if (injectorName.equals(name)) {
            injector = null;
            isValid = false;
        }
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
		injector.getInjector().injectMembers(instance);
	}
}
