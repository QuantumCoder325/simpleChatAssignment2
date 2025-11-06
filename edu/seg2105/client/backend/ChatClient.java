// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  
  /**
   * The loginID of this client.
   */
  String loginID; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, String loginID, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString(), "");
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
		if (message.startsWith("#")) {
			
			  switch(message) {
			  case "#quit":
				  this.quit();
				  break;
				  
			  case "#logoff":
				  try {
					  this.closeConnection();
				  } catch (IOException e) {
					  this.clientUI.display("Could not disconnect from server.", "");
				  }
				  break;
			
				  /*
			  case "#login":
				  if (!this.isConnected()) {
					try {
						this.openConnection();
					} catch (IOException e) {
						this.clientUI.display("Could not connect to server.");
					}
				  } else {
					  this.clientUI.display("Already logged in");
				  }
				  break;
				  */
				  
			  case "#gethost":
				  this.clientUI.display("The current host is " + this.getHost(), "");
				  break;
				  
			  case "#getport":
				  this.clientUI.display("The current port is " + this.getPort(), "");
				  break;
				  
			  default:
				  if (message.startsWith("#sethost")) {
					  
					  if (this.isConnected()) {
						  this.clientUI.display("Cannot set host while logged in.", "");
						  
					  } else {
						  
						  try {
							  this.setHost(message.split(" ")[1]);
						  } catch (ArrayIndexOutOfBoundsException arrayException) {
							  this.clientUI.display("Host needs to be specified.", "");
						  }
						  
					  }
					  
				  } else if (message.startsWith("#setport")) {
					  
					  if (this.isConnected()) {

						  this.clientUI.display("Cannot set port while logged in.", "");
						  
					  } else {

						  try {
							  this.setPort(Integer.parseInt(message.split(" ")[1]));
						  } catch (ArrayIndexOutOfBoundsException arrayException) {
							  this.clientUI.display("Port needs to be specified.", "");
						  } catch (NumberFormatException numException) {
							  this.clientUI.display("Port must be an integer.", "");
						  }
						  
					  }
					  
				  } else if (message.startsWith("#login")) {
					 
						try {
							
							if (!this.isConnected())
								this.openConnection();
							
							this.loginID = message.split(" ")[1];
							this.sendToServer(message);
							
						} catch (IOException e) {
							this.clientUI.display("Could not connect to login to server.", "");
						} catch (ArrayIndexOutOfBoundsException arrayException) {
							this.clientUI.display("ID must be specified.", "");
						}
				  
				  } else {
					  
					  this.clientUI.display("Unknown command", "");
					  
				  }
				  break;
			  }
			  
	  	  } else {
	  
		    try
		    {
		      sendToServer(message);
		    }
		    catch(IOException e)
		    {
		      clientUI.display
		        ("Could not send message to server.  Terminating client.", "");
		      quit();
		    }
		    
	  	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
    	if (this.isConnected()) {
    		closeConnection();
    	}
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
	/**
	 * Implements the hook method called after the connection has been closed. The default
	 * implementation does nothing. Tells the UI to display a message informing the user
	 * the connection has been closed gracefully.
	 */
  	@Override
	protected void connectionClosed() {
		this.clientUI.display("Connection closed.", "");
	}
  	
	/**
	 * Implements the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. Tells the UI to display a
	 * message informing the user the server has shut down.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  	@Override
	protected void connectionException(Exception exception) {
		this.clientUI.display("The server has shut down", "");
		System.exit(0);
	}
  	
	/**
	 * Hook method called after a connection has been established. Sends #login concatenated
	 * with loginID to the connected server.
	 */
	protected void connectionEstablished() {
		try {
			this.sendToServer("#login " + this.loginID);
		} catch (IOException e) {
			this.clientUI.display("Could not automatically send loginID to server", "");
		}
	}
}
//End of ChatClient class
