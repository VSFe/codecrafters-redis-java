import java.util.List;

public record RedisResultData(
	RedisDataType redisDataType,
	String data
) {
	public static List<RedisResultData> getSimpleResultData(RedisDataType redisDataType, String data) {
		return List.of(new RedisResultData(redisDataType, data));
	}

	public static List<RedisResultData> getBulkStringData(String data) {
		var sizeData = new RedisResultData(RedisDataType.BULK_STRINGS, data == null ? "-1" : String.valueOf(data.length()));
		var strData = new RedisResultData(RedisDataType.EMPTY_TYPE, data);

		return data == null ? List.of(sizeData) : List.of(sizeData, strData);
	}
}
