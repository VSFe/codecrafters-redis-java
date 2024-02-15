import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	public static void main(String[] args) {
		// You can use print statements as follows for debugging, they'll be visible when running tests.
		System.out.println("Logs from your program will appear here!");

		Socket clientSocket = null;
		try (var serverSocket = new ServerSocket(CommonInstant.REDIS_PORT)) {
			serverSocket.setReuseAddress(true);
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			System.out.println(String.format("IOException: %s", e.getMessage()));
		}
	}
}
