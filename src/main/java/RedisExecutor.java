import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisExecutor {
	private RedisExecutor() {

	}

	public static void parseAndExecute(BufferedWriter writer, List<String> inputParams) {
		try {
			if (!checkSupported(inputParams.getFirst())) {
				returnCommonErrorMessage(writer);
				return;
			}

			var data = executeCommand(inputParams);
			var outputStr = convertToOutputString(data);

			log.debug("output: {}", outputStr);
			writer.write(outputStr);
			writer.flush();
		} catch (RuntimeException e) {
			log.warn("command execute error - inputParams: {}", inputParams, e);
			returnCommonErrorMessage(writer);
		} catch (IOException e) {
			log.error("IOException", e);
		}
	}

	public static void returnCommonErrorMessage(BufferedWriter writer) {
		try {
			writer.write("-ERR\r\n");
			writer.flush();
		} catch (IOException e) {
			log.error("IOException", e);
		}
	}

	private static boolean checkSupported(String command) {
		return RedisCommand.parseCommand(command) != null;
	}

	private static List<RedisResultData> executeCommand(List<String> inputParams) {
		var command = RedisCommand.parseCommand(inputParams.getFirst());
		var restParams = inputParams.subList(1, inputParams.size());

		return switch (command) {
			case PING -> ping();
			case ECHO -> echo(restParams);
		};
	}

	private static String convertToOutputString(List<RedisResultData> redisResultDataList) {
		var result = new StringBuilder();

		for (var redisResultData : redisResultDataList) {
			result.append(redisResultData.redisDataType().getFirstByte());
			result.append(redisResultData.data());
			result.append("\r\n");
		}

		return result.toString();
	}

	private static List<RedisResultData> ping() {
		return List.of(new RedisResultData(RedisDataType.SIMPLE_STRINGS, CommonConstant.REDIS_PONG_OUTPUT));
	}

	private static List<RedisResultData> echo(List<String> args) {
		if (args.size() != 1) {
			throw new RedisExecuteException("execute error - echo need exact 1 args");
		}

		var str = args.getFirst();
		var sizeData = new RedisResultData(RedisDataType.BULK_STRINGS, String.valueOf(str.length()));
		var strData = new RedisResultData(RedisDataType.EMPTY_TYPE, str);

		return List.of(sizeData, strData);
	}
}
