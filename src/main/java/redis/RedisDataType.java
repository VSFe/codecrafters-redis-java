package redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisDataType {
	SIMPLE_STRINGS("+"),
	SIMPLE_ERROR("-"),
	ARRAYS("*"),
	BULK_STRINGS("$"),
	EMPTY_TYPE("");

	private final String firstByte;
}
