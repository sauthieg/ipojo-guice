package org.ow2.jonas.ipojo.guice.example;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.frameworks;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.ow2.jonas.ipojo.guice.example.exam.GuiceOptions;

@RunWith(JUnit4TestRunner.class)
public class GuiceSupportTestCase {

    @Inject
    private BundleContext bundleContext;
    
    private Factory sampleComponentFactory;
    private Factory module1ComponentFactory;
    private Factory module2ComponentFactory;
    private Factory injectorComponentFactory;

    @Configuration
    public static Option[] configuration() {
        return options(frameworks(felix().version("2.0.0")),
                       systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("DEBUG"), 
                       GuiceOptions.ipojoBundle().versionAsInProject(),
                       GuiceOptions.aopAllianceBundle().versionAsInProject(),
                       GuiceOptions.guiceBundle().versionAsInProject(),
                       mavenBundle().groupId("org.ow2.ipojo.guice").artifactId("ipojo-guice-support").versionAsInProject(),
                       mavenBundle().groupId("org.ow2.ipojo.guice").artifactId("ipojo-guice-example").versionAsInProject()
        );
    }

    @Test
    public void testSimpleModule() throws Exception {
        
        // Create module
        Properties moduleProperties = new Properties();
        moduleProperties.setProperty("instance.name", "module1");
        ComponentInstance m1 = module1ComponentFactory.createComponentInstance(moduleProperties);
        m1.start();
        assertThat(m1.getState(), is(ComponentInstance.VALID));
        
        // Create injector
        Dictionary<String, Object> injectorProperties = new Hashtable<String, Object>();
        injectorProperties.put("instance.name", "sample");
        injectorProperties.put("modules", new String[] {"module1"});
        ComponentInstance injector = injectorComponentFactory.createComponentInstance(injectorProperties);
        injector.start();
        assertThat(injector.getState(), is(ComponentInstance.VALID));
        
        // Create component
        Dictionary<String, Object> componentConfig = new Hashtable<String, Object>();
        componentConfig.put("injector.name", "sample");
        ComponentInstance cmp = sampleComponentFactory.createComponentInstance(componentConfig);
        cmp.start();
        assertThat(cmp.getState(), is(ComponentInstance.VALID));
        
        // TODO add interface to the component to check the result
    }
    
    @Before
    public void setUp() throws Exception {
        this.sampleComponentFactory = getComponentFactory("org.ow2.jonas.ipojo.guice.example.GuiceInjectedComponent");
        this.module1ComponentFactory = getComponentFactory("org.ow2.jonas.ipojo.guice.example.module.SampleModule");
        this.module2ComponentFactory = getComponentFactory("org.ow2.jonas.ipojo.guice.example.module.AnotherModule");
        this.injectorComponentFactory = getComponentFactory("org.ow2.jonas.ipojo.guice.internal.InjectorComponent");
        
        assertThat(sampleComponentFactory, is(notNullValue()));
        assertThat(module1ComponentFactory, is(notNullValue()));
        assertThat(module2ComponentFactory, is(notNullValue()));
        assertThat(injectorComponentFactory, is(notNullValue()));
    }
    
    private Factory getComponentFactory(String name) throws Exception {
        ServiceReference[] refs = bundleContext.getServiceReferences(Factory.class.getName(),
                "(factory.name=" + name + ")");
        if (refs == null) {
            return null;
        }
        
        return (Factory) bundleContext.getService(refs[0]);
    }

}