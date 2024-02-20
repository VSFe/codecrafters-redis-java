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
		init(args);
		log.info("Logs from your program will appear here!");

		var serverSocket = RedisConnectionUtil.createRedisServerSocket(CommonConstant.REDIS_PORT);
		while (true) {
			var clientSocket = serverSocket.accept();
			var redisClientThread = new RedisConnectionThread(clientSocket);
			redisClientThread.start();
		}
	}

	public static void init(String[] args) throws IOException {
		parseConfig(args);

		var dir = RedisRepository.configGet("dir");
		var dbFileName = RedisRepository.configGet("dbfilename");

		if (dir != null && dbFileName != null) {
			var data = RdbUtil.openRdbFile(dir, dbFileName);
			var builder = new RdbBuilder().bytes(data);
			var rdb = builder.build();

			log.info("rdb: {}", rdb);
			if (rdb != null) {
				rdb.init();
			}
		}
	}

	public static void parseConfig(String[] args) {
		for (int i = 0; i < args.length; i += 2) {
			var key = args[i].substring(2);
			var value = args[i + 1];

			RedisRepository.configSet(key, value);
		}
	}
}
