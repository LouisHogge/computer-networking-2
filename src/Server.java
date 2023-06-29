import java.net.*;
import java.io.*;

/**
 * Main class responsible handling incoming client  
 * 	queries.
 * 
 * @author Louis Hogge and Juliette Waltregny
 * 
 * @see RunnableClient
 */
public class Server {

	/**
     * Main method to start threads that handle incoming client queries.
     * 
     * @param args input data in the form of a String
     */
	public static void main(String[] args) throws IOException { 

		String ownedDomain = args[0];
		
		ServerSocket serverSocket = new ServerSocket(53); // 53

		// server run continuously until it receives an interruption signal (SIG- INT)
		while (true) {

			// handle client connections
			Socket clientSocket = serverSocket.accept();

			// new runnable for each client connection
			Runnable clientRunnable = new RunnableClient(clientSocket, ownedDomain);

			// new thread for each client connection
            Thread clientThread = new Thread(clientRunnable);
            clientThread.start();
	    }
	}
}

/**
 * Runnable class responsible for receiving queries in threads and for sending 
 * 	responses.
 * 
 * @author Louis Hogge and Juliette Waltregny
 * 
 * @see Message.java
 * @see NameTypeErrorCode.java
 * @see RequestHTTP.java
 */
class RunnableClient implements Runnable {

	// instance variable
	private Socket clientSocket;
	private String ownedDomain;

	/**
     * Constructor
     * 
     * @param clientSocket socket from client
     * @param ownedDomain domain name handled by server in the form of a String
     */
	public RunnableClient(Socket clientSocket, String ownedDomain) {
		this.clientSocket = clientSocket;
		this.ownedDomain = ownedDomain;
	}


	/**
     * Overridden run method of a thread which receives queries and sends 
     * 	responses.
     * 
     * @see Message.java
     * @see readMessage(byte[])
     * @see writeMessage(String, String)
     * @see writeFormatError()
     * @see writeRefused()
     * @see writeNameError()
     * 
     * 
     * @see NameTypeErrorCode.java
     * @see getName()
     * @see getType()
     * @see getErrorCode()
     * 
     * @see RequestHTTP.java
     * @see makeHTTPrequest(String)
     */
	@Override
	public void run() {

		try{
			clientSocket.setSoTimeout(5000); // timeOut 5sec
		

			while (!clientSocket.isClosed()) {

		        // Get input and output streams
		        InputStream in = clientSocket.getInputStream();
		        OutputStream out = clientSocket.getOutputStream();

		        // Retrieve the query length, as described in RFC 1035 (4.2.2 TCP usage)
				byte[] lengthBuffer = new byte[2];
				int check = in.read(lengthBuffer); 

				// Convert bytes to length (data sent over the network is always big-endian)
				int length = ((lengthBuffer[0] & 0xff) << 8) | (lengthBuffer[1] & 0xff);

				// Retrieve the full query of the client
				byte[] clientQuery = new byte[length];
				check = in.read(clientQuery); 

				Message message = new Message();

				// read client query
				NameTypeErrorCode nameTypeErrorCode = message.readMessage(clientQuery);

				String QNAME = nameTypeErrorCode.getName();
				String type = nameTypeErrorCode.getType();
				int errorCodeMessage = nameTypeErrorCode.getErrorCode();

				// RCODE = 1 --> Format error
				if (errorCodeMessage == 1){

					// create a server response
					byte[] serverResponse = message.writeFormatError();

					// Send a response in the form of a byte array
					out.write(serverResponse);
					out.flush();

					// writing client query to stdout
					System.out.println("\nQuestion (CL=" + clientSocket.getRemoteSocketAddress()  + ", NAME=" + QNAME + ", TYPE=" + type + ") => " + errorCodeMessage);
					
					// closing input and output streams and socket
					out.close();
			        in.close();
					clientSocket.close();
					return;
				}
				// RCODE = 5 --> Refused
				else if (errorCodeMessage == 5){

					// create a server response
					byte[] serverResponse = message.writeRefused();

					// Send a response in the form of a byte array
					out.write(serverResponse);
					out.flush();

					// writing client query to stdout
					System.out.println("\nQuestion (CL=" + clientSocket.getRemoteSocketAddress()  + ", NAME=" + QNAME + ", TYPE=" + type + ") => " + errorCodeMessage);
					
					// closing input and output streams and socket
					out.close();
			        in.close();
					clientSocket.close();
					return;
				}

				// check if QNAME is a subdomain of ownedDomain, if not we close the connection
				if (!QNAME.endsWith(ownedDomain)){
					// RCODE = 5 --> Refused
					errorCodeMessage = 5;

					// create a server response
					byte[] serverResponse = message.writeRefused();

					// Send a response in the form of a byte array
					out.write(serverResponse);
					out.flush();

					// writing client query to stdout
					System.out.println("\nQuestion (CL=" + clientSocket.getRemoteSocketAddress()  + ", NAME=" + QNAME + ", TYPE=" + type + ") => " + errorCodeMessage);

					// closing input and output streams and socket
					out.close();
			        in.close();
					clientSocket.close();
					return;
				} 

				// isolating base32 part of domain name
				String urlBase32 = QNAME.replace(ownedDomain, "");
				urlBase32 = urlBase32.substring(0, urlBase32.length() - 1);

				RequestHTTP requesthttp = new RequestHTTP();
				String httpResponse = "";

				try{
					// http request
					httpResponse = requesthttp.makeHTTPrequest(urlBase32);
				} 
				// RCODE = 3 --> Name error
				catch (NameErrorException | UnknownHostException e ){

					// create a server response
					byte[] serverResponse = message.writeNameError();

					// Send a response in the form of a byte array
					out.write(serverResponse);
					out.flush();

					// writing client query to stdout
					System.out.println("\nQuestion (CL=" + clientSocket.getRemoteSocketAddress()  + ", NAME=" + QNAME + ", TYPE=" + type + ") => 3");
					
					// closing input and output streams and socket
					out.close();
			        in.close();
					clientSocket.close();
					return;
				}

				// create a server response
				byte[] serverResponse = message.writeMessage(urlBase32, httpResponse);

				// Send a response in the form of a byte array
				out.write(serverResponse);
				out.flush();

				// If the encoded HTTP response has been truncated, set response code (RCODE) to "Name Error"
				if (httpResponse.length() >= 60000)
					errorCodeMessage = 3;

				// writing client query to stdout
				System.out.println("\nQuestion (CL=" + clientSocket.getRemoteSocketAddress()  + ", NAME=" + QNAME + ", TYPE=" + type + ") => " + errorCodeMessage);

				// closing input and output streams and socket
				out.close();
		        in.close();
				clientSocket.close();
				return;
			}

	    } catch (IOException e){
			System.err.println(e);
		}
	}
}
