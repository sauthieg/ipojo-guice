package org.ow2.jonas.ipojo.guice.example;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Validate;
import org.ow2.jonas.ipojo.guice.GuiceSupport;

import com.google.inject.Inject;

@Component
@GuiceSupport(modules = "sample-module")
public class GuiceInjectedComponent {

	@Inject
	private IRandom randomizer;
	
	@Validate
	public void start() {
		System.out.println("Random number: " + randomizer.getRandom());
	}
}
