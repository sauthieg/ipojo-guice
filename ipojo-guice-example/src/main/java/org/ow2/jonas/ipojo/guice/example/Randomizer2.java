package org.ow2.jonas.ipojo.guice.example;

public class Randomizer2 implements IRandom {

	public double getRandom() {
		return Math.random() + 10;
	}

}
