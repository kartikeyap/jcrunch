package com.github.fichtner.jcrunch.numsys;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.fichtner.jcrunch.numsys.NumberSystem;
import com.github.fichtner.jcrunch.numsys.NumberSystems;

public class NumberSystemTest {

	@Test(expected = RuntimeException.class)
	public void testInvalidNumbersystem() {
		new NumberSystem("");
	}

	@Test
	public void testDualFromDecimal() {
		NumberSystem ns = NumberSystems.DUAL;
		assertEquals("0", ns.fromDecimal(0));
		assertEquals("111", ns.fromDecimal(7));
		assertEquals("1000", ns.fromDecimal(8));
		assertEquals("10000", ns.fromDecimal(16));
		assertEquals("1100100", ns.fromDecimal(100));
	}

	@Test
	public void testFualUsingAbFromDecimal() {
		NumberSystem ns = new NumberSystem("ab");
		assertEquals("a", ns.fromDecimal(0));
		assertEquals("bbb", ns.fromDecimal(7));
		assertEquals("baaa", ns.fromDecimal(8));
		assertEquals("baaaa", ns.fromDecimal(16));
		assertEquals("bbaabaa", ns.fromDecimal(100));
	}

	@Test
	public void testOctalFromDecimal() {
		NumberSystem ns = NumberSystems.OCTAL;
		assertEquals("0", ns.fromDecimal(0));
		assertEquals("7", ns.fromDecimal(7));
		assertEquals("10", ns.fromDecimal(8));
		assertEquals("20", ns.fromDecimal(16));
		assertEquals("144", ns.fromDecimal(100));
	}

	@Test
	public void testAbcFromDecimal() {
		NumberSystem ns = new NumberSystem("abc");
		assertEquals("a", ns.fromDecimal(0));
		assertEquals("cb", ns.fromDecimal(7));
		assertEquals("cc", ns.fromDecimal(8));
		assertEquals("bcb", ns.fromDecimal(16));
		assertEquals("bacab", ns.fromDecimal(100));
	}

	@Test
	public void testAbcToDecimal() {
		NumberSystem ns = new NumberSystem("abc");
		assertEquals(0, ns.toDecimal("a"));
		assertEquals(7, ns.toDecimal("cb"));
		assertEquals(8, ns.toDecimal("cc"));
		assertEquals(16, ns.toDecimal("bcb"));
		assertEquals(100, ns.toDecimal("bacab"));
	}

	@Test
	public void testCbaFromDecimal() {
		NumberSystem ns = new NumberSystem("cba");
		assertEquals("c", ns.fromDecimal(0));
		assertEquals("ab", ns.fromDecimal(7));
		assertEquals("aa", ns.fromDecimal(8));
		assertEquals("bab", ns.fromDecimal(16));
		assertEquals("bcacb", ns.fromDecimal(100));
	}

	@Test
	public void testCbaToDecimal() {
		NumberSystem ns = new NumberSystem("cba");
		assertEquals(0, ns.toDecimal("c"));
		assertEquals(7, ns.toDecimal("ab"));
		assertEquals(8, ns.toDecimal("aa"));
		assertEquals(16, ns.toDecimal("bab"));
		assertEquals(100, ns.toDecimal("bcacb"));
	}

}
