//Verteilte Anwendungen - Uebungsblatt 3
//Aufgabe 1b: UDP IRC-Client
//
import java.net.*;
import java.io.*;

public class ChatUDPClient
{
	public static void main( String args[] ) 
	{
		try {
			if( args.length != 2 )
			{
				System.out.println( "Usage: UDPClient hostname port" );
				System.exit( 1 );
			}
			String hostname = args[0];
			int port = Integer.parseInt( args[1] );
			System.out.print( ">" );

			final DatagramSocket aSocket = openSocket();
			boolean on = true;
			while( on == true )
			{
				BufferedReader bin = new BufferedReader( new InputStreamReader(System.in) );
				if( message.equals("QUIT") || message.equals("USERS") )
					String message = bin.readLine() + " ";

				InetAddress aHost = InetAddress.getByName( hostname );
				DatagramPacket request = new DatagramPacket( message.getBytes(), message.length(), aHost, port );
				aSocket.send( request );

				Thread thread = new Thread(new Runnable()
				{
					public void run ( )
					{
						try {
							byte[] buffer = new byte[5000];	//-> bei 1400 werden nur 10 Zeichen angenommen!??
							DatagramPacket reply = null;
							while( true )
							{
								reply = new DatagramPacket( buffer, buffer.length );
								aSocket.receive( reply );
								System.out.println( "<" + new String(reply.getData()) );
								for( int i = 0 ; i < buffer.length ; i++ )
									buffer[i] = '\0';
								System.out.print( ">" );
							}
						} catch( SocketException e ) {
							System.out.println( "Socket: " + e.getMessage() );
						} catch( IOException e ) {
							System.out.println( "IO: " + e.getMessage() );
						}
					}
				});
				thread.start();
			}
			aSocket.close();

		} catch( SocketException e ) {
			System.out.println( "Socket: " + e.getMessage() );
		} catch( IOException e ) {
			System.out.println( "IO: " + e.getMessage() );
		}
	}

	public static DatagramSocket openSocket()
	{
		for( int localport = 50000 ; localport < Integer.MAX_VALUE ; localport++ )
		{
			try
			{
				DatagramSocket aSocket = new DatagramSocket();
				return aSocket;
			}
			catch( SocketException e )
			{
			}
		}
		return null;
	}
}
