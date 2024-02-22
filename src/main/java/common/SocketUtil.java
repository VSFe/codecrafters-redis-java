package common;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketUtil {
	private SocketUtil() {

	}

	public static void sendStringToSocket(BufferedWriter writer, String outputStr) throws IOException {
		writer.write(outputStr);
		writer.flush();
	}

	public static void sendBytesToSocket(OutputStream outputStream, byte[] bytes) throws IOException {
		outputStream.write(bytes);
		outputStream.flush();
	}
}
