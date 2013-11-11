//Verteilte Anwendungen - Uebungsblatt 4
//zu Aufgabe 1b: User für TCP IRC Reactor-Server
//
import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ReactorUser
{
	private static Vector uHosts = new Vector();	//Channels
	private static Vector uNames = new Vector();	//Names
	private static int anzRegistered = 0;


	public static void add( SocketChannel uChannel )
	{
		uHosts.add( uChannel );
		uNames.add( "noname" );
		System.out.printf("reg:%2d all:%2d # %s/%d # new Client connected\n", anzRegistered, uNames.size(), uChannel.socket().getInetAddress().getHostAddress(), uChannel.socket().getPort());
	}

	public static void del( SocketChannel uChannel )
	{
		int i = uHosts.indexOf( uChannel );
		if (i != -1)
		{
			String tmpName = (String)uNames.get(i);
			if( !tmpName.equals("noname") )
				anzRegistered--;
			uHosts.removeElementAt( i );
			uNames.removeElementAt( i );
		}
		anzRegistered--;
		System.out.printf("reg:%2d all:%2d # %s/%d # Client disconnected\n", anzRegistered, uNames.size(), uChannel.socket().getInetAddress().getHostAddress(), uChannel.socket().getPort());
	}

	public static void setName( SocketChannel uChannel, String aName )
	{
		int i = uHosts.indexOf( uChannel );
		if (i != -1)
			uNames.set( i, aName );
		anzRegistered++;
		System.out.printf("reg:%2d all:%2d # %s/%d # %s registered\n", anzRegistered, uNames.size(), uChannel.socket().getInetAddress().getHostAddress(), uChannel.socket().getPort(), aName);
	}

	public static Socket getChannel( String aName )
	{
		int i = uNames.indexOf( aName );
		if (i != -1)
			return (Socket)uHosts.get( i );
		else
			return null;
	}

	public static String getName( SocketChannel uChannel )
	{
		int i = uHosts.indexOf( uChannel );
		if (i != -1)
			return (String)uNames.get( i );
		else
			return "noname";
	}

	public static String getAllNames( )
	{
		String tmpUserList = "";
		for( int i=0 ; i<uNames.size() ; i++  )
		{
			tmpUserList += uNames.get(i);
			if( i != (uNames.size()-1) )
				tmpUserList += " ";
		}
		return tmpUserList;
	}
}
