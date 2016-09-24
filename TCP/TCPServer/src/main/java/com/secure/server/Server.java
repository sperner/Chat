package com.secure.server;

import java.io.*;
import java.net.*;

/**
 * Created by goodbytes on 9/24/2016.
 */
public class Server {
    public static void main( String[] args ) throws IOException
    {
        if( args.length != 1 )
        {
            System.out.println( "Usage: ChatTCPServer <Port>" );
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);

        ServerSocket myServerSocket = new ServerSocket( port );
        System.out.println( "Server startet on " + myServerSocket.getInetAddress() + ":" + myServerSocket.getLocalPort());

        Distributor myDistributor = new Distributor( );
        myDistributor.start( );

        while( true )
        {
            Socket clientSocket = myServerSocket.accept( );
            Listener myListener = new Listener( clientSocket, myDistributor );
            myDistributor.addClient( clientSocket );
            myListener.start( );
        }
    }
}





//	private ChatUser[] Users = new ChatUser[20];
//	private int anzUser = 0;
//	private int aktUser = 0;
//	private boolean used = false;
