package redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisDataType {
	SIMPLE_STRINGS("+", true),
	SIMPLE_ERROR("-", true),
	ARRAYS("*", true),
	BULK_STRINGS("$", true),
	EMPTY_TYPE("", true),
	EMPTY_TYPE_WITHOUT_TRAILING("", false);

	private final String firstByte;
	private final boolean isTrailing;
}
