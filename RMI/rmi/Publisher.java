//Verteilte Anwendungen - Uebungsblatt 6
//Aufgabe 1a: RMI IRC Server / Publisher
//
package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote Interface for a publisher.
 */
public interface Publisher extends Remote
{
	/**
	 * Subscribe a Subscriber
	 * 
	 * @return true if successful, false otherwise
	 * @exception RemoteException
	 *                (if the remote invocation fails).
	 */
	public boolean subscribe(Subscriber aSubscriber) throws RemoteException;
	public boolean unsubscribe(Subscriber aSubscriber) throws RemoteException;
	public void sendMessage(Subscriber aSubscriber, String aMessage) throws RemoteException;
	public void sendGroupMessage(Subscriber aSubscriber, String aMessage) throws RemoteException;
	public void sendAdminInfo(String aMessage) throws RemoteException;
	public boolean isUserAuth(Subscriber aSubscriber) throws RemoteException;

	public void cmdUSER(Subscriber aSubscriber, String sName, String aChannel) throws RemoteException;
	public void cmdUSERS(Subscriber aSubscriber) throws RemoteException;
	public void cmdPRIVMSG(Subscriber aSubscriber, String aMessage) throws RemoteException;
	public void cmdQUIT(Subscriber aSubscriber) throws RemoteException;
	public void cmdLIST(Subscriber aSubscriber) throws RemoteException;
	public void cmdJOIN(Subscriber aSubscriber, String aChannel) throws RemoteException;

}
