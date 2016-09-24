package com.secure.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by bhegde on 24-09-2016.
 */
public class Distributor extends Thread
{
    private Vector dClients = new Vector();
    private Vector dUsers = new Vector();
    private Vector dMessages = new Vector();
    private int anzRegistered = 0;

    public synchronized void addClient( Socket aClientSocket )
    {
        dClients.add( aClientSocket );
        dUsers.add( "noname" );
        System.out.printf("reg:%2d all:%2d # %s/%d # new Client connected\n", anzRegistered, dUsers.size(), aClientSocket.getInetAddress().getHostAddress(), aClientSocket.getPort());
    }

    public synchronized void delClient( Socket aClientSocket )
    {
        int i = dClients.indexOf( aClientSocket );
        if (i != -1)
        {
            String tmpName = (String)dUsers.get(i);
            String message = "SERVER: " + tmpName + " QUIT\n";
            if( !tmpName.equals("noname") )
                anzRegistered--;
            dClients.removeElementAt( i );
            dUsers.removeElementAt( i );
            sendInfo( aClientSocket, message );
            try {
                aClientSocket.close( );
            } catch(IOException exception) {
                System.err.println("IO: " + exception.getMessage());
                close( aClientSocket );
            }
        }
    }

    private void close(Socket cSocket)
    {
        try {
            cSocket.close();
        } catch (IOException e) {
            System.out.println("IO :" + e.getMessage());
        }
    }

    public synchronized void sendInfo( Socket aClientSocket, String aMessage)
    {
        try {
            OutputStream out = aClientSocket.getOutputStream( );
            out.write( aMessage.getBytes() );
            out.flush( );
        } catch(IOException exception) {
            System.err.println("IO: " + exception.getMessage());
            delClient( aClientSocket );
        }
    }

    private synchronized void setUsername( Socket aClientSocket, String aClientName )
    {
        String message = "";
        if( aClientName.length() > 1 )
        {
            int i = dClients.indexOf( aClientSocket );
            if( i != -1 )
            {
                dUsers.set( i, aClientName );
                message = "SERVER: " + aClientName + " USER\n";
            }
            anzRegistered++;
        }
        else
            message = "SERVER: USERNAME: '" + aClientName + "' is to short!\n";
        sendInfo( aClientSocket, message );
    }

    private synchronized void showUsers( Socket aClientSocket )
    {
        String message = "";
        int i = dClients.indexOf( aClientSocket );
        String tmpName = (String)dUsers.get(i);
        if( !tmpName.equals("noname") )
        {
            message = "SERVER: " + (String)dUsers.get(i) + " USERS: ";
            for( i=0 ; i < dUsers.size() ; i++ )
            {
                message += (String)dUsers.get(i);
                if( i < (dUsers.size()-1) )
                    message += ", ";
            }
            message += "\n";
        }
        else
            message = "SERVER: You are not registered" + "\n";
        sendInfo( aClientSocket, message );
    }

    private synchronized void sendMessage( Socket aClientSocket, String aMessage )
    {
        String message = "";
        int i = dClients.indexOf( aClientSocket );
        String tmpName = (String)dUsers.get(i);
        if( !tmpName.equals("noname") )
        {
            message = (String)dUsers.get(i) + ": " + aMessage + "\n\r";
            dMessages.add( message );
        }
        else
        {
            message = "SERVER: You are not registered" + "\n";
            sendInfo( aClientSocket, message );
        }
    }

    public synchronized void parseMessage( Socket aClientSocket, String aMessage )
    {
        int i = dClients.indexOf( aClientSocket );
        String command = "";
        String option = "";
        StringTokenizer tokenizer = new StringTokenizer( aMessage );

        if( tokenizer.hasMoreTokens() )
            command = tokenizer.nextToken( );

        if( tokenizer.hasMoreTokens() )
            option += tokenizer.nextToken( );
        while( tokenizer.hasMoreTokens() )
            option += " " + tokenizer.nextToken( );

        if( command.equals("USER") )
        {
            setUsername( aClientSocket, option );
            System.out.printf("reg:%2d all:%2d # %s/%d # %s: User '%s' added\n", anzRegistered, dUsers.size(), aClientSocket.getInetAddress().getHostAddress(), aClientSocket.getPort(), (String)dUsers.get(i), option);
        }
        else if( command.equals("USERS") )
        {
            showUsers( aClientSocket );
            System.out.printf("reg:%2d all:%2d # %s/%d # %s: Userlist requested\n", anzRegistered, dUsers.size(), aClientSocket.getInetAddress().getHostAddress(), aClientSocket.getPort(), (String)dUsers.get(i));
        }
        else if( command.equals("PRIVMSG") )
        {
            sendMessage( aClientSocket, option );
            System.out.printf("reg:%2d all:%2d # %s/%d # %s: Message '%s'\n", anzRegistered, dUsers.size(), aClientSocket.getInetAddress().getHostAddress(), aClientSocket.getPort(), (String)dUsers.get(i), option);
        }
        else if( command.equals("QUIT") )
        {
            delClient( aClientSocket );
            System.out.printf("reg:%2d all:%2d # %s/%d # %s: Quit from Chat\n", anzRegistered, dUsers.size(), aClientSocket.getInetAddress().getHostAddress(), aClientSocket.getPort(), (String)dUsers.get(i));
        }
        else
        {
            String message = "SERVER: Unknown Command: " + command + "\n";
            sendInfo( aClientSocket, message );
            System.out.printf("reg:%2d all:%2d # %s/%d # %s: Unknown Command '%s'\n", anzRegistered, dUsers.size(), aClientSocket.getInetAddress().getHostAddress(), aClientSocket.getPort(), (String)dUsers.get(i), command);
        }
        notify( );
    }

    private synchronized String getNextMessage() throws InterruptedException
    {
        while( dMessages.size() == 0 )
            wait( );
        String message = (String)dMessages.get( 0 );
        dMessages.removeElementAt( 0 );
        return message;
    }

    private synchronized void send2Clients( String aMessage )
    {
        for( int i=0 ; i < dClients.size() ; i++ )
        {
            Socket socket = (Socket)dClients.get(i);
            sendInfo( socket, aMessage );
        }
    }

    public void run()
    {
        try {
            while( true )
            {
                String message = getNextMessage();
                send2Clients(message);
            }
        } catch (InterruptedException ie) {
            // Thread interrupted. Do nothing
        }
    }
}
