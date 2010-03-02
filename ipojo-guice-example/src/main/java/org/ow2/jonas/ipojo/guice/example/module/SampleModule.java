package org.ow2.jonas.ipojo.guice.example.module;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.ipojo.guice.example.IRandom;
import org.ow2.jonas.ipojo.guice.example.Randomizer;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

@Component
@Provides(specifications = Module.class)
public class SampleModule extends AbstractModule {

    @Validate
    public void start() {
        System.out.println("Start " + getClass().getSimpleName());
    }

    @Invalidate
    public void stop() {
        System.out.println("Stop " + getClass().getSimpleName());
    }

	@Override
	protected void configure() {
		bind(IRandom.class).to(Randomizer.class);
	}

}
