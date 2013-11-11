//Verteilte Anwendungen - Uebungsblatt 6
//Aufgabe 1a: RMI IRC Server / Server
//
package rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.util.Date;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class Server
{
	public static String channelServerName = "SIDETRACK";
	public static String channelList = "";

	public synchronized static void main( String[] args )
	{
		if (args.length != 1) {
			System.out.println("Usage: Server <path-to-configfile>");
			System.exit(1);
		}

		FileInputStream myFile = null;
		try {
			myFile = new FileInputStream( args[0] );
		} catch (FileNotFoundException e) {
			System.out.println( "File not found: " + args[0] + " " + e );
		}
		DataInputStream myFileStream = new DataInputStream( myFile );

		if( System.getSecurityManager() == null )
		{
			System.setSecurityManager( new RMISecurityManager() );
			System.out.println( "Using RMISecurityManager" );
		}

		try {
			while( myFileStream.available() > 0 )
			{
				String tmpName = myFileStream.readLine();
				Thread channelHandler = createHandler( tmpName );
				channelHandler.start();
				channelList += tmpName + "\n";
			}
		} catch (IOException e) {
			System.out.println( "Input Exception: " + e );
		}

		try {
			PublisherServant myServant = new PublisherServant( channelServerName );
			Naming.rebind( channelServerName, myServant );
		} catch (RemoteException e) {
			System.out.println( "Binding Server failed: " + e );
		} catch (MalformedURLException e) {
			System.out.println( "MalformedURLException: " + e );
		}

		try {
			Thread.sleep(1000);
			System.out.println( "Channel-Server is ready!" );
			while( true )
			{
				Thread.sleep(2000);
			}
		} catch (InterruptedException e) {
			System.out.println( "Server interrupted: " + e );
		}
	}

	public synchronized static Thread createHandler( final String aName ) {
		Thread handler = new Thread(new Runnable() {
			public void run() {
				PublisherServant aServant = null;
				try {
					aServant = new PublisherServant( aName );
					Naming.rebind( aName, aServant );
				} catch (RemoteException e) {
					System.out.println( "RemoteException: " + " in " + aName + "\n" + e );
				} catch (MalformedURLException e) {
					System.out.println( "MalformedURLException: " + " in " + aName + "\n" + e );
				}
				System.out.println( "Thread: Created Channel '" + aName + "'" );
			}
		});
		return handler;
	}

}
