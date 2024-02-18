import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

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
			String input;
			while ((input = bufferedReader.readLine()) != null) {
				// TODO: will extracted from another method.
				log.debug("client input: {}", input);
				if (CommonConstant.REDIS_PING_INPUT.equalsIgnoreCase(input)) {
					bufferedWriter.write(CommonConstant.REDIS_PONG_OUTPUT);
					bufferedWriter.flush();
				}
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
}
