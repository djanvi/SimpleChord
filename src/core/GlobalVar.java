package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalVar {

	
	static short myconnectionPort;
	static int myIdentifier;
	static int myPredecessorNode;
	static int mySuccesorNode;

	
	// Main Data Structure used for saving state of connections in Simpella
		static HashMap<Integer, ConnectionFormat> outConn = new HashMap<Integer, ConnectionFormat>();
		static HashMap<Integer, ConnectionFormat> inConn = new HashMap<Integer, ConnectionFormat>();
		
				
		//FingerTable
		static List<FingerInfo> fingerTable = new ArrayList<FingerInfo>();
		static List<String> nodeInfo = new ArrayList<String>();
		
		
		//keysset;
		
		
	
	
	
	
	
	
	
	
	
	
	
	
}
