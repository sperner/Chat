//Verteilte Anwendungen - Uebungsblatt 6
//Aufgabe 1a: RMI IRC Server / Subscriber
//
package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote Interface for a subscriber.
 */
public interface Subscriber extends Remote
{
	/**
	 * Notify
	 * 
	 * @exception RemoteException
	 *                (if the remote invocation fails).
	 */
	public void notify(String message) throws RemoteException;

	/**
	 * Get Name of subscriber
	 * 
	 * @return name
	 * @exception RemoteException
	 *                (if the remote invocation fails).
	 */
	public String getName() throws RemoteException;

	/**
	 * Get UID of subscriber
	 * 
	 * @return uid
	 * @exception RemoteException
	 *                (if the remote invocation fails).
	 */
	public long getSVUID() throws RemoteException;

}
