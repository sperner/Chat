//Verteilte Anwendungen - Uebungsblatt 4
//Aufgabe 1: TCP IRC-Client
//
import java.net.*;
import java.io.*;

public class ChatTCPClient
{
	private Socket clientSocket;
	private InputStream in;
	private OutputStream out;
	private BufferedReader stdin;

	public ChatTCPClient( String hostname, int hostport )
	{
		try {
			clientSocket = new Socket( hostname, hostport );
			in = clientSocket.getInputStream();
			out = clientSocket.getOutputStream();
			stdin = new BufferedReader( new InputStreamReader(System.in) );
		} catch (UnknownHostException e) {
			System.out.println("Sock:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}
	}

	private Socket getSocket()
	{
		return clientSocket;
	}

	private void close(Socket cSocket)
	{
		try {
			cSocket.close();
		} catch (IOException e) {
			System.out.println("IO :" + e.getMessage());
		}
	}

	private void chat()
	{
		try {
			//int bufSize = 1024;
			//BufferedInputStream inBuf = new BufferedInputStream( in, bufSize );
			//BufferedOutputStream outBuf = new BufferedOutputStream( out, bufSize );

			System.out.print( ">" );
			String message = "";
			boolean on = true;
			while( on == true )
			{
				message = stdin.readLine() + '\n';
				if( message.equals("quit\n") )
				{
					message = "QUIT\n";
					on = false;
				}
				out.write( message.getBytes() );
				out.flush();
				message = "";
				//inBuf.close();
				//outBuf.close();
			}				
			System.out.println( "closing the Socket..." );
			clientSocket.close();

		} catch (UnknownHostException e) {
			System.out.println("Sock:" + e.getMessage());
			close(clientSocket);
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
			close(clientSocket);
		} catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
			close(clientSocket);
		}
	}

	private Thread createHandler(final Socket aClientSocket) {
		Thread handler = new Thread(new Runnable() {
			public void run() {
				try {
					InputStream in = aClientSocket.getInputStream();
					OutputStream out = aClientSocket.getOutputStream();
					int c;
					String reply = "";
					boolean on = true;
					while( on == true )
					{
						while( (c = in.read()) != -1 && ( (char)c != '\n' ) && ( (char)c != '\r' ) ) {
							reply += (char)c;
						}
						if( !reply.equals("") && !reply.equals("\n") ) {
							System.out.printf( "\n<%s\n>", reply );
						}
						if( reply.indexOf("QUIT") > 5 ) {
							on = false;
						}
						reply = "";
					}
					System.out.println("Closing client socket");
					aClientSocket.close();

				} catch (EOFException e) {
					System.out.println("EOF:" + e.getMessage());
					close(aClientSocket);
				} catch (IOException e) {
					System.out.println("IO:" + e.getMessage());
					close(aClientSocket);
				}
			}
		});
		return handler;
	}

	public static void main( String args[] )
	{
		if( args.length != 2 )
		{
			System.out.println( "Usage: TCPClient hostname port" );
			System.exit(1);
		}

		ChatTCPClient myClient = new ChatTCPClient( args[0], Integer.parseInt(args[1]) );
		Thread myHandler = myClient.createHandler( myClient.getSocket() );
		myHandler.start();
		myClient.chat();
	}
}
