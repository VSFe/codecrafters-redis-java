import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import rdb.RdbBuilder;
import rdb.RdbUtil;
import redis.CommonConstant;
import redis.RedisConnectionThread;
import redis.RedisConnectionUtil;
import redis.RedisRepository;

@Slf4j
public class Main {
	public static void main(String[] args) throws IOException {
		var portNumber = init(args);
		log.info("Logs from your program will appear here!");

		var serverSocket = RedisConnectionUtil.createRedisServerSocket(portNumber);
		while (true) {
			var clientSocket = serverSocket.accept();
			var redisClientThread = new RedisConnectionThread(clientSocket);
			redisClientThread.start();
		}
	}

	public static int init(String[] args) {
		parseConfig(args);

		var dir = RedisRepository.configGet("dir");
		var dbFileName = RedisRepository.configGet("dbfilename");
		var port = RedisRepository.configGet("port");

		if (dir != null && dbFileName != null) {
			try {
				var data = RdbUtil.openRdbFile(dir, dbFileName);
				var builder = new RdbBuilder().bytes(data);
				var rdb = builder.build();

				log.info("rdb: {}", rdb);
				if (rdb != null) {
					rdb.init();
				}
			} catch (Exception e) {
				log.info("RDB Read Failed. init without RDB file.", e);
			}
		}

		if (port != null) {
			try {
				var portNumber = Integer.parseInt(port);

				return portNumber;
			} catch (Exception e) {
				log.info("Setting Custom Port Number Failed. use default port ({})", CommonConstant.DEFAULT_REDIS_PORT);
			}
		}

		return CommonConstant.DEFAULT_REDIS_PORT;
	}

	public static void parseConfig(String[] args) {
		for (int i = 0; i < args.length; i += 2) {
			var key = args[i].substring(2);
			var value = args[i + 1];

			RedisRepository.configSet(key, value);
		}
	}
}
