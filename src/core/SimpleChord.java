package core;




import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@SuppressWarnings("unused")
public class SimpleChord
{
	

	public static void main(String args[])
	{
		
		Random numGenerator= new Random();
		String command=null;


		GlobalVar.myconnectionPort=Short.parseShort(args[0]);
		//GlobalVar.myIdentifier=Integer.parseInt(args[1]);
		int connID;


		if (args.length<2)
		{
			System.out.println("Default ports will be Used");
			GlobalVar.myconnectionPort = 6346;
			ServerThread serverThread =new ServerThread(GlobalVar.myconnectionPort);
			serverThread.start();
		}
		else
		{
			ServerThread serverThread =new ServerThread(GlobalVar.myconnectionPort);
			serverThread.start();
		}

		Socket s=null;
		try {
			s = new Socket("www.google.com",80);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Local IP:" + s.getLocalAddress().getHostAddress());
		System.out.println("Chord Port:" + GlobalVar.myconnectionPort );
		
		//initialize my finger table and node info
		Operations.initializeFingerTable(args[1],s.getLocalAddress().getHostAddress());
		
		while(true)
		{

			System.out.print("chord>");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			try {
				command=in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] temp;
			/*if(command.equals("info"))
		{
			Socket s= new Socket("www.google.com",80);
			System.out.println("IPAddress  TCP port   DownloadPort");
			System.out.println(s.getLocalAddress().getHostAddress()+" "+GlobalVar.myconnectionPort+ " " + GlobalVar.mydownloadPort);
			s.close();
		}*/
			
			 if(command.contains("open"))
			{
				connID = numGenerator.nextInt();
				temp=command.split(" ");
				Operations.connect(temp[1],connID);
			}
			else if(command.contains("info"))
			{
				Operations.showMyInfo();
			}
			else if(command.contains("exit") || command.contentEquals("Exit") || command.contentEquals("quit") || command.contentEquals("Quit"))
			{
				System.out.println("Bye!!");
				System.exit(0);
			}
			/*else if(command.contains("Monitor")|| (command.contains("monitor")))
			{

			}
			else if (command.contains("share"))
			{
				temp=command.split(" ");
				//System.out.println("Sharing" + new File("").getAbsolutePath());
				Operations.shareDirectory(temp[1]);
			}
			else if(command.contentEquals("Scan") || command.contentEquals("scan"))
			{
				Operations.scan();
			}
			else if(command.contains("exit") || command.contentEquals("Exit") || command.contentEquals("quit") || command.contentEquals("Quit"))
			{
				System.out.println("Bye!!");
				System.exit(0);
			}
			else if( (command.contentEquals("bye")  || command.contentEquals("Bye")))
			{
				System.out.print("Sorry wrong command to leave .... try using Exit/Quit");
			}
			/*else if(command.contains("ponginfo"))
			{
				Operations.showPongInfo();
			}
			else if(command.contains("showfileinfo"))
			{
				Operations.showFilesInfo();
			}
			else if(command.contains("find"))
			{
				temp=command.split(" ",2);
				Operations.initiateQuery(temp[1]);
			}
			else if(command.contains("download"))
			{
				temp=command.split(" ");
				DownloadClientThread downloadclientThread =new DownloadClientThread(Integer.parseInt(temp[1]));
				downloadclientThread.start();	
				
			}
			else if(command.contains("list"))
			{
				Operations.showlistResults();

			}
			else if(command.contains("clear"))
			{
				temp = command.split(" ");

				if(temp.length==1){
					Operations.clearListResults("all");
				}
				else{
					Operations.clearListResults(temp[1]);}

			}
			else if(command.contains("info"))
			{
				Operations.handleInfoCommand(command);

			}*/
			else
			{
				System.out.println("unknown command");
			}
		}

	}



}