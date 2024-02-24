package replication;

import java.io.BufferedWriter;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MasterConnectionHolder {
	private static final Map<ReplicationConnectionInfo, Integer> WAITING_LIST = new HashMap<>();
	private static final List<MasterConnectionProvider> MASTER_CONNECTION_PROVIDERS = new ArrayList<>();

	public static void propagateCommand(List<String> inputParams) {
		MASTER_CONNECTION_PROVIDERS.forEach(provider -> provider.sendMessage(inputParams));
	}

	public static void createNewWaitingConnection(String ipAddress, int connectionPort, int slavePort) {
		WAITING_LIST.put(new ReplicationConnectionInfo(ipAddress, connectionPort), slavePort);
	}

	public static MasterConnectionProvider findProvider(Socket socket) {
		return MASTER_CONNECTION_PROVIDERS.stream()
			.filter(masterConnectionProvider -> masterConnectionProvider.getSocket().getInetAddress().equals(socket.getInetAddress()))
			.findFirst()
			.orElse(null);
	}

	public static void addConnectedList(Socket socket, BufferedWriter writer) {
		MASTER_CONNECTION_PROVIDERS.add(new MasterConnectionProvider(socket, writer));
	}

	public static int getFullySyncedReplicaCount(int desireCount, long limitTime) {
		var start = Instant.now();
		var result = 0;

		while (Duration.between(start, Instant.now()).toMillis() < limitTime) {
			MASTER_CONNECTION_PROVIDERS.stream()
				.filter(masterConnectionProvider -> !masterConnectionProvider.isAckRequested())
				.forEach(MasterConnectionProvider::sendAck);
			result = (int)MASTER_CONNECTION_PROVIDERS.stream().map(MasterConnectionProvider::isFullySynced)
				.count();

			if (result >= desireCount) {
				return result;
			}
		}

		return result;
	}
}
