//Assuming mount dir is /Users/joerg/Documents/dev/workspace/irc/bin i.e. the java package "rmi" is in sub dir "rmi"
//otherwise replace with your mount point chosen

//Run the following on class files to generate stubs and skeletons (not  java 1.5 anymore)
rmic rmi.SubscriberImpl
rmic rmi.PublisherServant

//To run example: start rmiregistry by typing
rmiregistry
//at the command line

// Codebase
// 1.) Option: 	HTTP Server - if you have installed the separate ClassFileServer from the examples package, then you can run the http server as follows 
//				assuming that your classes are located in /Users/joerg/Documents/dev/workspace/irc/bin:
//				Start simple http server:
				java examples.classServer.ClassFileServer 2001 /Users/joerg/Documents/dev/workspace/irc/bin
//				and 
//				use -Djava.rmi.server.codebase=http://localhost:2001/ 
//				Remark: The trailing slash "/" is important!
// 2.) Option: 	Local File System:
//				use -Djava.rmi.server.codebase=-Djava.rmi.server.codebase=file:///Users/joerg/Documents/dev/workspace/irc/bin/
//
// 3.) Option: 	No risk, no fun: Do not use code base at all (not recommended)! This will work only iff rmiregistry has been started from mount dir 
//				so that all necessary classes are in classpath of rmiregistry! 

//Start server
Option 1.) java -Djava.security.policy=rmi/SecurityPolicy.txt -Djava.rmi.server.codebase=http://localhost:2001/ rmi.Server
Option 2.) java -Djava.security.policy=rmi/SecurityPolicy.txt -Djava.rmi.server.codebase=file:///Users/joerg/Documents/dev/workspace/irc/bin/ rmi.Server
Option 3.) java -Djava.security.policy=rmi/SecurityPolicy.txt rmi.Server

//Start client
Option 1.) java -Djava.security.policy=rmi/SecurityPolicy.txt -Djava.rmi.server.codebase=http://localhost:2001/ rmi.Client karl
Option 2.) java -Djava.security.policy=rmi/SecurityPolicy.txt -Djava.rmi.server.codebase=file:///Users/joerg/Documents/dev/workspace/irc/bin/ rmi.Client karl
Option 3.) java -Djava.security.policy=rmi/SecurityPolicy.txt rmi.Client karl