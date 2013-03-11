package core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Operations {

	public static void connect(String hostname, int connID) //throws NumberFormatException, UnknownHostException, IOException, ClassNotFoundException
	{


		String[] serventSplit=hostname.split(":");
		String destIP=serventSplit[0];			//to do for host name
		String destPort=serventSplit[1];

		try {
			System.out.println("Indde open");
			Socket outgoingSocket=new Socket(InetAddress.getByName(destIP),Integer.parseInt(destPort));
			ConnectionFormat entry = new ConnectionFormat();

			entry.hostname = InetAddress.getByName(destIP).getCanonicalHostName().toString();
			entry.ipAddress=InetAddress.getByName(destIP).toString().replace("/", "");				
			entry.localPort= outgoingSocket.getLocalPort();
			entry.remotePort=outgoingSocket.getPort();
			entry.outStream = new ObjectOutputStream(outgoingSocket.getOutputStream());
			entry.inStream = new ObjectInputStream(outgoingSocket.getInputStream());
			entry.socket=outgoingSocket;

			//GlobalVar.outConn.put(connID, entry);

			
			//sending my id to server
			WrapThis start = new WrapThis();
			start.element = GlobalVar.myIdentifier+"|"+GlobalVar.myconnectionPort+"|";
			try {
				entry.outStream.writeObject(start);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			//reading server's id
			WrapThis obj=null;
			try {
				obj = (WrapThis) entry.inStream.readObject();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String serverId;
			serverId = obj.element;
			
			
			//puting this id in conectioninfo
			entry.nodeId=Integer.parseInt(serverId);
			
			System.out.println("connection established with node id: "+entry.nodeId);
			GlobalVar.outConn.put(connID, entry);
			int nodeIDforPrint=entry.nodeId;
			GlobalVar.myPredecessorNode=Integer.parseInt(serverId);
			
			
			
			
			System.out.println("1.Predecessor : "+GlobalVar.myPredecessorNode);
			//savwe my predecessor
			ClientThread clientThread =new ClientThread(connID);
			clientThread.start();
			System.out.println("Client Thread with ID:"+ nodeIDforPrint+" has been started");

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			/* A separate thread for further processing of this connection*/			
			
		}
		
	public static void updateFingerTable(List<FingerInfo> predecessorfingerTable)
	{
		
		//GlobalVar.fingerTable.get(0).node=Operations.findSuccessor(predecessorfingerTable);
		
		
		//update my finger table
		//chord node.succesor= add success
		
		//ChordNode me = new ChordNode();
		//update all info here too
		
		
		//connect to successor if not connected
		
		
		
		
	}
	
	public static void initializeFingerTable( String myIdentifier, String myIP)
	{
		GlobalVar.nodeInfo.add(myIdentifier);
		GlobalVar.nodeInfo.add(myIP);
		GlobalVar.nodeInfo.add(Integer.toString(GlobalVar.myconnectionPort));
		
		GlobalVar.myIdentifier=Integer.parseInt(myIdentifier);
		GlobalVar.myPredecessorNode=GlobalVar.myIdentifier;
		GlobalVar.mySuccesorNode=GlobalVar.myIdentifier;
		
		FingerInfo[] finger = new FingerInfo[3];
		finger[0] = new FingerInfo();
		finger[1] = new FingerInfo();
		finger[2] = new FingerInfo();
		
		finger[0].start= ((GlobalVar.myIdentifier +1)%8);
		finger[1].start=((GlobalVar.myIdentifier + 2)%8);
		finger[2].start=((GlobalVar.myIdentifier + 4)%8);
		
		finger[0].interval="["+Integer.toString(finger[0].start)+","+Integer.toString(finger[1].start)+")";
		finger[1].interval="["+Integer.toString(finger[1].start)+","+Integer.toString(finger[2].start)+")";
		finger[2].interval="["+Integer.toString(finger[2].start)+","+GlobalVar.myIdentifier+")";
	
		finger[0].node=GlobalVar.myIdentifier;
		finger[1].node=GlobalVar.myIdentifier;
		finger[2].node=GlobalVar.myIdentifier;
		
		GlobalVar.fingerTable.add(finger[0]);
		GlobalVar.fingerTable.add(finger[1]);
		GlobalVar.fingerTable.add(finger[2]);

		
		
		
		
		
		//GlobalVar.fingerTable
		//initialize this table
		//chord node.succesor= add success
		//update all info here too
		
		
		//connect to successor if not connected
		
	}
	

	public static void showMyInfo()
	{
		System.out.println("Node id:"+GlobalVar.myIdentifier);
		System.out.println("Successor:"+GlobalVar.mySuccesorNode);
		System.out.println("Predecessor:"+GlobalVar.myPredecessorNode);
		System.out.println("start       |    interval   |   succ node");
		for(int index=0;index<3;index++)
		{
			System.out.println(GlobalVar.fingerTable.get(index).start+"     "+GlobalVar.fingerTable.get(index).interval+"   "+GlobalVar.fingerTable.get(index).node);
		}
		
	}
	
	public static int findSuccessor(List<FingerInfo> fingerTable, int id)
	{
		
		
		return 0;
		
	}
	
	public static void internalConnect(InetAddress successorIP, int successorPort, int connID) throws IOException, ClassNotFoundException
	{

		Socket outgoingSocket= new Socket(successorIP,successorPort);
		
		ConnectionFormat entry = new ConnectionFormat();

		entry.hostname = successorIP.getCanonicalHostName().toString();
		entry.ipAddress=successorIP.toString();				
		entry.localPort= outgoingSocket.getLocalPort();
		entry.remotePort=outgoingSocket.getPort();
		entry.outStream = new ObjectOutputStream(outgoingSocket.getOutputStream());
		entry.inStream = new ObjectInputStream(outgoingSocket.getInputStream());
		entry.socket=outgoingSocket;

		GlobalVar.outConn.put(connID, entry);
		//sending my id to server
				WrapThis start = new WrapThis();
				start.element = Integer.toString(GlobalVar.myIdentifier);
				GlobalVar.outConn.get(connID).outStream.writeObject(start);
				
				
				//reading server's id
				WrapThis obj;
				obj = (WrapThis) GlobalVar.outConn.get(connID).inStream.readObject();
				String serverId;
				serverId = obj.element;
				
				//puting this id in conectioninfo
				entry.nodeId=Integer.parseInt(serverId);
				GlobalVar.outConn.put(connID, entry);
		
		
		
		
	}
	
	
	
	
	
	
	

	}
	
	
