//Verteilte Anwendungen - Uebungsblatt 4
//zu Aufgabe 1b: TCP IRC Reactor-Server
//
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Reactor
{
	protected ServerSocketChannel serverChannel;
	private InetAddress hostAddress;
	private int port;

	private EventHandlerTable eventHandlerTable = new EventHandlerTable( );
	protected Selector selector;


	public Reactor( InetAddress hostAddress, int port ) throws IOException
	{
		this.hostAddress = hostAddress;
		this.port = port;
		this.selector = this.initSelector();
	}


	private Selector initSelector( ) throws IOException
	{
		Selector socketSelector = SelectorProvider.provider().openSelector( );

		this.serverChannel = ServerSocketChannel.open( );
		serverChannel.configureBlocking( false );

		InetSocketAddress isa = new InetSocketAddress( this.hostAddress, this.port );
		serverChannel.socket().bind( isa );

		return socketSelector;
	}

	public static void main( String[] args )
	{
		if( args.length != 1 )
		{
			System.out.println("Usage: Reactor <port>");
			System.exit(1);
		}

		try {
			Reactor reactor = new Reactor( null, Integer.parseInt(args[0]) );
			EventHandler handler = new AcceptHandler( reactor.serverChannel, reactor );
			reactor.registerEventHandler( handler, SelectionKey.OP_ACCEPT );
			System.out.printf( "Reactor is listening on Port %s\n", args[0] );
			reactor.run( );

		} catch (IOException e) {
			e.printStackTrace( );
		}
	}

	public void run( )
	{
		while( true )
		{
			try {	// Wait for an event one of the registered channels
				this.selector.select( );
			} catch (IOException e) {
				// TODO needs proper exception handling, here we break and stop
				e.printStackTrace( );
				break;
			}
			Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator( );
			while( selectedKeys.hasNext() )
			{
				SelectionKey key = selectedKeys.next( );
				selectedKeys.remove( );
				if( !key.isValid() )
					continue;	// Nothing to do

				List<EventHandler> handlers = eventHandlerTable.getEventHandlers( key.channel(), key );
				Iterator<EventHandler> iterator = handlers.iterator( );
				while( iterator.hasNext() )
				{
					EventHandler handler = iterator.next( );
					try {
						if( key.isAcceptable() )
							handler.handleAcceptEvent( key.channel() );
						else if( key.isReadable() )
							handler.handleReadEvent( key.channel() );
						else if( key.isWritable() )
							handler.handleWriteEvent( key.channel() );
						
					} catch (IOException e) {
						// if any exception occurs, we remove the handler and cancel the (invalid) selection key
						iterator.remove( );
						key.cancel( );
						System.out.println( "IOException " + e.getMessage() );
						System.out.println( "Removed :" + handler.getClass() );
					}
				}
				eventHandlerTable.cleanUp( );
			}
		}
		/* NOTREACHED */
	}

	public void registerEventHandler( EventHandler handler, int selectionKeyType )
			throws ClosedChannelException {
		eventHandlerTable.add( handler, selectionKeyType );
		// double dispatch!
		handler.getChannel().register( this.selector, selectionKeyType );
	}

	public void removeEventHandler( EventHandler handler, int selectionKeyType ) {
		eventHandlerTable.remove( handler, selectionKeyType );
	}

	public Set<EventHandler> getEventHandlers( ) {
		return eventHandlerTable.getEventHandlers( );
	}

}
