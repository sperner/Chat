//Verteilte Anwendungen - Uebungsblatt 6
//Aufgabe 1a: RMI IRC Server / Publisher-Servant
//
package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class PublisherServant extends UnicastRemoteObject implements Publisher
{
	private static final long serialVersionUID = -5917495456883328569L;

	private List<Subscriber> subscribers = new ArrayList<Subscriber>();

	private String channelName = "";

	protected PublisherServant( String aName ) throws RemoteException
	{
		channelName = aName;
	}


	public boolean subscribe( Subscriber aSubscriber ) throws RemoteException
	{
		//System.out.print( "Adding Subscriber " + aSubscriber.getName() + "....." );
		subscribers.add( aSubscriber );
		//System.out.println( "OK" );
		return true;
	}
	
	public boolean unsubscribe( Subscriber aSubscriber ) throws RemoteException
	{
		//System.out.print( "Removing Subscriber " + aSubscriber.getName() + "....." );
		subscribers.remove( aSubscriber );
		//System.out.println( "OK" );
		return true;
	}


	public void cmdUSER( Subscriber aSubscriber, String sName, String aChannel ) throws RemoteException
	{
		RmiUser user2add = new RmiUser(aSubscriber, sName, aChannel);
		RmiUsers.add( user2add );
		String message = "<SERVER: " + RmiUsers.getSubName( aSubscriber ) + " USER\n";
		System.out.printf("%s: User '%s' added\n", RmiUsers.getSubName(aSubscriber), sName );
		sendMessage( aSubscriber, message );
	}

	public void cmdUSERS( Subscriber aSubscriber ) throws RemoteException
	{
		if( !channelName.equals(Server.channelServerName) )
		{
			String message = "<SERVER: " + RmiUsers.getSubName( aSubscriber ) + " USERS: " + RmiUsers.getChannelUsers(channelName) + "\n";
			System.out.printf("%s: Userlist requested\n", RmiUsers.getSubName(aSubscriber) );
			sendMessage( aSubscriber, message );
		}
		else
			sendMessage( aSubscriber, "Not in Channels-Room\n" );
	}

	public void cmdPRIVMSG( Subscriber aSubscriber, String aMessage ) throws RemoteException
	{
		if( !channelName.equals(Server.channelServerName) )
		{
			String message = "<SERVER: " + RmiUsers.getSubName( aSubscriber ) + " PRIVMSG: " + aMessage + "\n";
			sendGroupMessage( aSubscriber, RmiUsers.getSubName( aSubscriber ) + ": " + aMessage + "\n" );
			System.out.printf("%s: Message '%s'\n", RmiUsers.getSubName(aSubscriber), aMessage );
			sendMessage( aSubscriber, message );
		}
		else
			sendMessage( aSubscriber, "Not in Channels-Room\n" );
	}

	public void cmdQUIT( Subscriber aSubscriber ) throws RemoteException
	{
		RmiUsers.del( RmiUsers.getSubUser(aSubscriber) );
		String message = "<SERVER: " + RmiUsers.getSubName( aSubscriber ) + " QUIT\n";
		System.out.printf("%s: Quit from Chat\n", RmiUsers.getSubName(aSubscriber) );
		sendMessage( aSubscriber, message );
	}

	public void cmdLIST( Subscriber aSubscriber ) throws RemoteException
	{
		String message = "<SERVER: " + RmiUsers.getSubName( aSubscriber ) + " LIST:\n" + Server.channelList;
		System.out.printf("%s: Channellist requested\n", RmiUsers.getSubName(aSubscriber) );
		sendMessage( aSubscriber, message );
	}

	public void cmdJOIN( Subscriber aSubscriber, String aChannel ) throws RemoteException
	{
		String message = "<SERVER: " + RmiUsers.getSubName( aSubscriber ) + " JOIN: " + aChannel + "\n";
		System.out.printf("%s: Joined Channel '%s'\n", RmiUsers.getSubName(aSubscriber), aChannel );
		sendMessage( aSubscriber, message );
		RmiUsers.setSubName( aSubscriber, aChannel );
	}

	public void sendMessage( Subscriber aSubscriber, String aMessage ) throws RemoteException
	{
		try {
			aSubscriber.notify( aMessage + ">" );
		} catch (RemoteException e) {
			System.out.println( "Unable to notify subscriber, reason: " + e.toString() );
		}
	}

	public void sendGroupMessage( Subscriber aSubscriber, String aMessage ) throws RemoteException
	{
		for( Subscriber subscriber: subscribers )
		{
			if( !subscriber.equals(aSubscriber) && isUserAuth(subscriber) )
			{
				try {
					subscriber.notify( aMessage + ">" );
				} catch (RemoteException e) {
					System.out.println( "Unable to notify subscriber, reason: " + e.toString() );
				}
			}
		}
	}

	public void sendAdminInfo( String aMessage ) throws RemoteException
	{
		for( Subscriber subscriber: subscribers )
		{
			try {
				subscriber.notify( aMessage );
			} catch (RemoteException e) {
				System.out.println( "Unable to notify subscriber, reason: " + e.toString() );
			}
		}
	}

	public boolean isUserAuth( Subscriber aSubscriber )
	{
		String tmpName = RmiUsers.getSubName( aSubscriber );
		if( tmpName != null )
			return true;
		else
			return false;
	}

}
