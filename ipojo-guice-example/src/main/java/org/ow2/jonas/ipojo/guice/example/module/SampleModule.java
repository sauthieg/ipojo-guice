package org.ow2.jonas.ipojo.guice.example.module;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.ow2.jonas.ipojo.guice.example.IRandom;
import org.ow2.jonas.ipojo.guice.example.Randomizer;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

@Component
@Provides(specifications = Module.class)
public class SampleModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IRandom.class).to(Randomizer.class);
	}

}
