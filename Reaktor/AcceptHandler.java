//Verteilte Anwendungen - Uebungsblatt 4
//zu Aufgabe 1b: Accept-Handler für TCP IRC Reactor-Server
//
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptHandler implements EventHandler
{
	private Reactor reactor;
	private SelectableChannel channel;

	public AcceptHandler( ServerSocketChannel channel, Reactor reactor ) throws ClosedChannelException
	{
		this.reactor = reactor;
		this.channel = channel;
	}


	public void handleAcceptEvent( SelectableChannel channel ) throws IOException
	{
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) channel;
		SocketChannel socketChannel = serverSocketChannel.accept( );
		socketChannel.configureBlocking( false );
		//System.out.printf( "AcceptHandler: received accept event from %s/%d\n", socketChannel.socket().getInetAddress().getHostAddress(), socketChannel.socket().getPort() );

		ChatHandler handler = new ChatHandler( socketChannel, reactor );
		reactor.registerEventHandler( handler, SelectionKey.OP_READ );
		reactor.registerEventHandler( handler, SelectionKey.OP_WRITE );

		try {
			ReactorUser.add( socketChannel );
		} catch (NullPointerException e) { 
			System.err.println( "NP: " + e.getMessage() );
		}
	}

	public void handleReadEvent( SelectableChannel channel ) throws IOException
	{
		throw new IOException( "we should never get called" );
	}

	public void handleWriteEvent( SelectableChannel channel ) throws IOException
	{
		throw new IOException( "we should never get called" );
	}

	public SelectableChannel getChannel( )
	{
		return this.channel;
	}

}
