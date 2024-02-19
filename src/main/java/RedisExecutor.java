import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisExecutor {
	private RedisExecutor() {

	}

	public static void parseAndExecute(BufferedWriter writer, List<String> inputParams) {
		try {
			if (!checkSupported(inputParams.getFirst())) {
				returnCommonErrorMessage(writer, null);
				return;
			}

			var data = executeCommand(inputParams);
			var outputStr = convertToOutputString(data);

			log.debug("output: {}", outputStr);
			writer.write(outputStr);
			writer.flush();
		} catch (RuntimeException e) {
			log.warn("command execute error - inputParams: {}", inputParams, e);
			returnCommonErrorMessage(writer, null);
		} catch (IOException e) {
			log.error("IOException", e);
		}
	}

	public static void returnCommonErrorMessage(BufferedWriter writer, String detailErrorMessage) {
		try {
			if (detailErrorMessage != null) {
				writer.write("-ERR " + detailErrorMessage + "\r\n");
			} else {
				writer.write("-ERR\r\n");
			}
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
			case GET -> get(restParams);
			case SET -> set(restParams);
			case CONFIG -> config(restParams);
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
		return List.of(new RedisResultData(RedisDataType.SIMPLE_STRINGS, CommonConstant.REDIS_OUTPUT_PONG));
	}

	private static List<RedisResultData> echo(List<String> args) {
		if (args.size() != 1) {
			throw new RedisExecuteException("execute error - echo need exact 1 args");
		}

		return RedisResultData.getBulkStringData(args.getFirst());
	}

	private static List<RedisResultData> get(List<String> args) {
		if (args.size() != 1) {
			throw new RedisExecuteException("execute error - get need exact 1 args");
		}

		var key = args.getFirst();
		var findResult = RedisRepository.get(key);

		return RedisResultData.getBulkStringData(findResult);
	}

	private static List<RedisResultData> set(List<String> args) {
		if (args.size() < 2) {
			throw new RedisExecuteException("execute error - set need more than 2 args");
		}

		var key = args.getFirst();
		var value = args.get(1);
		var expireTime = new AtomicLong(-1L);

		// TODO: if need more options, extract separate method... maybe?
		if (args.size() >= 4) {
			if ("px".equalsIgnoreCase(args.get(2))) {
				var milliseconds = Long.parseLong(args.get(3));
				expireTime.set(milliseconds);
			}
		}

		RedisRepository.set(key, value);
		if (expireTime.get() > 0L) {
			new Thread(() -> {
				try {
					Thread.sleep(expireTime.get());
					RedisRepository.expire(key);
				} catch (Exception e) {
					log.error("expire failed.", e);
				}
			}).start();
		}

		return RedisResultData.getSimpleResultData(RedisDataType.SIMPLE_STRINGS, CommonConstant.REDIS_OUTPUT_OK);
	}

	private static List<RedisResultData> config(List<String> args) {
		if (args.size() != 2) {
			throw new RedisExecuteException("execute error - config need exact 2 params");
		}

		if (CommonConstant.REDIS_COMMAND_PARAM_GET.equalsIgnoreCase(args.getFirst())) {
			var key = args.get(1);
			var value = RedisRepository.configGet(key);
			return RedisResultData.getArrayData(key, value);
		} else {
			throw new RedisExecuteException("execute error - not supported option");
		}
	}
}
