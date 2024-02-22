package common;

import java.io.BufferedWriter;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketUtil {
	private SocketUtil() {

	}

	public static void sendToSocket(BufferedWriter writer, String outputStr) throws IOException {
		writer.write(outputStr);
		writer.flush();
	}
}
