import java.util.HashMap;
import java.util.Map;

public class RedisRepository {
	private static final Map<String, String> REDIS_MAP = new HashMap<>();

	private RedisRepository() {

	}

	public static String get(String key) {
		return REDIS_MAP.getOrDefault(key, null);
	}

	public static void set(String key, String value) {
		REDIS_MAP.put(key, value);
	}

	public static void expire(String key) {
		REDIS_MAP.remove(key);
	}
}
