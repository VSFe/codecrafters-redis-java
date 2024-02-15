import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;

public class Main {
	public static void main(String[] args) throws IOException {
		// You can use print statements as follows for debugging, they'll be visible when running tests.
		System.out.println("Logs from your program will appear here!");

		var serverSocket = new ServerSocket(CommonConstant.REDIS_PORT);
		serverSocket.setReuseAddress(true);
		while (true) {
			try (var clientSocket = serverSocket.accept()) {
				sendResponse(clientSocket.getOutputStream(), CommonConstant.REDIS_PONG_MESSAGE);
			} catch (IOException e) {
				System.out.printf("IOException: %s%n", e.getMessage());
			}
		}
	}

	private static void sendResponse(OutputStream outputStream, String response) {
		var writer = new PrintWriter(outputStream, true);
		writer.printf(response);
	}
}
