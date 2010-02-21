package org.ow2.jonas.ipojo.guice.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.PrimitiveHandler;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Controller;
import org.apache.felix.ipojo.annotations.Handler;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.metadata.Element;
import org.osgi.framework.ServiceReference;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

@Handler(name = GuiceHandler.GUICE_SUPPORT,
		 namespace = GuiceHandler.NAMESPACE)
public class GuiceHandler extends PrimitiveHandler {

	public static final String GUICE_SUPPORT = "guicesupport";
	public static final String NAMESPACE = "org.ow2.jonas.ipojo.guice";
	
	private Injector injector;
	
	@Controller
	private boolean isValid = false;
	
	private Map<String, Module> modules;
	
	@Override
	public void configure(Element element, Dictionary configuration)
			throws ConfigurationException {

		Element[] elements = element.getElements(GUICE_SUPPORT, NAMESPACE);
		if (elements.length != 1) {
			throw new ConfigurationException("Too many 'guicesupport' elements.");
		}
		
		// Get the main element
		Element guice = elements[0];
		
		String modulesAttr = guice.getAttribute("modules");
		
		if (modulesAttr == null || "".equals(modulesAttr)) {
			throw new ConfigurationException("Missing 'modules' attribute.");
		}
		
		// The value is of the form {a,b,c,d}
		modulesAttr = modulesAttr.trim();
		modulesAttr = modulesAttr.substring(1);
		modulesAttr = modulesAttr.substring(0, modulesAttr.length() - 1);
		
		System.out.println("Removed {}: " + modulesAttr);
		
		String[] sections = modulesAttr.split(",");
		
		modules = new HashMap<String, Module>();
		for (String section : sections) {
			System.out.println("Module: " + section);
			modules.put(section, null);
		}
	}
	
	@Bind(optional = false/*,
		      filter = "(instance.name=*)"*/)
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
		
		if (allModulesAreAvailables) {
			injector = Guice.createInjector(modules.values());
			isValid = true;
		}
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
		injector.injectMembers(instance);
	}
	
	
	
}
