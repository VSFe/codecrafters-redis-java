package replication;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterConnectionHolder {
	private static final Map<ReplicationConnectionInfo, Integer> WAITING_LIST = new HashMap<>();
	private static final List<MasterConnectionProvider> MASTER_CONNECTION_PROVIDERS = new ArrayList<>();

	public static void propagateCommand(List<String> inputParams) {
		MASTER_CONNECTION_PROVIDERS.forEach(provider -> provider.sendMessage(inputParams));
	}

	public static void createNewWaitingConnection(String ipAddress, int connectionPort, int slavePort) {
		WAITING_LIST.put(new ReplicationConnectionInfo(ipAddress, connectionPort), slavePort);
	}

	public static void addConnectedList(BufferedWriter writer) {
		MASTER_CONNECTION_PROVIDERS.add(new MasterConnectionProvider(writer));
	}
}
