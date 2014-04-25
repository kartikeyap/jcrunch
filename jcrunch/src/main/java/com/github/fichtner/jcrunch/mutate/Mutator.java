package com.github.fichtner.jcrunch.mutate;

import java.math.BigDecimal;
import java.util.Iterator;

import com.github.fichtner.jcrunch.numsys.NumberSystem;
import com.google.common.base.CharMatcher;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Iterators;
import com.google.common.math.LongMath;

public class Mutator {

	private static class CharMatcherFilter implements Predicate<String> {

		private final String string;
		private final CharMatcher charMatcher;

		public CharMatcherFilter(String string) {
			this.string = string;
			// TODO all chars of "string" must be included in
			// numberSystem#getDigits
			this.charMatcher = CharMatcher.anyOf(string);
		}

		public String getString() {
			return string;
		}

		@Override
		public boolean apply(String input) {
			return charMatcher.matchesAnyOf(input);
		}

	}

	public class Itr implements Iterator<String> {

		private long cnt;
		private int length;
		private final char zero;
		private long increaseLengthAt;
		private int maxLength;

		public Itr() {
			this.maxLength = Mutator.this.maxLength;
			this.cnt = Mutator.this.startAt;
			this.length = Mutator.this.minLength;
			this.zero = Mutator.this.numberSystem.getDigits().charAt(0);
			calcIncLength(this.length);
		}

		private void calcIncLength(int len) {
			this.increaseLengthAt = LongMath.pow(numberSystem.getDigits()
					.length(), len);
		}

		@Override
		public boolean hasNext() {
			return this.length <= maxLength;
		}

		@Override
		public String next() {
			String result = Strings.padStart(
					numberSystem.fromDecimal(this.cnt), this.length, this.zero);
			this.cnt++;
			if (this.cnt == this.increaseLengthAt) {
				calcIncLength(++this.length);
				this.cnt = 0;
			}
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private final NumberSystem numberSystem;

	private final int minLength;

	private final int maxLength;

	private Predicate<String> filter = Predicates.alwaysTrue();

	private long startAt;

	public Mutator(String string, int minLength, int maxLength) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.numberSystem = new NumberSystem(string);
	}

	/**
	 * Let's say you already did mutate a,b,c and think you should retry a run
	 * with a,b,c,x there is no need to retry all the combinations already tried
	 * (like aaa, aab) and so on but only those where "x" is in place.
	 * 
	 * @param string
	 *            characters which must be contained in mutation string
	 * @return this Mutator
	 */
	public Mutator outputOnlyIfContains(final String string) {
		this.filter = new CharMatcherFilter(string);
		return this;
	}

	public Mutator startAt(String startAt) {
		this.startAt = this.numberSystem.toDecimal(startAt);
		return this;
	}

	public Iterable<String> mutate() {
		return new Iterable<String>() {
			@Override
			public Iterator<String> iterator() {
				Itr itr = new Itr();
				return Mutator.this.filter == null
						|| Mutator.this.filter == Predicates
								.<String> alwaysTrue() ? itr : Iterators
						.filter(itr, Mutator.this.filter);
			}
		};
	}

	public BigDecimal possibilities() {
		return possibilities(this.minLength, this.maxLength);
	}

	public BigDecimal possibilities(int from, int to) {
		String digits = this.numberSystem.getDigits();
		BigDecimal phrasesToTry = calcPossibilities(digits.length(), from, to);
		phrasesToTry = phrasesToTry.subtract(new BigDecimal(this.startAt));
		if (this.filter instanceof CharMatcherFilter) {
			CharMatcherFilter cmf = (CharMatcherFilter) this.filter;
			phrasesToTry = phrasesToTry.subtract(calcPossibilities(
					this.numberSystem.getDigits().length()
							- cmf.getString().length(), from, to));
		}
		return phrasesToTry;
	}

	private static BigDecimal calcPossibilities(int digitsLength, int min,
			int max) {
		BigDecimal possibilities = new BigDecimal(0);
		for (int i = min; i <= max; i++) {
			possibilities = possibilities.add(new BigDecimal(digitsLength)
					.pow(i));
		}
		return possibilities;
	}

	public BigDecimal getBytes() {
		return getBytes(true);

	}

	public BigDecimal getBytes(boolean countLineSeparators) {
		BigDecimal bytes = BigDecimal.ZERO;
		int toAdd = countLineSeparators ? System.getProperty("line.separator")
				.length() : 0;
		for (int i = this.minLength; i <= this.maxLength; i++) {
			BigDecimal possibilities = possibilities(i, i);
			bytes = bytes.add(possibilities.multiply(BigDecimal.valueOf(i
					+ toAdd)));
		}
		return bytes;
	}

}
