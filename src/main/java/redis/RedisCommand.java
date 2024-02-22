package redis;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum RedisCommand {
	// Basic Command
	ECHO,
	SET,
	GET,
	CONFIG,
	KEYS,

	// Replication Command
	INFO,
	PING,
	REPLCONF;

	private static final Map<String, RedisCommand> commandMap = Arrays.stream(RedisCommand.values())
		.collect(Collectors.toMap(redisCommand -> redisCommand.name().toLowerCase(), it -> it));

	public static RedisCommand parseCommand(String command) {
		return commandMap.get(command.toLowerCase());
	}
}
