package redis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import common.SocketUtil;
import lombok.extern.slf4j.Slf4j;
import rdb.RdbUtil;

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
			var outputStr = RedisResultData.convertToOutputString(data);
			log.debug("output: {}", outputStr);

			SocketUtil.sendToSocket(writer, outputStr);
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
			case KEYS -> keys();
			case INFO -> info(restParams);
			case REPLCONF -> replconf(restParams);
			case PSYNC -> psync(restParams);
		};
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
			RedisRepository.expireWithExpireTime(key, expireTime.get());
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

	private static List<RedisResultData> keys() {
		var keys = RedisRepository.getKeys();
		return RedisResultData.getArrayData(keys.toArray(new String[0]));
	}

	private static List<RedisResultData> info(List<String> restParam) {
		if (!restParam.getFirst().equalsIgnoreCase("replication")) {
			return null;
		}

		var result = new StringBuilder("# Replication\n");

		for (var setting : RedisRepository.getAllReplicationSettings()) {
			result.append(setting.getKey());
			result.append(":");
			result.append(setting.getValue());
			result.append("\n");
		}

		return RedisResultData.getBulkStringData(result.toString());
	}

	private static List<RedisResultData> replconf(List<String> restParam) {
		// TODO: Will be used in further step.
		return RedisResultData.getSimpleResultData(RedisDataType.SIMPLE_STRINGS, "OK");
	}

	private static List<RedisResultData> psync(List<String> restParam) {
		var replId = RedisRepository.getReplicationSetting("master_replid");
		var offset = RedisRepository.getReplicationSetting("master_repl_offset");

		var result = RedisResultData.getSimpleResultData(RedisDataType.SIMPLE_STRINGS, "FULLRESYNC %s %s".formatted(replId, offset));

		if (restParam.getFirst().equalsIgnoreCase("?") && restParam.getLast().equalsIgnoreCase("-1")) {
			var sizeData = new RedisResultData(RedisDataType.BULK_STRINGS, String.valueOf(RdbUtil.EMPTY_RDB_HEX.length()));
			var strData = new RedisResultData(RedisDataType.EMPTY_TYPE_WITHOUT_TRAILING, RdbUtil.EMPTY_RDB_HEX);

			result = Stream.concat(result.stream(), Stream.of(sizeData, strData)).toList();
		}

		return result;
	}
}
