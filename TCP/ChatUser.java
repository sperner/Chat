//Verteilte Anwendungen - Uebungsblatt 4
//zu Aufgabe 1a/b: User für TCP IRC Server
//
import java.io.*;
import java.net.*;

public class ChatUser
{
	private String userName;
	private Socket userSocket;

	public ChatUser()
	{
		userName = "none";
		userSocket = null;
	}

	public ChatUser(String cName)
	{
		userName = cName;
		userSocket = null;
	}

	public ChatUser(Socket cSocket)
	{
		userName = "none";
		userSocket = cSocket;
	}

	public ChatUser(String cName, Socket cSocket)
	{
		userName = cName;
		userSocket = cSocket;
	}

	public void setName(String cName)
	{
		userName = cName;
	}

	public void setSocket(Socket cSocket)
	{
		userSocket = cSocket;
	}

	public Socket getSocket()
	{
		return userSocket;
	}

	public String getName()
	{
		return userName;
	}	

}
