import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
	public static void main(String[] args) throws IOException {
		log.info("Logs from your program will appear here!");

		var serverSocket = RedisConnectionUtil.createRedisServerSocket(CommonConstant.REDIS_PORT);
		while (true) {
			var clientSocket = serverSocket.accept();
			var redisClientThread = new RedisConnectionThread(clientSocket);
			redisClientThread.start();
		}
	}
}
