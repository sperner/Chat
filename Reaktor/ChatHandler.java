//Verteilte Anwendungen - Uebungsblatt 4
//zu Aufgabe 1b: Chat-Handler TCP IRC Reactor-Server
//
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.net.Socket;

public class ChatHandler implements EventHandler
{
	private Reactor reactor;
	private SocketChannel socketChannel;
	private ByteBuffer readBuffer = ByteBuffer.allocate( 4096 );

	private List<ByteBuffer> queue = new ArrayList<ByteBuffer>( );

	public ChatHandler( SocketChannel socketChannel, Reactor reactor ) throws ClosedChannelException
	{
		this.reactor = reactor;
		this.socketChannel = socketChannel;
	}


	public void handleAcceptEvent( SelectableChannel channel ) throws IOException
	{
		throw new IOException("we should never get called");
	}

	public void handleReadEvent( SelectableChannel channel ) throws IOException
	{
		//System.out.println("ChatHandler: received read event");
		SocketChannel socketChannel = (SocketChannel) channel;
		this.readBuffer.clear( );

		int numRead;
		try {
			numRead = socketChannel.read( this.readBuffer );
		} catch (IOException e) {
			System.out.println( "Client forcibly shut down the socket" );
			channel.keyFor(this.reactor.selector).cancel( );
			socketChannel.close( );
			return;
		}
		if( numRead == -1 )
		{
			channel.close( );
			channel.keyFor(this.reactor.selector).cancel( );
			socketChannel.close( );
			//System.out.println( "Client shut the socket down cleanly" );
			return;
		}

		byte[] dataCopy = new byte[numRead];
		byte[] data = this.readBuffer.array( );
		System.arraycopy( data, 0, dataCopy, 0, numRead );
		//System.out.println( new String(dataCopy) );

		parseMessage( (SocketChannel)channel, new String(dataCopy) );
	}

	public void handleWriteEvent(SelectableChannel channel) throws IOException {
		//System.out.println("ChatHandler: received write event");
		SocketChannel socketChannel = (SocketChannel) channel;
		while( !queue.isEmpty() )
		{
			ByteBuffer buf = (ByteBuffer) queue.get( 0 );
			//System.out.print( new String(buf.array()) );
			socketChannel.write( buf );
			if( buf.remaining() > 0 )
			{
				break;
			}
			queue.remove( 0 );
		}

		if( queue.isEmpty() )
		{
			setReadOps( );
		}
	}

	public synchronized void parseMessage( SocketChannel aChannel, String aMessage )
	{
//		int i = dClients.indexOf( aClientSocket );
		String command = "";
		String option = "";
		String message = "";
		StringTokenizer tokenizer = new StringTokenizer( aMessage );

		if( tokenizer.hasMoreTokens() )
			command = tokenizer.nextToken( );

		if( tokenizer.hasMoreTokens() )
			option += tokenizer.nextToken( );
		while( tokenizer.hasMoreTokens() )
			option += " " + tokenizer.nextToken( );

		if( isUserAuth( aChannel ) )
		{
			if( command.equals("USERS") )
			{
				message = "SERVER: " + command + ": " + ReactorUser.getAllNames() + "\n";
				//System.out.printf("reg:%2d all:%2d # %s/%d # %s: Userlist requested\n", anzRegistered, dUsers.size(), aClientSocket.getInetAddress().getHostAddress(), aClientSocket.getPort(), (String)dUsers.get(i));
			}
			else if( command.equals("PRIVMSG") )
			{
				message = "SERVER: " + command + ": " + option + "\n";
				sendGroupMessage( aChannel, option + "\n" );
				//System.out.printf("reg:%2d all:%2d # %s/%d # %s: Message '%s'\n", anzRegistered, dUsers.size(), aClientSocket.getInetAddress().getHostAddress(), aClientSocket.getPort(), (String)dUsers.get(i), option);
			}
			else if( command.equals("QUIT") )
			{
				ReactorUser.del( aChannel );
				message = "SERVER: " + command + "\n";
//->				reactor.removeEventHandler();
				//System.out.printf("reg:%2d all:%2d # %s/%d # %s: Quit from Chat\n", anzRegistered, dUsers.size(), aClientSocket.getInetAddress().getHostAddress(), aClientSocket.getPort(), (String)dUsers.get(i));
			}
			else
			{
				message = "SERVER: Unknown Command: " + command + "\n";
				//System.out.printf("reg:%2d all:%2d # %s/%d # %s: Unknown Command '%s'\n", anzRegistered, dUsers.size(), aClientSocket.getInetAddress().getHostAddress(), aClientSocket.getPort(), (String)dUsers.get(i), command);
			}
		}
		else 
		{
			if( command.equals("USER") )
			{
				ReactorUser.setName( aChannel, option );
				message = "SERVER: USER " + option + "\n";
				//System.out.printf("reg:%2d all:%2d # %s/%d # %s: User '%s' added\n", anzRegistered, dUsers.size(), aClientSocket.getInetAddress().getHostAddress(), aClientSocket.getPort(), (String)dUsers.get(i), option);
			}
			else
				message =  "SERVER: You have to log in first\n";
		}
		sendMessage( aChannel, message );
	}

	public void sendMessage( SocketChannel aChannel, String aMessage )
	{
		Set<EventHandler> handlers = reactor.getEventHandlers( );
		for( EventHandler handler : handlers )
		{
			if( handler instanceof ChatHandler )
			{
				if( handler.getChannel() == aChannel )
				{
					ChatHandler chatHandler = (ChatHandler) handler;
					chatHandler.addToWriteQueue( aMessage.getBytes() );
					chatHandler.setWriteOps( );
				}
			}
		}
	}

	public void sendGroupMessage( SocketChannel aChannel, String aMessage )
	{
		Set<EventHandler> handlers = reactor.getEventHandlers( );
		for( EventHandler handler : handlers )
		{
			if( handler instanceof ChatHandler )
			{
				if( handler.getChannel() != aChannel )
				{
					ChatHandler chatHandler = (ChatHandler) handler;
					chatHandler.addToWriteQueue( aMessage.getBytes() );
					chatHandler.setWriteOps( );
				}
			}
		}
	}

	public boolean isUserAuth( SocketChannel aChannel )
	{
		if( ReactorUser.getName( aChannel) != "noname" )
			return true;
		else
			return false;
	}

	public void addToWriteQueue( byte[] bytes )
	{
		queue.add( ByteBuffer.wrap(bytes) );
	}

	public void setReadOps( )
	{
		SelectionKey key = socketChannel.keyFor( this.reactor.selector );
		if( key != null && key.isValid() )
			key.interestOps( SelectionKey.OP_READ );
	}

	public void setWriteOps()
	{
		SelectionKey key = socketChannel.keyFor( this.reactor.selector );
		if( key != null && key.isValid() )
			key.interestOps( SelectionKey.OP_WRITE );
	}

	public SelectableChannel getChannel( )
	{
		return socketChannel;
	}
}
