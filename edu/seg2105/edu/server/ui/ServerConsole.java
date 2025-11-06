package edu.seg2105.edu.server.ui;

import java.util.Scanner;

import edu.seg2105.client.common.*;
import edu.seg2105.edu.server.backend.EchoServer;

/**
 * This class constructs the UI for a echo server.  It implements the
 * chat interface in order to activate the display() method.
 */
public class ServerConsole implements ChatIF {

	
	//Class variables *************************************************
	
	/**
	 * The default port to connect on.
	 */
	final public static int DEFAULT_PORT = 5555;
	
	/**
	 * The instance of the server that created this ConsoleChat.
	 */
	EchoServer server;
	   
	/**
	 * Scanner to read from the console
	 */
	Scanner fromConsole; 

	//Constructors ****************************************************

	/**
	 * Constructs an instance of the ClientConsole UI.
	 *
	 */
	public ServerConsole(int port) {
		
		server = new EchoServer(port, this);
		fromConsole = new Scanner(System.in); 
		
	}
	
	//Instance methods ************************************************
	  
	  /**
	   * This method waits for input from the console.  Once it is 
	   * received, it sends it to the client's message handler.
	   */
	  public void accept() {
		  
	    try {

	      String message;

	      while (true) {
	        message = fromConsole.nextLine();
	        server.handleMessageFromServerUI(message);
	      }
	    } 
	    catch (Exception ex) {
	      System.out.println
	        ("Unexpected error while reading from console!" + ex);
	    }
	    
	  }
	
	
	
	
	
	
	@Override
	public void display(String message, String source) {
		
		if (source.equals("")) {
			System.out.println(message);
		} else {
			System.out.println(source + "> " + message);
		}

	}

	  /**
	   * This method is responsible for the creation of 
	   * the server instance (there is no UI in this phase).
	   *
	   * @param args[0] The port number to listen on.  Defaults to 5555 
	   *          if no argument is entered.
	   */
	public static void main(String[] args) {
		
		int port = 0; //Port to listen on

	    try {
	    	port = Integer.parseInt(args[0]); //Get port from command line
	    } catch(Throwable t) {
	      port = DEFAULT_PORT; //Set port to 5555
	    }
		
	    ServerConsole console= new ServerConsole(port);
	    
	    try {
	      console.server.listen(); //Start listening for connections
	    } 
	    catch (Exception ex) {
	      System.out.println("ERROR - Could not listen for clients!");
	    }
	    
	    console.accept();
	}
}