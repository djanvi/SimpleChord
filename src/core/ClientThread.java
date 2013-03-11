package core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;

public class ClientThread extends Thread{

	
	int connID;
	Random numGenerator= new Random();
  StringTokenizer st2=null;
	ClientThread(int connID)
	{
		this.connID = connID;
	}
	
	public void run()
	{
		
		 try {
		  while(true)
		  {
			//read from my predecessor, he is telling me my new predecessor
			    boolean alreadyconnected=false;
			    
			    
			    String fromMyPredecessor=null;
				try {
					fromMyPredecessor = GlobalVar.outConn.get(connID).inStream.readObject().toString();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    StringTokenizer st=new StringTokenizer(fromMyPredecessor);
			    String commandHeader=st.nextToken("|");
			    if(commandHeader.equals("nowConnectToThis")==true)
			    {
			    	//conect to new predesoosr
			    	//newSuccessor.nodeID+"|"+newSuccessor.ipAddress+"|"+newSuccessor.port+
			    	System.out.println("Got message from old prede");
			    	
			    	//GlobalVar.outConn.
			    	String nodeID=st.nextToken("|");
			    	String ipAddress=st.nextToken("|");
			    	String port=st.nextToken("|");
			    	
			    	int newSuccessorConnectionID = numGenerator.nextInt();
			    	
			    	Operations.connect(ipAddress + ":"	+ port, newSuccessorConnectionID);
			    	
			    	//GlobalVar.outConn.get(connID).socket.close();
			    	throw new SocketException();
			    	
			    	
			    }
			    else if(fromMyPredecessor.contains("find")==true)
			    {
			    	
			    	StringTokenizer st1=new StringTokenizer(fromMyPredecessor);
			    	String senderIdentifier=st1.nextToken("|");
			    	String cmd=st1.nextToken("|");
			    	String entryToBeChecked=st1.nextToken("|");
			    	
			    	int fingerEntry=Integer.parseInt(entryToBeChecked);
			    	
			    	System.out.println("Got find query to find: "+fingerEntry+"from:"+GlobalVar.outConn.get(connID).nodeId);
			    	
			    	if(Integer.parseInt(senderIdentifier)==GlobalVar.myIdentifier)
			    	{
			    		//i am his answer
			    		
			    		System.out.println("this find it mine only...");
			    		for(int i=0;i<GlobalVar.fingerTable.size();i++)
			    		{
			    			FingerInfo f=new FingerInfo();
			    			f=GlobalVar.fingerTable.get(i);
			    			
			    			int int_entryToBeChecked=Integer.parseInt(entryToBeChecked);
			    			if(f.start==int_entryToBeChecked)
			    			{
			    				
			    				
			    				
			    				if(GlobalVar.mySuccesorNode<=GlobalVar.myIdentifier)
			    					{
			    						f.node=GlobalVar.mySuccesorNode;
			    					}
			    				else
			    				{
			    					f.node=GlobalVar.myIdentifier;
			    				}
			    				GlobalVar.fingerTable.set(i, f);
			    			}
			    		}
			    		
			    		
			    	}			    	
			    	else
			    	{
			    	
			    	
			    		if(fingerEntry<=GlobalVar.myIdentifier)
			    		{
			    			String resposeOfFind="response|"+fromMyPredecessor+GlobalVar.myIdentifier+"|";

			    			GlobalVar.outConn.get(connID).outStream.writeObject(resposeOfFind);
			    			GlobalVar.outConn.get(connID).outStream.flush();
			    			System.out.println("Sent response of find to my predecessor:");
			    		}
			    		else if(fingerEntry>GlobalVar.myIdentifier)
			    		{	    		
			    			System.out.println("inside fingerEntry myIdentifier and my  GlobalVar.mySuccesorNode: "+GlobalVar.mySuccesorNode);
			    			for (int cID : GlobalVar.inConn.keySet()) 
			    			{

			    				ConnectionFormat object = GlobalVar.inConn.get(cID);
			    				if (object.nodeId == GlobalVar.mySuccesorNode) 
			    				{
			    					object.outStream.writeObject(fromMyPredecessor);
			    					object.outStream.flush();			    					
			    				}
			    			}			    		
			    		}
			    
			    	}
			     }
			    else if (fromMyPredecessor.contains("response")==true)
			    {
			    	
			    	StringTokenizer st1=new StringTokenizer(fromMyPredecessor);
			    	String response=st1.nextToken("|");
			    	String senderIdentifier=st1.nextToken("|");
			    	String cmd=st1.nextToken("|");
			    	String entryToBeChecked=st1.nextToken("|");
			    	String answer=st1.nextToken("|");
			    	if(Integer.parseInt(senderIdentifier)==GlobalVar.myIdentifier)
			    	{
			    		for(int i=0;i<GlobalVar.fingerTable.size();i++)
			    		{
			    			FingerInfo f=new FingerInfo();
			    			f=GlobalVar.fingerTable.get(i);
			    			int int_entryToBeChecked=Integer.parseInt(entryToBeChecked);
			    			if(f.start==int_entryToBeChecked)
			    			{
			    				f.node=Integer.parseInt(answer);
			    				GlobalVar.fingerTable.set(i, f);
			    			}
			    		}		    		
			    	}
			    	else//not intended for me
			    	{
			    		for (int cID : GlobalVar.outConn.keySet()) 
			    		{				
			    			ConnectionFormat object = GlobalVar.outConn.get(cID);
							if (object.nodeId == GlobalVar.myPredecessorNode) 
							{
								object.outStream.writeObject(fromMyPredecessor);
								object.outStream.flush();
			    			}
			    		}		
			    		
			    	}
			    }
				//SuccessorInfo successor = (SuccessorInfo) GlobalVar.outConn.get(connID).inStream.readObject();
				
				//check if i am connected to my this successor, if not connect to it.
				//check to do
				//GlobalVar.mySuccesorNode=successor.nodeID;
				
//				GlobalVar.myPredecessorNode=successor.nodeID;
//				System.out.println("My Predecessor:"+GlobalVar.myPredecessorNode);
				
//				for( int each: GlobalVar.outConn.keySet())
//				{
//					ConnectionFormat object =GlobalVar.outConn.get(each);
//					if(object.nodeId==successor.nodeID)
//					{
//						alreadyconnected= true;
//					}
//					
//				}
//				
//				
//				if(alreadyconnected!=true)
//				{
//			
//				InetAddress newPredecessorIP =InetAddress.getByName(successor.ipAddress);
//				int newPredecessorPort =  successor.port;
//				
//				int newconnID = numGenerator.nextInt();
				//Operations.internalConnect(newPredecessorIP,newPredecessorPort,newconnID);
			
			 //List<FingerInfo> predecessorFingerTable = (List) GlobalVar.outConn.get(connID).inStream.readObject();
	
		 //read finger table of my predecessor and call update finger table
		//	Operations.updateFingerTable(predecessorFingerTable); 
			
			
			 
			 
		 
		 //call update finger table by passing the received info from predecessor
		 
		 
		 //complete the ring i.e connect to successor
		 
		 
		 
		//	GlobalVar.outConn.get(connID).inStream.readObject();
			//	}
				
		 } }catch (SocketException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			 
			 
			 GlobalVar.outConn.remove(connID);
			 
			 
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 	
		
		
	}
	
	
}
