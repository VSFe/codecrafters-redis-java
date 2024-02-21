package common;

import org.slf4j.helpers.MessageFormatter;

public class StringUtil {
	private StringUtil() {

	}

	public static String format(String format, String... args) {
		return MessageFormatter.arrayFormat(format, args).getMessage();
	}
}
