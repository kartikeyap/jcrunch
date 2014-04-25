package com.github.fichtner.jcrunch.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.github.fichtner.jcrunch.mutate.Mutator;
import com.google.common.base.Strings;

public class JCrunch {

	private static final String ASCII = "abcdefghijklmnopqrstuvwxyz";
	private static final String NUMBERS = "0123456789";
	private static final String COMMON_SPECIAL_CHARS = "!?.";

	private static final String KB = "KB";

	@Option(name = "-d", usage = "characters to use", required = false)
	private String digits = ASCII.toLowerCase() + ASCII.toUpperCase() + NUMBERS
			+ COMMON_SPECIAL_CHARS;

	@Option(name = "-l", aliases = "-low", usage = "minimum length", required = true)
	private int min;

	@Option(name = "-h", aliases = "-high", usage = "maximum length", required = true)
	private int max;

	@Option(name = "-i", usage = "do only output line if line contains one of these characters", required = false)
	private String checkOnlyIfContains;

	@Option(name = "-s", aliases = "startAt", usage = "(re)start/resume with this line", required = false)
	private String startAt;

	@Option(name = "-n", aliases = "noStatusLine", usage = "do not print statusline (first line)", required = false)
	private boolean noStatusLine = false;

	public static void main(String[] args) throws FileNotFoundException,
			IOException, InterruptedException, ExecutionException {
		new JCrunch().doMain(args);
	}

	private void doMain(String[] args) throws InterruptedException {
		CmdLineParser cmdLineParser = new CmdLineParser(this);

		try {
			cmdLineParser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			cmdLineParser.printUsage(System.err);
			return;
		}

		Mutator mutator = new Mutator(this.digits, this.min, this.max);
		mutator = Strings.isNullOrEmpty(this.checkOnlyIfContains) ? mutator
				: mutator.outputOnlyIfContains(checkOnlyIfContains);
		mutator = Strings.isNullOrEmpty(this.startAt) ? mutator : mutator
				.startAt(this.startAt);

		PrintStream stream = System.err;
		if (!this.noStatusLine) {
			BigDecimal bytes = mutator.getBytes();
			stream.println("JCrunch will now generate the following amount of data: "
					+ bytes + " bytes");

			for (String unit : Arrays.asList(KB, "MB", "GB", "TB", "PB")) {
				bytes = bytes.divide(BigDecimal.valueOf(1024));
				if (!KB.equals(unit)) {
					stream.println(bytes.setScale(0, RoundingMode.HALF_UP)
							+ " " + unit);
				}
			}

			stream.println("JCrunch will now generate the following number of lines: "
					+ mutator.possibilities());
			TimeUnit.SECONDS.sleep(3);
		}
		for (String passphrase : mutator.mutate()) {
			System.out.println(passphrase);
		}

	}

}
