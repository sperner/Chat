package com.secure.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by bhegde on 24-09-2016.
 */
public class Listener extends Thread
{
    private Socket lSocket;
    private Distributor lDistributor;
    private BufferedReader lSocketReader;

    public Listener(Socket aSocket, Distributor aDistributor) throws IOException
    {
        lSocket = aSocket;
        lSocketReader = new BufferedReader( new InputStreamReader( lSocket.getInputStream() ) );
        lDistributor = aDistributor;
    }

    public void run()
    {
        try {
            while( !isInterrupted() )
            {
                String message = lSocketReader.readLine( );
                if( message == null )
                    break;
                lDistributor.parseMessage( lSocket, message );
            }
        } catch(IOException exception) {
            System.err.println("IO: " + exception.getMessage());
            lDistributor.delClient( lSocket );
        } catch(ArrayIndexOutOfBoundsException exception) {
            System.err.println("IO: " + exception.getMessage());
            lDistributor.delClient( lSocket );
        }
        lDistributor.delClient( lSocket );
    }
}