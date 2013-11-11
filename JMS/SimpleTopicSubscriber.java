import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SimpleTopicSubscriber {

	public static void main(String[] args) {
		Context jndiContext = null;
		TopicConnectionFactory topicConnectionFactory = null;
		TopicConnection topicConnection = null;
		TopicSession topicSession = null;
		Topic topic = null;
		TopicSubscriber topicSubscriber = null;
		TextMessage message = null;

		/*
		 * Read topic name from command line and display it.
		 */
		if (args.length != 1) {
			System.out.println("Usage: java "
					+ "SimpleTopicSubscriber <topic-name>");
			System.exit(1);
		}
		String topicName = new String(args[0]);
		System.out.println("Topic name is " + topicName);

		/*
		 * Create a JNDI API InitialContext object if none exists yet.
		 */
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.exolab.jms.jndi.InitialContextFactory");
		props.put(Context.PROVIDER_URL, "tcp://localhost:3035/");

		try {
			jndiContext = new InitialContext(props);
		} catch (NamingException e) {
			System.out.println("Could not create JNDI API " + "context: "
					+ e.toString());
			System.exit(1);
		}

		/*
		 * Look up connection factory and topic. If either does not exist, exit.
		 */
		try {
			topicConnectionFactory = (TopicConnectionFactory) jndiContext
					.lookup("ConnectionFactory");
			topic = (Topic) jndiContext.lookup(topicName);
		} catch (NamingException e) {
			System.out.println("JNDI API lookup failed: " + e.toString());
			System.exit(1);
		}

		/* Create connection. */
		try {
			topicConnection = topicConnectionFactory.createTopicConnection();
			topicSession = topicConnection.createTopicSession(false,
					Session.AUTO_ACKNOWLEDGE);
			topicSubscriber = topicSession.createSubscriber(topic);
			topicConnection.start();
			while (true) {
				System.out.println("Waiting for message for " + topicName
						+ " topic");
				Message m = topicSubscriber.receive();
				System.out.println("Received message for " + topicName
						+ " topic");
				if (m != null) {
					if (m instanceof TextMessage) {
						message = (TextMessage) m;
						System.out.println("Reading message: "
								+ message.getText());
					} else {
						System.out.println("Received non text msg, exiting");
						break;
					}
				}
			}
		} catch (JMSException e) {
			System.out.println("Exception occurred: " + e.toString());
		} finally {
			if (topicConnection != null) {
				try {
					topicConnection.close();
					System.out.println("Connection closed");
				} catch (JMSException e) {
				}
			}
		}
		System.exit(0);
	}
}
