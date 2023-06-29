import java.io.*;
import java.nio.*;

/**
 * Class responsible for writing and reading header.
 * 
 * @author Louis Hogge
 */
public class HeaderClient{

	// instance variable
	private Short ID;

	/**
     * Constructor
     * 
     * @param ID id of query in the form of a Short
     */
	public HeaderClient(/*Short ID*/){
		//this.ID = ID;
	}

	/**
     * Method to write a header.
     * 
     * @return header in the form of a byte array
     */
	public byte[] writeHeaderClient(Short clientID){

		short QR = 0; // 0
		short OPCODE = 0;
		short AA = 0;
		short TC = 0;
		short RD = 0;
		short RA = 0;
		short Z = 0;
		short RCODE = 0;

		short flags = (short) ((QR << 15) | (OPCODE << 11) | (AA << 10) | (TC << 9) | (RD << 8) | (RA << 7) | (Z << 4) | RCODE);

		// QDCOUNT, ANCOUNT, NSCOUNT and ARCOUNT
		short QDCOUNT = 1;
		short ANCOUNT = 0;
		short NSCOUNT = 0;
		short ARCOUNT = 0;

		ByteBuffer headerBB = ByteBuffer.allocate(12); // 6 shorts = 12 bytes

		// adding all the components to the header
		headerBB.putShort(clientID);
		headerBB.putShort(flags);
		headerBB.putShort(QDCOUNT);
		headerBB.putShort(ANCOUNT);
		headerBB.putShort(NSCOUNT);
		headerBB.putShort(ARCOUNT);

		// converting into byte array
		byte[] header = headerBB.array();

		return header;
	}
}
