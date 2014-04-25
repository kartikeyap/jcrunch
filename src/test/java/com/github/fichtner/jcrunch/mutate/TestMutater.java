package com.github.fichtner.jcrunch.mutate;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.github.fichtner.jcrunch.mutate.Mutator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.math.IntMath;

public class TestMutater {

	@Test
	public void testAB_1_1() {
		Mutator mutator = new Mutator("ab", 1, 1);
		assertEquals(Arrays.asList("a", "b"),
				Lists.newArrayList(mutator.mutate()));
	}

	@Test
	public void testAB_1_2() {
		Mutator mutator = new Mutator("ab", 1, 2);
		assertEquals(Arrays.asList("a", "b", "aa", "ab", "ba", "bb"),
				Lists.newArrayList(mutator.mutate()));
	}

	@Test
	public void testABC_1_3() {
		Mutator mutator = new Mutator("abc", 1, 3);
		assertEquals(Arrays.asList("a", "b", "c", "aa", "ab", "ac", "ba", "bb",
				"bc", "ca", "cb", "cc", "aaa", "aab", "aac", "aba", "abb",
				"abc", "aca", "acb", "acc", "baa", "bab", "bac", "bba", "bbb",
				"bbc", "bca", "bcb", "bcc", "caa", "cab", "cac", "cba", "cbb",
				"cbc", "cca", "ccb", "ccc"), Lists.newArrayList(mutator
				.mutate()));
	}

	@Test
	public void testABC_2_3() {
		Mutator mutator = new Mutator("abc", 2, 3);
		assertEquals(Arrays.asList("aa", "ab", "ac", "ba", "bb", "bc", "ca",
				"cb", "cc", "aaa", "aab", "aac", "aba", "abb", "abc", "aca",
				"acb", "acc", "baa", "bab", "bac", "bba", "bbb", "bbc", "bca",
				"bcb", "bcc", "caa", "cab", "cac", "cba", "cbb", "cbc", "cca",
				"ccb", "ccc"), Lists.newArrayList(mutator.mutate()));
	}

	@Test
	public void testABC_3_3() {
		Mutator mutator = new Mutator("abc", 3, 3);
		assertEquals(Arrays.asList("aaa", "aab", "aac", "aba", "abb", "abc",
				"aca", "acb", "acc", "baa", "bab", "bac", "bba", "bbb", "bbc",
				"bca", "bcb", "bcc", "caa", "cab", "cac", "cba", "cbb", "cbc",
				"cca", "ccb", "ccc"), Lists.newArrayList(mutator.mutate()));
	}

	@Test
	public void testCounts() {
		String ascii = "abcdefghijklmnopqrstuvwxyz";
		String range = ascii.toLowerCase() + ascii.toUpperCase();
		int rangeA = IntMath.pow(range.length(), 1);
		assertEquals(rangeA, Iterables.size(new Mutator(range, 1, 1).mutate()));

		int rangeB = IntMath.pow(range.length(), 2) + rangeA;
		assertEquals(rangeB, Iterables.size(new Mutator(range, 1, 2).mutate()));

		int rangeC = IntMath.pow(range.length(), 3) + rangeB;
		assertEquals(rangeC, Iterables.size(new Mutator(range, 1, 3).mutate()));

		int rangeD = IntMath.pow(range.length(), 4) + rangeC;
		assertEquals(rangeD, Iterables.size(new Mutator(range, 1, 4).mutate()));
	}

	@Test
	public void testABCD_3_3_WithFilter() {
		Mutator mutator = new Mutator("abcd", 3, 3).outputOnlyIfContains("d");
		List<String> missing = Lists.newArrayList(mutator.mutate());
		assertEquals(Arrays.asList("aad", "abd", "acd", "ada", "adb", "adc",
				"add", "bad", "bbd", "bcd", "bda", "bdb", "bdc", "bdd", "cad",
				"cbd", "ccd", "cda", "cdb", "cdc", "cdd", "daa", "dab", "dac",
				"dad", "dba", "dbb", "dbc", "dbd", "dca", "dcb", "dcc", "dcd",
				"dda", "ddb", "ddc", "ddd"), missing);

		Set<String> l1 = Sets.newHashSet(new Mutator("abc", 3, 3).mutate());
		Set<String> l2 = Sets.newHashSet(new Mutator("abcd", 3, 3).mutate());
		assertEquals(Sets.difference(l2, l1), Sets.newHashSet(missing));
	}

	@Test
	public void testFilter() {
		Mutator mutator = new Mutator("abcde", 1, 5).outputOnlyIfContains("de");
		List<String> missing = Lists.newArrayList(mutator.mutate());

		Set<String> l1 = Sets.newHashSet(new Mutator("abc", 1, 5).mutate());
		Set<String> l2 = Sets.newHashSet(new Mutator("abcde", 1, 5).mutate());
		assertEquals(Sets.difference(l2, l1), Sets.newHashSet(missing));

		// abc: 363
		assertEquals(l1.size(), new Mutator("abc", 1, 5).possibilities()
				.longValue());
		// abcde: 3905
		assertEquals(l2.size(), new Mutator("abcde", 1, 5).possibilities()
				.longValue());
		// 3542
		assertEquals(missing.size(), mutator.possibilities().longValue());
	}

	@Test
	public void testStartAt() {
		Mutator mutator = new Mutator("abc", 3, 3).startAt("cab");
		List<String> expected = Arrays.asList("cab", "cac", "cba", "cbb",
				"cbc", "cca", "ccb", "ccc");
		assertEquals(expected.size(), mutator.possibilities().longValue());
		assertEquals(expected, Lists.newArrayList(mutator.mutate()));
	}

	@Test
	public void testSubPossibilities() {
		Mutator mutator = new Mutator("abc", 1, 3);
		assertEquals(39, mutator.possibilities(1, 1).longValue()
				+ mutator.possibilities(2, 2).longValue()
				+ mutator.possibilities(3, 3).longValue());
	}

	@Test
	public void testGetBytes() {
		assertEquals(141, new Mutator("abc", 1, 3).getBytes().longValue());
		assertEquals(131225000, new Mutator("abcde", 4, 10).getBytes()
				.longValue());
	}
}
