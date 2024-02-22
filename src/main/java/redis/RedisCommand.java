package redis;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RedisCommand {
	// Basic Command
	ECHO(true),
	SET(true),
	GET(true),
	CONFIG(true),
	KEYS(true),

	// Replication Command
	INFO(true),
	PING(true),
	REPLCONF(true),
	PSYNC(false);

	private static final Map<String, RedisCommand> commandMap = Arrays.stream(RedisCommand.values())
		.collect(Collectors.toMap(redisCommand -> redisCommand.name().toLowerCase(), it -> it));

	public static RedisCommand parseCommand(String command) {
		return commandMap.get(command.toLowerCase());
	}

	private final boolean isSendMessage;
}
