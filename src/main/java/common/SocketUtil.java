package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketUtil {
	private SocketUtil() {

	}

	public static void sendStringToSocket(BufferedWriter writer, String outputStr) throws IOException {
		if (writer == null) {
			return;
		}
		writer.write(outputStr);
		writer.flush();
	}

	public static void sendBytesToSocket(OutputStream outputStream, byte[] bytes) throws IOException {
		outputStream.write(bytes);
		outputStream.flush();
	}

	public static List<String> parseSocketInputToRedisCommand(BufferedReader reader) {
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
		} catch (RuntimeException e) {
			return List.of();
		} catch (Exception e) {
			log.warn("I/O error", e);
			return null;
		}
	}
}
