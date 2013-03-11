package core;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

@SuppressWarnings("unused")
public class WorkerThread extends Thread
{
	int connID;
	int incomingID;
	boolean foundNewSuccessor=false;
	boolean conveyedToOldSuccessor=false;
	Random numGenerator= new Random();
	  StringTokenizer st=null;
	WorkerThread(int connID, String incomingID)
	{
		this.connID = connID;
		this.incomingID=Integer.parseInt(incomingID);
	}
	public void run()
	{
		Random numGenerator= new Random();
		try
		{
			  while(true)
			  {
				//read from my predecessor, he is telling me my new predecessor
				    boolean alreadyconnected=false;
				    
				    
				    String fromMySucessor=null;
					try {
						fromMySucessor = GlobalVar.inConn.get(connID).inStream.readObject().toString();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				    st=new StringTokenizer(fromMySucessor);
				    String commandHeader=st.nextToken("|");
				   if(fromMySucessor.contains("response")==true)
				    {
				    	System.out.println("received response "+fromMySucessor);
				    	StringTokenizer st1=new StringTokenizer(fromMySucessor);
				    	
				    	String response=st1.nextToken("|").trim();
				    	String senderIdentifier=st1.nextToken("|").trim();
				    	String cmd=st1.nextToken("|").trim();
				    	String entryToBeChecked=st1.nextToken("|").trim();
				    	String answer=st1.nextToken("|").trim();
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
									object.outStream.writeObject(fromMySucessor);
									object.outStream.flush();
				    			}
				    		}		
				    		
				    	}
				    	
				    	
				    }
				    
		 
			//	GlobalVar.outConn.get(connID).inStream.readObject();
				//	}
					
			 } }catch (SocketException e) {
				 
				 
				 GlobalVar.inConn.remove(connID);
				 
				 				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
		}

	}

