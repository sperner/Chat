//Verteilte Anwendungen - Uebungsblatt 4
//zu Aufgabe 1b: EventHandler-Tabelle für TCP IRC Reactor-Server
//
import java.nio.channels.Channel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventHandlerTable
{

	private Map<CombinedKey, List<EventHandler>> eventHandlers = new HashMap<CombinedKey, List<EventHandler>>();

	private class CombinedKey 
	{
		public Channel channel;
		public int selectionKeyType;

		public CombinedKey( Channel channel, int selectionKeyType )
		{
			this.channel = channel;
			this.selectionKeyType = selectionKeyType;
		}

		public boolean equals( Object obj )
		{
			return (obj instanceof CombinedKey && channel.equals(((CombinedKey) obj).channel) && selectionKeyType == ((CombinedKey) obj).selectionKeyType);
		}

		public int hashCode()
		{
			return 31 * channel.hashCode() + selectionKeyType;
		}
	}

	public Set<EventHandler> getEventHandlers()
	{
		Set<EventHandler> handlers = new HashSet<EventHandler>( );
		for( List<EventHandler> list : eventHandlers.values() )
		{
			handlers.addAll(list);
		}
		return handlers;
	}

	public List<EventHandler> getEventHandlers( Channel channel, int selectionKeyType )
	{
		CombinedKey key = new CombinedKey( channel, selectionKeyType );
		List<EventHandler> handlers = this.eventHandlers.get( key );
		if (handlers == null)
		{
			handlers = new ArrayList<EventHandler>( );
			this.eventHandlers.put( key, handlers );
		}
		return handlers;
	}

	public List<EventHandler> getEventHandlers( SelectableChannel channel, SelectionKey key )
	{
		return getEventHandlers( channel, key.interestOps() );
	}

	public void add( EventHandler handler, int selectionKeyType )
	{
		List<EventHandler> handlers = getEventHandlers( handler.getChannel(), selectionKeyType );
		handlers.add( handler );
	}

	public void remove( EventHandler handler, int selectionKeyType )
	{
		List<EventHandler> handlers = getEventHandlers( handler.getChannel(), selectionKeyType );
		handlers.remove( handler );
	}

	public void cleanUp()
	{
		Iterator<CombinedKey> iterator = eventHandlers.keySet().iterator( );
		while( iterator.hasNext() ) 
		{
			CombinedKey combinedKey = iterator.next( );
			if( !combinedKey.channel.isOpen() )
				iterator.remove( );
		}
	}

}
