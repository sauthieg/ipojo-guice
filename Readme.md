Description
-----------

iPOJO Guice Support.
This library has the aim to provide an iPOJO/Guice integration.

Example
--------

A component that want to take advantage of this integration needs to perform the following step:

## Module(s) component

Creates a classical Guice module that will be exported as an OSGi service.
   @Component
   @Provides(specifications = Module.class)
   @Instanciate
   public class SampleModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IRandom.class).to(Randomizer.class);
	}
   }

and it's associate instance (needs to provide a name that we will use later):
   <ipojo xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="org.apache.felix.ipojo http://felix.apache.org/ipojo/schemas/CURRENT/core.xsd"
       xmlns="org.apache.felix.ipojo">

	<instance component="org.ow2.jonas.ipojo.guice.example.module.SampleModule"
	          name="sample-module" />

   </ipojo>


## Injector component

Just define an instance of the pre-defined GuiceInjectorComponent provided by this library.
   <ipojo xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="org.apache.felix.ipojo http://felix.apache.org/ipojo/schemas/CURRENT/core.xsd"
       xmlns="org.apache.felix.ipojo">

	<instance component="org.ow2.jonas.ipojo.guice.internal.InjectorComponent"
	          name="sample-injector">
	    <property name="modules" type="list">
	        <property value="sample-module" />
	    </property>
	</instance>

   </ipojo>

## Injected component

Creates a classic iPOJO component that will be annotated with the @GuiceSupport annotation.
   @Component
   @GuiceSupport(name = "sample-injector")
   public class GuiceInjectedComponent {

	@Inject
	private IRandom randomizer;

	@Validate
	public void start() {
		System.out.println("Random number: " + randomizer.getRandom());
	}
   }

## Runtime

Once the bundles have been started, you should see the following output:

   Start SampleModule
   Random number: 0.10249957740836657

Toughts (for the future)
--------

* Contribution to Apache iPOJO or OW2 Chameleon
* Ease usage (maybe remove the explicit injector component creation)

Building
--------

### Requirements

* Maven 2+
* Java 5+

Check out and build:

    git clone git://github.com/sauthieg/ipojo-guice.git
    cd ipojo-guice
    mvn install
