package com.secure.server;

import java.net.Socket;

/**
 * Created by goodbytes on 9/24/2016.
 */
public class User {
    private String userName;
    private Socket userSocket;

    public User()
    {
        userName = "none";
        userSocket = null;
    }

    public User(String cName)
    {
        userName = cName;
        userSocket = null;
    }

    public User(Socket cSocket)
    {
        userName = "none";
        userSocket = cSocket;
    }

    public User(String cName, Socket cSocket)
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
