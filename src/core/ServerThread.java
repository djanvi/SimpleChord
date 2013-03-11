package core;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.StringTokenizer;


public class ServerThread extends Thread
{
	int id;
	short tcpport;
	int remoteport;
	String cip;
	String chostname;

	ServerThread (short tcpport)
	{
		this.tcpport= tcpport;
	}
	public void run()
	{
		@SuppressWarnings("unused")
		int id=0;
		GlobalVar.myconnectionPort=this.tcpport;
		ServerSocket tcpSocket=null;		
		try
		{
			tcpSocket=new ServerSocket(tcpport);
		}
		catch(Exception e)
		{

			e.printStackTrace();
		}

		Random numGenerator= new Random();
		
		while(true)
		{
			try
			{	
				Socket incomingSocket=null;
				incomingSocket=tcpSocket.accept();
				id ++;
				System.out.println("Connection accepted");
				
				
				ConnectionFormat entry = new ConnectionFormat();
				entry.hostname = incomingSocket.getInetAddress().getCanonicalHostName().toString();
				entry.ipAddress=incomingSocket.getInetAddress().toString();				
				entry.localPort= incomingSocket.getLocalPort();
				entry.remotePort=incomingSocket.getPort();
				
				entry.inStream = new ObjectInputStream(incomingSocket.getInputStream());
				entry.outStream = new ObjectOutputStream(incomingSocket.getOutputStream());
				entry.socket=incomingSocket;
				int connID = numGenerator.nextInt();
				//GlobalVar.inConn.put(connID, entry);
				
				
				//read client's id
				WrapThis obj=null;
				
				try {
					obj = (WrapThis) entry.inStream.readObject();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				String incomingIdentifier_Port;
				incomingIdentifier_Port = obj.element;
				System.out.println("incomingIdentifier_Port"+incomingIdentifier_Port);
				StringTokenizer st=new StringTokenizer(incomingIdentifier_Port);
				String iden=st.nextToken("|");
				System.out.println("iden"+iden);
				String port=st.nextToken("|");
				System.out.println("port"+port);
				//String [] iden_port=incomingIdentifier_Port.split("|");
				
				entry.nodeId=Integer.parseInt(iden);
				int incomingId=entry.nodeId;
				entry.chordPort=Integer.parseInt(port);
				
			
				GlobalVar.inConn.put(connID, entry);
				System.out.println("REad my client's id:"+entry.nodeId);
				//GlobalVar.mySuccesorNode=Integer.parseInt(incomingId);
				
				System.out.println("Connection established with host IP: "+incomingSocket.getInetAddress().getCanonicalHostName().toString()+"and id: "+incomingId);
				//sending my id to client
		    	WrapThis start = new WrapThis();
				start.element = Integer.toString(GlobalVar.myIdentifier);
				try {
					GlobalVar.inConn.get(connID).outStream.writeObject(start);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				//***************************
				//code shifted from Worker
				//***************************
				boolean foundNewSuccessor=false;
				boolean conveyedToOldSuccessor=false;
				int incomingID=incomingId;
				
				
				
				
				
				if (GlobalVar.myIdentifier == GlobalVar.mySuccesorNode
						&& GlobalVar.myIdentifier == GlobalVar.myPredecessorNode) 
				{
					System.out.println("inside condition     ");
					SuccessorInfo successor = new SuccessorInfo();
					successor.nodeID = GlobalVar.inConn.get(connID).nodeId;
					successor.ipAddress = GlobalVar.inConn.get(connID).ipAddress.replace("/", "");
					successor.port = GlobalVar.inConn.get(connID).chordPort;
					int newSuccessorConnectionID = numGenerator.nextInt();
					System.out.println("Connecting to id: " + successor.nodeID);
					
					Operations.connect(successor.ipAddress + ":"	+ successor.port, newSuccessorConnectionID);
					
					GlobalVar.myPredecessorNode=successor.nodeID;
					System.out.println("My Predecessor:"+GlobalVar.myPredecessorNode);
				}
				SuccessorInfo newSuccessor = new SuccessorInfo();
				
				newSuccessor.nodeID = GlobalVar.inConn.get(connID).nodeId;
				newSuccessor.ipAddress = GlobalVar.inConn.get(connID).ipAddress.replace("/", "");
				newSuccessor.port = GlobalVar.inConn.get(connID).chordPort;
				
				
				//int newSuccessorConnectionID = numGenerator.nextInt();
				//System.out.println("Connecting to id: "+newSuccessor.nodeID);
				//where should the incoming id lie.. create ring
				
				if (incomingID < GlobalVar.myIdentifier&&foundNewSuccessor==false)
				{
					GlobalVar.mySuccesorNode=incomingID;
					System.out.println("My succesor:"+ GlobalVar.mySuccesorNode);
					
					
					String populateFinger1=GlobalVar.myIdentifier+"|find|"+GlobalVar.fingerTable.get(0).start+"|";
					String populateFinger2=GlobalVar.myIdentifier+"|find|"+GlobalVar.fingerTable.get(1).start+"|";
					String populateFinger3=GlobalVar.myIdentifier+"|find|"+GlobalVar.fingerTable.get(2).start+"|";
					
					try {
						GlobalVar.inConn.get(connID).outStream.writeObject(populateFinger1);
						GlobalVar.inConn.get(connID).outStream.flush();
						
						GlobalVar.inConn.get(connID).outStream.writeObject(populateFinger2);
						GlobalVar.inConn.get(connID).outStream.flush();
						
						GlobalVar.inConn.get(connID).outStream.writeObject(populateFinger3);
						GlobalVar.inConn.get(connID).outStream.flush();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					foundNewSuccessor=true;
				}
				
				
				if (incomingID > GlobalVar.myIdentifier&&foundNewSuccessor==false) //&&incomingID<GlobalVar.mySuccesorNode
				{

						//System.out.println("inside reduntatn if");
						//random number==connIDOldSuccessor
						
					if(conveyedToOldSuccessor==false)	
						{
						for (int connIDOldSuccessor : GlobalVar.inConn.keySet()) {

							ConnectionFormat object = GlobalVar.inConn.get(connIDOldSuccessor);

							if (object.nodeId == GlobalVar.mySuccesorNode) {
								//disconnect with this connID
								//Predecessor info
								//telling about new successor to old one
								System.out.println("telling abnout neew success to old one");
								object.outStream.writeObject("nowConnectToThis|"+newSuccessor.nodeID+"|"+newSuccessor.ipAddress+"|"+newSuccessor.port+"|");
								Thread.sleep(6000);
								GlobalVar.inConn.remove(connID);
								
								conveyedToOldSuccessor=true;	
								//  object.socket.close();
								//change my new successor
								GlobalVar.mySuccesorNode = incomingID;
								System.out.println("2.My Successor:"+GlobalVar.mySuccesorNode);
								//object.socket.close();
								//throw new  SocketException();
							
								//remove entry from list- to do 
							}
						}
						}
					
					if(conveyedToOldSuccessor==false)	
					{
						for (int cID : GlobalVar.outConn.keySet()) {
							ConnectionFormat object = GlobalVar.outConn.get(cID);
							if (object.nodeId == GlobalVar.mySuccesorNode) {
								//disconnect with this connID
								//Predecessor info
								SuccessorInfo successor = new SuccessorInfo();
								successor.nodeID = GlobalVar.outConn.get(connID).nodeId;
								successor.ipAddress = GlobalVar.outConn.get(connID).ipAddress;
								successor.port = GlobalVar.outConn.get(connID).localPort;

								GlobalVar.outConn.get(cID).outStream.writeObject(successor);
                                 Thread.sleep(6000);
								GlobalVar.inConn.remove(connID);
								//object.socket.close();

								//change my new successor
								GlobalVar.mySuccesorNode = incomingID;
								System.out.println("My Successor:"+GlobalVar.mySuccesorNode);
								conveyedToOldSuccessor=true;
								//remove entry from list to do
							}

						}
					}
					if(conveyedToOldSuccessor==false)	
					{
						
						GlobalVar.mySuccesorNode = incomingID;
						System.out.println("GlobalVar.mySuccesorNode "+GlobalVar.mySuccesorNode);
						
						String populateFinger1=GlobalVar.myIdentifier+"|find|"+GlobalVar.fingerTable.get(0).start+"|";
						String populateFinger2=GlobalVar.myIdentifier+"|find|"+GlobalVar.fingerTable.get(1).start+"|";
						String populateFinger3=GlobalVar.myIdentifier+"|find|"+GlobalVar.fingerTable.get(2).start+"|";
						
						try {
							GlobalVar.inConn.get(connID).outStream.writeObject(populateFinger1);
							GlobalVar.inConn.get(connID).outStream.flush();
							
							GlobalVar.inConn.get(connID).outStream.writeObject(populateFinger2);
							GlobalVar.inConn.get(connID).outStream.flush();
							
							GlobalVar.inConn.get(connID).outStream.writeObject(populateFinger3);
							GlobalVar.inConn.get(connID).outStream.flush();
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
						foundNewSuccessor=true;
						conveyedToOldSuccessor=true;
					}
//						SuccessorInfo successor = new SuccessorInfo();
//						successor.nodeID = GlobalVar.myIdentifier;
//						successor.ipAddress = "";
//						successor.port = GlobalVar.myconnectionPort;
//
//						try {
//							GlobalVar.inConn.get(connID).outStream
//							.writeObject(successor);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						// now change my successor				
//						GlobalVar.mySuccesorNode = incomingID;
					}

					//on this same connection do writeObject-mysuccessor's id, ipadd, port
				
				
				
				//code shifted from worker ends here
				
				
				
				
				WorkerThread workerThread =new WorkerThread(connID,incomingId+"");
				workerThread.start();
			
			}
			catch(SocketException se)
			{
				//GlobalVar.inConn.remove();
			}
			
			catch(Exception e){}
		}
	}
}