package com.github.fichtner.jcrunch.numsys;

public class NumberSystems {

	private static final String DIGITS = "0123456789ABCDEF";

	private NumberSystems() {
		super();
	}

	public static final NumberSystem DUAL = newNumberSystem(2);

	public static final NumberSystem OCTAL = newNumberSystem(8);

	public static final NumberSystem HEX = newNumberSystem(16);

	private static NumberSystem newNumberSystem(int base) {
		return new NumberSystem(DIGITS.substring(0, base));
	}

}
