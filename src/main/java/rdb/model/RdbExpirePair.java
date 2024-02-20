package rdb.model;

import redis.RedisDataType;

public record RdbExpirePair(
	int expireTime,
	RedisDataType valueType,
	String key,
	String value
) {
}
