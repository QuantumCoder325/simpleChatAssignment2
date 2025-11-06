package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.*;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  
	this.serverUI.display("Message received: " + msg + " from " + client.getInfo("clientID"), "");
	if (msg != null)
		if (msg.getClass().equals(String.class)) {
			String message = (String) msg;
			if (message.startsWith("#login")) {
				
				if (client.getInfo("clientID") != null) {
					try {
						client.sendToClient("Cannot #login after already logged in. Terminating Connection.");
						client.close();
					} catch (IOException e) {
						this.serverUI.display("Could not close connection to client who tried to login twice.", "");
					}
					this.serverUI.display("Ended connection to client trying to login twice in same connection.", "");
					return;
				}
				
				client.setInfo("clientID", message.split(" ")[1]);
				this.serverUI.display(client.getInfo("clientID") + " has logged on.", "");
				return;
			}
		}
	if (client.getInfo("clientID") == null) {
		try {
			client.sendToClient("#login must be the first command recived after connection");
			client.close();
		} catch (IOException e) {
			this.serverUI.display("Ended connection to client with unspecified ID", "");
		}
		return;
	}
    
    this.sendToAllClients(client.getInfo("clientID") + "> " + msg);
  }
  
  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromServerUI(String message) {
		if (message.startsWith("#")) {
			
			  switch(message) {
			  case "#quit":
				  this.quit();
				  break;
				  
			  case "#stop":
				  this.stopListening();;
				  break;
				  
			  case "#close":
				  try {
					this.close();
				} catch (IOException e) {
					this.serverUI.display("Exeption while closing server.", "");
				}
				  break;
				  
			  case "#start":
				  if (this.isListening()) {
					  this.serverUI.display("Already listening.", "");
				  } else {
					  try {
						  this.listen();
					  } catch (IOException e) {
						  this.serverUI.display("Failed to start listening", "");
					  }
				  }
				  break;
				  
			  case "#getport":
				  this.serverUI.display("The current port is " + this.getPort(), "");
				  break;
				  
			  default:
				  if (message.startsWith("#setport")) {
					  
					  if (this.isListening()) {

						  this.serverUI.display("Cannot set port while logged in.", "");
						  
					  } else {

						  try {
							  this.setPort(Integer.parseInt(message.split(" ")[1]));
						  } catch (ArrayIndexOutOfBoundsException arrayException) {
							  this.serverUI.display("Port needs to be specified.", "");
						  } catch (NumberFormatException numException) {
							  this.serverUI.display("Port must be an integer.", "");
						  }
						  
					  }
					  
				  } else {
					  
					  this.serverUI.display("Unknown command", "");
					  
				  }
				  break;
			  }
			  
	  	  } else {
	  
		    this.sendToAllClients("SERVER MESSAGE> " + message);
		    this.serverUI.display(message, "SERVER MESSAGE");
		    
	  	  }
  }
  
  private void quit() {
	  this.stopListening();
	  boolean connectionsClosed = true;
	  
	  for (ConnectionToClient client : this.getClientConnections()) {
		  try {
			  client.close();
		  } catch (IOException e) {
			  connectionsClosed = false;
			  this.serverUI.display("Failed to close client a connection.", "");
		  }
	  }
	  
	  if (connectionsClosed)
		  System.exit(0);
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    this.serverUI.display
      ("Server listening for connections on port " + getPort(), "");
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    this.serverUI.display
      ("Server has stopped listening for connections.", "");
  }
  
  
  //Class methods ***************************************************
  
	/**
	 * Implements the hook method called each time a client connects to this server.
	 * Prints a message indicating a client has connected.
	 * 
	 * @param client
	 *            the connection to client that has been created.
	 */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  this.serverUI.display("A new client has connected to the server.", "");
  }
  
	/**
	 * Implements the hook method called each time a client disconnects from this server.
	 * Prints a message indicating a client has disconnected.
	 * 
	 * @param client
	 *            the connection to client that needs to be removed.
	 */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  super.clientDisconnected(client);
	  if (client.getInfo("terminated") == null) {
		  this.serverUI.display(client.getInfo("clientID") + " has disconnected.", "");
	  } else {
		  this.serverUI.display(client.getInfo("clientID") + " was already disconnected "
		  		+ "as it's connection ran into an exception.", "");
	  }
  }

	/**
	 * Implements the hook method called each time an exception is thrown by a connection
	 * to client that is not ready to stop but stops anyways. Prints a message indicating
	 * a client has shut down.
	 * 
	 * @param client
	 *            the connection to client that shut down unexpectedly.
	 * @param exception
	 * 			  the exception raised.
	 */
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	  client.setInfo("terminated", true);
	  this.serverUI.display(client.getInfo("clientID") + " has disconnected unexpectedly.", "");
  }
}
//End of EchoServer class
