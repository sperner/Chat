//Verteilte Anwendungen - Uebungsblatt 6
//zu Aufgabe 1a: User fuer RMI IRC Server
//
package rmi;

import java.io.*;
import java.rmi.*;
import java.util.*;

public class RmiUser
{
	private Subscriber uHost;
	private String uName = "";
	private String uChannel = "";


	public RmiUser( Subscriber aSubscriber, String aName, String aChannel )
	{
		uHost = aSubscriber;
		uName = aName;
		uChannel = aChannel;
	}


	public Subscriber getSubscriber( )
	{
		return uHost;
	}

	public String getName( )
	{
		return uName;
	}

	public String getChannel( )
	{
		return uChannel;
	}

	public void setChannel( String aChannel )
	{
		uChannel = aChannel;
	}

}

