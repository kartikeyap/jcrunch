package com.github.fichtner.jcrunch.numsys;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.math.LongMath.checkedAdd;
import static com.google.common.math.LongMath.checkedMultiply;
import static com.google.common.primitives.Ints.checkedCast;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Chars;

public class NumberSystem {

	private final char[] digits;
	private final int numDigits;

	public NumberSystem(String digits) {
		checkArgument(
				checkNotNull(digits, "digits must not be null").length() > 1,
				"digits must contain at least two characters");
		List<Character> characters = Lists.charactersOf(digits);
		checkState(Sets.newHashSet(characters).size() == digits.length(),
				"Duplicates found in %s", digits);
		this.digits = Chars.toArray(characters);
		this.numDigits = digits.length();
	}

	public String getDigits() {
		return Chars.join("", digits);
	}

	public String fromDecimal(long dec) {
		if (dec == 0) {
			return String.valueOf(this.digits[0]);
		}
		// TODO What's the result size? -> Pass capacity
		StringBuilder sb = new StringBuilder();
		while (dec > 0) {
			sb.insert(0, this.digits[checkedCast(dec % this.numDigits)]);
			dec /= this.numDigits;
		}
		return sb.toString();
	}

	public String from(NumberSystem other, String value) {
		return fromDecimal(other.toDecimal(value));
	}

	public long toDecimal(String string) {
		long result = 0;
		long pow = 1;
		for (int i = 0; i < string.length(); i++) {
			char charAt = string.charAt(string.length() - i - 1);
			int indexOf = Chars.indexOf(this.digits, charAt);
			checkArgument(indexOf >= 0, "%s is not a valid character in %s",
					charAt, this.digits);
			result = checkedAdd(result, checkedMultiply(indexOf, pow));
			pow = checkedMultiply(pow, numDigits);
		}
		return result;
	}

}
