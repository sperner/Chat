//Verteilte Anwendungen - Uebungsblatt 6
//Aufgabe 1a: RMI IRC Server / Subscriber-Implementation
//
package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class SubscriberImpl extends UnicastRemoteObject implements Subscriber
{
	private static Date now = new Date( );
	private static final long serialVersionUID = now.getTime( );
	private String name;

	public SubscriberImpl( ) throws RemoteException
	{
		
	}


	public SubscriberImpl( String name ) throws RemoteException
	{
		this();
		this.name = name;
	}

	public long getSVUID( ) throws RemoteException
	{
		return serialVersionUID;
	}

	public String getName( ) throws RemoteException
	{
		return name;
	}

	public void notify( String message ) throws RemoteException
	{
		System.out.printf( message );
	}

}
