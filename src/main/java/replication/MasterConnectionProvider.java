package replication;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import common.SocketUtil;
import lombok.extern.slf4j.Slf4j;
import redis.RedisResultData;

@Slf4j
public class MasterConnectionProvider {
	private Socket socket;
	private BufferedWriter writer;

	public MasterConnectionProvider(BufferedWriter writer) {
		this.writer = writer;
	}

	public void init(String slaveHost, int port) {
		try {
			this.socket = new Socket(slaveHost, port);
			this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			log.error("IOException", e);
		}
	}

	public void sendMessage(List<String> param) {
		try {
			var arrayData = RedisResultData.getArrayData(param.toArray(new String[0]));
			var message = RedisResultData.convertToOutputString(arrayData);

			SocketUtil.sendStringToSocket(writer, message);
		} catch (IOException e) {
			log.error("IOException", e);
		}
	}
}
