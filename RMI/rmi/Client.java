//Verteilte Anwendungen - Uebungsblatt 6
//Aufgabe 1a: RMI IRC Client
//
package rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Client
{
	private static String channelServerName = "SIDETRACK";

	public static void main( String[] args )
	{
		BufferedReader stdin = new BufferedReader( new InputStreamReader(System.in) );

		if( System.getSecurityManager() == null )
		{
			System.setSecurityManager( new RMISecurityManager() );
			System.out.println( "Using RMISecurityManager" );
		}

		Subscriber mySubscriber = null;
		Publisher chatPublisher = null;
		try {
			mySubscriber = new SubscriberImpl( );
		} catch (RemoteException e) {
			System.out.println( "Unable to create subscriber, reason: " + e.toString() );
		}
		try {
			chatPublisher = (Publisher) Naming.lookup( channelServerName );
		} catch (RemoteException e) {
			System.out.println( "RemoteException: " + e.toString() );
		} catch (NotBoundException e) {
			System.out.println( "NotBoundException: " + e.toString() );
		} catch (MalformedURLException e) {
			System.out.println( "MalformedURLException: " + e.toString() );
		}


		StringTokenizer tokenizer = null;
		String username= "";
		String command = "";
		String option = "";

		boolean auth = false;
		boolean on = true;
		while( on )
		{
			try {
				tokenizer = new StringTokenizer( stdin.readLine( ) );
			} catch (IOException e) {
				System.out.println( "Unable to read from keyboard, reason: " + e.toString() );
			}

			if( tokenizer.hasMoreTokens() )
				command = tokenizer.nextToken( );

			if( tokenizer.hasMoreTokens() )
				option += tokenizer.nextToken( );
			while( tokenizer.hasMoreTokens() )
				option += " " + tokenizer.nextToken( );

			if( auth )
			{
				if( command.equals( "PRIVMSG" ) )
				{
					try {
						chatPublisher.cmdPRIVMSG( mySubscriber, option );
					} catch (RemoteException e) {
						System.out.println( "Unable to send message, reason: " + e.toString() );
					}
				}
				else if( command.equals( "USERS" ) )
				{
					try {
						chatPublisher.cmdUSERS( mySubscriber );
					} catch (RemoteException e) {
						System.out.println( "Unable to request userlist, reason: " + e.toString() );
					}
				}
				else if( command.equals( "QUIT" ) )
				{
					try {
						chatPublisher.cmdQUIT( mySubscriber );
					} catch (RemoteException e) {
						System.out.println( "Unable to quit, reason: " + e.toString() );
					}
					auth = false;
					on = false;
				}
				else if( command.equals( "LIST" ) )
				{
					try {
						chatPublisher.cmdLIST( mySubscriber );
					} catch (RemoteException e) {
						System.out.println( "Unable to request channellist, reason: " + e.toString() );
					}
				}
				else if( command.equals( "JOIN" ) )
				{
					try {
						chatPublisher.unsubscribe( mySubscriber );
						chatPublisher = (Publisher) Naming.lookup( option );
						chatPublisher.subscribe( mySubscriber );
						chatPublisher.cmdJOIN( mySubscriber, option );
					} catch (RemoteException e) {
						System.out.println( "Unable to request channellist, reason: " + e.toString() );
					} catch (NotBoundException e) {
						System.out.println( "NotBoundException: " + e.toString() );
					} catch (MalformedURLException e) {
						System.out.println( "MalformedURLException: " + e.toString() );
					}
				}
				else
				{
					System.out.printf("Unknown Command: %s\n", command);
				}
			}
			else if( command.equals( "USER" ) )
			{
				try {
					chatPublisher.subscribe( mySubscriber );
					chatPublisher.cmdUSER( mySubscriber, option, channelServerName );
				} catch (RemoteException e) {
					System.out.println( "Unable to register, reason: " + e.toString() );
				}
				username = option;
				auth = true;
			}
			else if( command.equals( "QUIT" ) )
			{
				on = false;
			}
			else
			{
				System.out.printf("You have to log in first\n");
			}
			command = "";
			option = "";
		}//while(on)
		try {
			chatPublisher.unsubscribe( mySubscriber );
		} catch (RemoteException e) {
			System.out.println( "Unable to unsubscribe, reason: " + e.toString() );
		}
		System.exit(1);
	}//main()

}
