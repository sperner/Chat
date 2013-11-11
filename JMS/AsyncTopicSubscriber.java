import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
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

public class AsyncTopicSubscriber {

	public static void main(String[] args) {
		Context jndiContext = null;
		TopicConnectionFactory topicConnectionFactory = null;
		TopicConnection topicConnection = null;
		TopicSession topicSession = null;
		Topic topic = null;
		TopicSubscriber topicSubscriber = null;

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
			final Thread thread = Thread.currentThread();
			MessageListener listener = new MessageListener() {
				public void onMessage(Message message) {
					if (message instanceof TextMessage) {
						try {
							System.out.println("Reading message: "
									+ ((TextMessage) message).getText());
						} catch (JMSException e) {
							System.out.println("Error: " + e.getMessage());
						}
					} else {
						// any non text message signals exit ...
						System.out.println("Exiting...");
						synchronized (thread) {
							thread.notify();
						}
					}
				}
			};
			topicSubscriber.setMessageListener(listener);
			System.out.println("Non blocking, so we can do something now...");
			try {
				synchronized (thread) {
					thread.wait();
				}
			} catch (InterruptedException ignore) {
			}
			System.out.println("Any message not received now is gone");
		} catch (JMSException e) {
			System.out.println("Exception occurred: " + e.toString());
		} finally {
			if (topicConnection != null) {
				try {
					topicConnection.close();
				} catch (JMSException e) {
				}
			}
		}
		// must call this as apparently OpenJM InitialCOntext is holding onto a
		// connection preventing this thread from exiting...
		System.exit(0);
	}
}
