package redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisRepository {
	private static final Map<String, String> REDIS_MAP = new HashMap<>();
	private static final Map<String, String> REDIS_CONFIG_MAP = new HashMap<>();

	private RedisRepository() {

	}

	public static String get(String key) {
		return REDIS_MAP.getOrDefault(key, null);
	}

	public static List<String> getKeys() {
		return REDIS_MAP.keySet().stream().toList();
	}

	public static String configGet(String key) {
		return REDIS_CONFIG_MAP.getOrDefault(key, null);
	}

	public static void set(String key, String value) {
		REDIS_MAP.put(key, value);
	}

	public static void configSet(String key, String value) {
		REDIS_CONFIG_MAP.put(key, value);
	}

	public static void expireWithExpireTime(String key, long expireTime) {
		new Thread(() -> {
			try {
				Thread.sleep(expireTime));
				RedisRepository.expire(key);
			} catch (Exception e) {
				log.error("expire failed.", e);
			}
		}).start();
	}

	public static void expire(String key) {
		REDIS_MAP.remove(key);
	}
}
