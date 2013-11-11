//Verteilte Anwendungen - Uebungsblatt 6
//zu Aufgabe 1a: Users fuer RMI IRC Server
//
package rmi;

import java.io.*;
import java.rmi.*;
import java.util.*;

public class RmiUsers
{
	private static Set<RmiUser> chatUsers = new HashSet<RmiUser>();
	private static int anzUser = 0;

	public static void add( RmiUser aUser )
	{
		chatUsers.add( aUser );
		anzUser++;
		System.out.printf("reg:%2d all:%2d # New Client connected\n", anzUser, chatUsers.size() );
	}

	public static void del( RmiUser aUser )
	{
		if( aUser == null )
			return;
		chatUsers.remove( aUser );
		anzUser--;
		System.out.printf("reg:%2d all:%2d # Client disconnected\n", anzUser, chatUsers.size() );
	}

	public static String getUserlist() 
	{
		if( chatUsers.isEmpty() )
		{
			return "";
		}
		String tmpList = "";
		for (RmiUser tmpUser: chatUsers)
		{
			tmpList += " " + tmpUser.getName();
		}
		return tmpList;
	}

	public static String getChannelUsers( String aChannel ) 
	{
		if( chatUsers.isEmpty() )
		{
			return "";
		}
		String tmpList = "";
		for (RmiUser tmpUser: chatUsers)
		{
			if( tmpUser.getChannel().equals( aChannel ) )
				tmpList += " " + tmpUser.getName();
		}
		return tmpList;
	}

	public static RmiUser getSubUser( Subscriber aSubscriber )
	{
		for( RmiUser tmpUser: chatUsers )
		{
			if( tmpUser.getSubscriber().equals(aSubscriber) )
			{
				return tmpUser;
			}
		}
		return null;
	}

	public static String getSubName( Subscriber aSubscriber )
	{
		for( RmiUser tmpUser: chatUsers )
		{
			if( tmpUser.getSubscriber().equals(aSubscriber) )
			{
				String tmpName = tmpUser.getName();
				return tmpName;
			}
		}
		return null;
	}

	public static String getSubChannel( Subscriber aSubscriber )
	{
		for( RmiUser tmpUser: chatUsers )
		{
			if( tmpUser.getSubscriber().equals(aSubscriber) )
			{
				String tmpChannel = tmpUser.getChannel();
				return tmpChannel;
			}
		}
		return null;
	}

	public static void setSubName( Subscriber aSubscriber, String aChannel )
	{
		for( RmiUser tmpUser: chatUsers )
		{
			if( tmpUser.getSubscriber().equals(aSubscriber) )
			{
				tmpUser.setChannel( aChannel );
			}
		}
	}

}

