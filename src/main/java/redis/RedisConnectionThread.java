package redis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RedisConnectionThread extends Thread {
	private final Socket socket;

	@Override
	public void run() {
		try (
			var inputStream = socket.getInputStream();
			var outputStream = socket.getOutputStream();
			var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			var bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
		) {
			List<String> inputParams;
			while ((inputParams = parseInput(bufferedReader)) != null) {
				log.debug("inputParams: {}", inputParams);
				RedisExecutor.parseAndExecute(bufferedWriter, inputParams);
			}
		} catch (IOException e) {
			log.error("create I/O stream error.", e);
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				log.error("close socket error.", e);
			}
		}
	}

	/**
	 * parse input
	 * Redis input given in "array of bulk string" format.
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private List<String> parseInput(BufferedReader reader) throws IOException {
		try {
			var sizeStr = reader.readLine();
			var inputList = new ArrayList<String>();

			if (sizeStr == null) {
				return null;
			}

			int size = Integer.parseInt(sizeStr.substring(1));
			for (int i = 0; i < size; i++) {
				var paramSizeStr = reader.readLine();
				var param = reader.readLine();
				inputList.add(param);
			}

			return inputList;
		} catch (Exception e) {
			log.warn("parse input failed.", e);
			return null;
		}
	}
}
