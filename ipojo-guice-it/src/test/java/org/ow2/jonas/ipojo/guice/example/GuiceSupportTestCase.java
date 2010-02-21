package org.ow2.jonas.ipojo.guice.example;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.ow2.jonas.ipojo.guice.example.exam.GuiceOptions;

import static org.ops4j.pax.exam.CoreOptions.*;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

@RunWith(JUnit4TestRunner.class)
public class GuiceSupportTestCase {

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public static Option[] configuration() {
        return options(frameworks(felix().version("2.0.0")),
                       GuiceOptions.ipojoBundle().versionAsInProject(),
                       GuiceOptions.aopAllianceBundle().versionAsInProject(),
                       GuiceOptions.guiceBundle().versionAsInProject(),
                       mavenBundle().groupId("org.ow2.ipojo.guice").artifactId("ipojo-guice-support").versionAsInProject(),
                       mavenBundle().groupId("org.ow2.ipojo.guice").artifactId("ipojo-guice-example").versionAsInProject()
        );
    }

    @Test
    public void testMethod() {
        assertThat(bundleContext, is(notNullValue()));
    }

}