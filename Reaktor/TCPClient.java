//Verteilte Anwendungen - Uebungsblatt 4
//Aufgabe 1b: TCP IRC Reactor-Client
//
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	public static void main(String args[]) {
		try {
			if (args.length != 2) {
				System.out.println("Usage: TCPClient <hostname> <port>");
				System.exit(1);
			}
			String hostname = args[0];
			int port = Integer.parseInt(args[1]);

			final Socket aSocket = new Socket(hostname, port);
			System.out.println(aSocket.toString());
			final DataInputStream in = new DataInputStream(aSocket.getInputStream());

			Thread handler = new Thread(new Runnable() {
				public void run() {
					int c;
					try {
						while ((c = in.read()) != -1) {
							System.out.print((char) c);
						}
					} catch (IOException e) {
						if (!aSocket.isClosed()) {
							e.printStackTrace();
						}
					}
				}
			});
			handler.start();

			DataOutputStream out = new DataOutputStream( aSocket.getOutputStream() );
			BufferedReader br = new BufferedReader( new InputStreamReader(System.in) );

			String message = null;
			boolean on = true;
			try {
				while( on == true )
				{
					message = br.readLine();
					if( message.equals( "QUIT" ) )
						on = false;
					//System.out.println("Sending: " + message + " from " + aSocket.getLocalAddress() + ":" + aSocket.getLocalPort());
					//out.write((id + ":" + message + "\n").getBytes());
					out.write((message + "\n").getBytes());
				}
				aSocket.close();
			} catch (IOException e) {
				// won't happen too often from the keyboard
				e.printStackTrace();
			}

		} catch (UnknownHostException e) {
			System.out.println("Sock:" + e.getMessage());
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO:" + e.getMessage());
		}
	}
}
