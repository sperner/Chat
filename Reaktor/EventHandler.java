//Verteilte Anwendungen - Uebungsblatt 4
//zu Aufgabe 1b: EventHandler-Interface für TCP IRC Reactor-Server
//
import java.io.IOException;
import java.nio.channels.SelectableChannel;

public interface EventHandler
{
	/**
	 * Handle accept event.  
	 * @param channel
	 * @throws IOException if unable to handle 
	 */
	public void handleAcceptEvent(SelectableChannel channel) throws IOException;
	 
	/**
	 * Handle read event.
	 * @param channel
	 * @throws IOException if unable to handle
	 */
	public void handleReadEvent(SelectableChannel channel) throws IOException;

	/**
	 * Handle write event.
	 * @param channel
	 * @throws IOException if unable to handle
	 */
	public void handleWriteEvent(SelectableChannel channel) throws IOException;
	
	/**
	 * Return underlying handle we use
	 * @return the underlying handle
	 */
	public SelectableChannel getChannel();
}
