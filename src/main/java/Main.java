import java.io.IOException;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
	public static void main(String[] args) throws IOException {
		log.info("Logs from your program will appear here!");
		parseConfig(args);

		var serverSocket = RedisConnectionUtil.createRedisServerSocket(CommonConstant.REDIS_PORT);
		while (true) {
			var clientSocket = serverSocket.accept();
			var redisClientThread = new RedisConnectionThread(clientSocket);
			redisClientThread.start();
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
