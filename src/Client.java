import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

/**
 * Main class responsible for sending question and receiving answer.
 * 
 * @author Louis Hogge
 * 
 * @see Message.java
 */
public class Client{

	/**
     * Main method to handle tcp connection, send question and receive answer.
     * 
     * @param args input data in the form of a String
     */
	public static void main(String[] args) throws IOException{

		// **************
		// * query part *
		// **************

		// input data
		if (args.length < 2){
			System.err.println("Error: Not enough arguments");
			System.exit(-1);
		}
	    String nameServerIP = args[0];
	    String domainName = args[1];
	    String questionType = "A";
	    if (args.length >= 3)
	    	questionType = args[2];
	   	int defaultDNSPort = 53; // 53

	   	// if (!(questionType.equals("A")) && !(questionType.equals("TXT"))){
		// 	System.err.println("Error: Question type not handled");
		// 	System.exit(-1);
		// }

		// initiate a new TCP connection with a Socket
		Socket socket = new Socket(nameServerIP, defaultDNSPort);
		socket.setTcpNoDelay(true); // disable Nagle’s algorithm
		socket.setSoTimeout(5000); // timeOut 5sec
		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();

		// // generate ID
		// Random random = new Random();
		// short ID = (short)random.nextInt(32767);

		// write message
		MessageClient message = new MessageClient(/*ID*/);
		byte[] query = message.writeMessageClient(/*domainName*/Base32.encode(domainName.getBytes(StandardCharsets.UTF_8)) + ".tnl.test", questionType);

		// writing Question to stdout
		System.out.println("\nQuestion (NS=" + nameServerIP + ", NAME=" + domainName + ", TYPE=" + questionType + ")");

		System.out.println("\nclientQuery =");
		for(int i=0; i< query.length ; i++) {
	         System.out.print(query[i] +" ");
	    }
	    System.out.println("\n");

		// Send a query in the form of a byte array
		out.write(query);
		out.flush();

		
		// *****************
		// * response part *
		// *****************

		// Retrieve the response length, as described in RFC 1035 (4.2.2 TCP usage)
		byte[] lengthBuffer = new byte[2];
		in.read(lengthBuffer); // Verify it returns 2

		// Convert bytes to length (data sent over the network is always big-endian)
		int length = ((lengthBuffer[0] & 0xff) << 8) | (lengthBuffer[1] & 0xff);

		// Retrieve the full response
		byte[] response = new byte[length];
		in.read(response); // Verify it returns the value of "length"

		System.out.println("\nresponse = " + response);
		System.out.println("\n");

		System.out.println(new String(response));

		// reading the response message
		// message.readMessage(response);
		

		// closing input and output streams and socket
		out.close();
        in.close();
        socket.close();
	}
}
