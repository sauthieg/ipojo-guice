package org.ow2.jonas.ipojo.guice.example;

public class Randomizer implements IRandom {

	public double getRandom() {
		return Math.random();
	}

}
