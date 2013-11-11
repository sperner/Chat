//Verteilte Anwendungen - Uebungsblatt 3
//Aufgabe 1a: UDP IRC-Server
//
import java.util.StringTokenizer;

import java.io.IOException;
import java.net.*;

public class ChatUDPServer {

	private DatagramSocket srvSocket = null;	//
	private byte[] buffer = new byte[1000];		//Packet-Buffer
	private DatagramPacket request = null;		//
	private DatagramPacket reply = null;		//

	private byte[] message = new byte[1000];
	private String command = "";
	private String option = "";
	private String answer = "";

	private String[] userNames = new String[20];
	private InetAddress[] userAdds = new InetAddress[20];
	private int[] userPorts = new int[20];
	private int anzUser = 0;
	private int aktUser = 0;
	private boolean used = false;


	private ChatUDPServer(int serverPort)
	{
		try {
			srvSocket = new DatagramSocket( serverPort );
			System.out.println( "Socket Created, listening on: " + serverPort );
		} catch( SocketException e ) {
			System.out.println("Socket: " + e.getMessage());
		} catch( IOException e ) {
			System.out.println("IO: " + e.getMessage());
		}
	}

	private void listen()
	{
		try {
			request = new DatagramPacket( buffer, buffer.length );
			srvSocket.receive( request );
		} catch( SocketException e ) {
			System.out.println("Socket: " + e.getMessage());
		} catch( IOException e ) {
			System.out.println("IO: " + e.getMessage());
		}
	}

	private void checkClient()
	{
		for( int j=0; j<anzUser; j++ )
		{
			if( (userAdds[j].equals( request.getAddress() )) && (userPorts[j] == request.getPort()) )
			{
				used = true;
				aktUser = j;
				break;
			}
		}

	}

	private void parsePacket()
	{
		message = request.getData();
		StringTokenizer tokenizer = new StringTokenizer( new String(message) );

		if( tokenizer.hasMoreTokens() )
			command = tokenizer.nextToken();

		while (tokenizer.hasMoreTokens())
			option += tokenizer.nextToken() + " ";

		System.out.println(request.getAddress() + "/" + request.getPort() + ": " + new String(request.getData()));
	}

	private void execute( )
	{
		if( command.equals("USER") )
		{
			if( used != true )
				addUser( option, request.getAddress(), request.getPort() );
			//answer = "§ hello " + option;
			answer = "SERVER: " + userNames[aktUser] + command;
		}
		else if( command.equals("USERS") )
		{
			if( used == true )
			{
				//answer = "\n";
				for( int lv=0; lv<anzUser; lv++ )
					answer += userNames[lv] + ", ";// + '\n';
			}
			answer = "SERVER: " + userNames[aktUser] + command + ": " + answer;
		}
		else if( command.equals("PRIVMSG") )
		{
			if( used == true )
				answer = option;
			answer = "SERVER: " + userNames[aktUser] + command + ": " + answer;
		}
		else if( command.equals("QUIT") )
		{
			String tmpName = userNames[aktUser];
			if( used == true )
				delUser( );
			//answer = "§ goodbye";
			answer = "SERVER: " + tmpName + command;
		}
		else
			//answer = "§ [unknown command]";
			answer = "SERVER: Unknown Command: " + command;
	}

	private void send()
	{
		byte[] rmessage = answer.getBytes();
		reply = new DatagramPacket( rmessage, rmessage.length, request.getAddress(), request.getPort() );

		try {
			if( used )
			{
				srvSocket.send( reply );
				if( command.equals("PRIVMSG") )
				{
					String message = userNames[aktUser] + ": " + option;
					for( int k=0; k<anzUser; k++ )
					{
						if( userNames[aktUser] != userNames[k] )
						{
							reply = new DatagramPacket( message.getBytes(), message.length(), userAdds[k], userPorts[k]);
							srvSocket.send( reply );
							System.out.println( "Sent to: " + userAdds[k] + "/" + userPorts[k] + "/" + userNames[k] + " : " + message );
						}
					}
				}
				//else
					//System.out.println( "COMMAND" );
			}
		} catch( SocketException e ) {
			System.out.println("Socket: " + e.getMessage());
		} catch( IOException e ) {
			System.out.println("IO: " + e.getMessage());
		}
	}

	private void clean()
	{
		for( int i = 0 ; i < buffer.length ; i++ )
			buffer[i] = '\0';
		used = false;
		command = "";
		option = "";
		answer = "";
	}

	private void doUSER()
	{
		
	}

	private void doUSERS()
	{
		
	}

	private void doPRIVMSG()
	{
		
	}

	private void doQUIT()
	{
		
	}

	private void doError()
	{
		
	}

	private void addUser( String name, InetAddress address, int port )
	{
		userNames[anzUser] = name;
		userAdds[anzUser] = address;
		userPorts[anzUser] = port;
		anzUser++;
		aktUser = anzUser-1;
		used = true;
		System.out.println( "user added: " + name + ", Address: " + address + ", Port: " + port + ", aktAnzUser: " + anzUser);
	}

	private void delUser( )
	{
		for( int lv=0; lv<anzUser; lv++ )
		{
			if( (userAdds[lv].equals( userAdds[aktUser] )) && (userPorts[lv] == userPorts[aktUser]) )
			{
				System.out.println( "user deleted: " + userNames[lv] + ", Address: " + userAdds[lv] + ", Port: " + userPorts[lv] + ", aktAnzUser: " + (anzUser-1));
				if( lv == (anzUser-1) )
				{
					userNames[anzUser] = null;
					userAdds[anzUser] = null;
					userPorts[anzUser] = 0;
					if( lv > 0 )
						aktUser = lv-1;
					else
						aktUser = 0;
				}
				else
				{
					for( int lv2=lv; lv2<(anzUser-1); lv2++ )
					{
						userNames[lv2] = userNames[lv2+1];
						userAdds[lv2] = userAdds[lv2+1];
						userPorts[lv2] = userPorts[lv2+1];
					}
				}
				anzUser--;
				break;
			}
		}
	}



	public static void main(String args[])
	{
		if( args.length != 1 ) {
			System.out.println( "Usage: UDPServer port" );
			System.exit(1);
		}
		int port = Integer.parseInt(args[0]);
		ChatUDPServer myServer = new ChatUDPServer( port );

		while( true )
		{
			myServer.listen();
			myServer.checkClient();
			myServer.parsePacket();
			myServer.execute();
			myServer.send();
			myServer.clean();
		}
	}

}
