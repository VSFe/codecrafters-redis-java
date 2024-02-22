package replication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import redis.RedisCommand;
import redis.RedisRepository;

@Slf4j
public class SlaveConnectionProvider {
	private Socket clientSocket;
	private BufferedReader reader;
	private BufferedWriter writer;

	public void init(String masterHost, int masterPort) {
		try {
			this.clientSocket = new Socket(masterHost, masterPort);
			this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			handShake();
		} catch (Exception e) {

		}
	}

	public void handShake() {
		ping();
		replconf();
	}

	private void ping() {
		CommandSender.sendCommand(reader, writer, RedisCommand.PING, List.of());
	}

	private void replconf() {
		CommandSender.sendCommand(reader, writer, RedisCommand.REPLCONF, List.of("listening-port", RedisRepository.configGet("port")));
		CommandSender.sendCommand(reader, writer, RedisCommand.REPLCONF, List.of("capa", "psync2"));
	}
}
