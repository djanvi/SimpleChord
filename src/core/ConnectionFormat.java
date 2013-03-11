package core;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionFormat {
	String ipAddress;
	String hostname;
	int nodeId;
	int localPort , remotePort, chordPort;
	Socket socket;
	ObjectOutputStream outStream;
	ObjectInputStream inStream;
}
