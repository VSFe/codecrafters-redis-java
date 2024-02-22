package replication;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import common.SocketUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import redis.RedisResultData;

@Slf4j
public class SlaveConnectionProvider {
	private Socket clientSocket;

	public void init(String masterHost, int masterPort) {
		try {
			this.clientSocket = new Socket(masterHost, masterPort);
			handShake();
		} catch (Exception e) {

		}
	}

	public void handShake() {
		ping();
	}

	private void ping() {
		try {
			var pingStr = "ping";
			var message = RedisResultData.getArrayData(pingStr);

			var outputStream = clientSocket.getOutputStream();
			var writer = new BufferedWriter(new OutputStreamWriter(outputStream));

			SocketUtil.sendToSocket(writer, RedisResultData.convertToOutputString(message));
		} catch (IOException e) {
			log.error("IOException", e);
		}
	}
}
