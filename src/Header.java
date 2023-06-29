// import java.io.*;
import java.nio.*;

/**
 * Class responsible for writing and reading header.
 * 
 * @author Louis Hogge and Juliette Waltregny
 */
public class Header{

	// instance variable
	private Short ID;

	// constructor
	public Header(){
	}

	/**
     * Method to write a header.
     * 
     * @param httpResponse HTTP response in the form of a String
     * 
     * @return header in the form of a byte array
     */
	public byte[] writeHeader(String httpResponse){

		short QR = 1; // 0
		short OPCODE = 0;
		short AA = 0;
		short TC = 0;
		if (httpResponse.length() >= 60000)
			TC = 1; // truncation, not sure if required
		short RD = 0;
		short RA = 0;
		short Z = 0;
		short RCODE = 0;
		if (httpResponse.length() >= 60000)
			RCODE = 3;

		short flags = (short) ((QR << 15) | (OPCODE << 11) | (AA << 10) | (TC << 9) | (RD << 8) | (RA << 7) | (Z << 4) | RCODE);

		// QDCOUNT, ANCOUNT, NSCOUNT and ARCOUNT
		short QDCOUNT = 1;
		short ANCOUNT = 1;
		short NSCOUNT = 0;
		short ARCOUNT = 0;

		ByteBuffer headerBB = ByteBuffer.allocate(12); // 6 shorts = 12 bytes

		// adding all the components to the header
		headerBB.putShort(ID);
		headerBB.putShort(flags);
		headerBB.putShort(QDCOUNT);
		headerBB.putShort(ANCOUNT);
		headerBB.putShort(NSCOUNT);
		headerBB.putShort(ARCOUNT);

		// converting into byte array
		byte[] header = headerBB.array();

		return header;
	}

	/**
     * Method to write a header with RCODE = 1.
     * 
     * @return header in the form of a byte array
     */
	public byte[] formatErrorHeader(){

		short QR = 1;
		short OPCODE = 0;
		short AA = 0;
		short TC = 0;
		short RD = 0;
		short RA = 0;
		short Z = 0;
		short RCODE = 1;

		short flags = (short) ((QR << 15) | (OPCODE << 11) | (AA << 10) | (TC << 9) | (RD << 8) | (RA << 7) | (Z << 4) | RCODE);

		// QDCOUNT, ANCOUNT, NSCOUNT and ARCOUNT
		short QDCOUNT = 1;
		short ANCOUNT = 0;
		short NSCOUNT = 0;
		short ARCOUNT = 0;

		ByteBuffer headerBB = ByteBuffer.allocate(12); // 6 shorts = 12 bytes

		// adding all the components to the header
		headerBB.putShort(ID);
		headerBB.putShort(flags);
		headerBB.putShort(QDCOUNT);
		headerBB.putShort(ANCOUNT);
		headerBB.putShort(NSCOUNT);
		headerBB.putShort(ARCOUNT);

		// converting into byte array
		byte[] header = headerBB.array();

		return header;
	}

	/**
     * Method to write a header with RCODE = 3.
     * 
     * @return header in the form of a byte array
     */
	public byte[] nameErrorHeader(){

		short QR = 1;
		short OPCODE = 0;
		short AA = 0;
		short TC = 0;
		short RD = 0;
		short RA = 0;
		short Z = 0;
		short RCODE = 3;

		short flags = (short) ((QR << 15) | (OPCODE << 11) | (AA << 10) | (TC << 9) | (RD << 8) | (RA << 7) | (Z << 4) | RCODE);

		// QDCOUNT, ANCOUNT, NSCOUNT and ARCOUNT
		short QDCOUNT = 1;
		short ANCOUNT = 0;
		short NSCOUNT = 0;
		short ARCOUNT = 0;

		ByteBuffer headerBB = ByteBuffer.allocate(12); // 6 shorts = 12 bytes

		// adding all the components to the header
		headerBB.putShort(ID);
		headerBB.putShort(flags);
		headerBB.putShort(QDCOUNT);
		headerBB.putShort(ANCOUNT);
		headerBB.putShort(NSCOUNT);
		headerBB.putShort(ARCOUNT);

		// converting into byte array
		byte[] header = headerBB.array();

		return header;
	}

	/**
     * Method to write a header with RCODE = 5.
     * 
     * @return header in the form of a byte array
     */
	public byte[] refusedHeader(){

		short QR = 1;
		short OPCODE = 0;
		short AA = 0;
		short TC = 0;
		short RD = 0;
		short RA = 0;
		short Z = 0;
		short RCODE = 5;

		short flags = (short) ((QR << 15) | (OPCODE << 11) | (AA << 10) | (TC << 9) | (RD << 8) | (RA << 7) | (Z << 4) | RCODE);


		// QDCOUNT, ANCOUNT, NSCOUNT and ARCOUNT
		short QDCOUNT = 1;
		short ANCOUNT = 0;
		short NSCOUNT = 0;
		short ARCOUNT = 0;

		ByteBuffer headerBB = ByteBuffer.allocate(12); // 6 shorts = 12 bytes

		// adding all the components to the header
		headerBB.putShort(ID);
		headerBB.putShort(flags);
		headerBB.putShort(QDCOUNT);
		headerBB.putShort(ANCOUNT);
		headerBB.putShort(NSCOUNT);
		headerBB.putShort(ARCOUNT);

		// converting into byte array
		byte[] header = headerBB.array();

		return header;
	}

	/**
     * Method to read a header.
     * 
     * @param headerBB query header to decode in the form of a ByteBuffer
     * 
     * @return errorCode, here: 0, 1 or 5 
     */
	public int readHeader(ByteBuffer headerBB) {

		// 0 = no error
		int errorCode = 0;

		// A 16 bit identifier
		ID = headerBB.getShort();

		// flags
		short flags = headerBB.get();

		// A one bit field that specifies whether this message is a query (0), or a response (1).
		int QR = (flags & 0b10000000) >>> 7;

		// message should not be a response
		if (QR == 1)
			errorCode = 1;

		// A four bit field that specifies kind of query in this message.
		int opCode = (flags & 0b01111000) >>> 3;

		// Authoritative Answer - this bit is valid in responses, and specifies that the responding name server is an authority for the domain name in question section.
		int AA = (flags & 0b00000100) >>> 2;

		// message should not be a response
		if (AA == 1)
			errorCode = 1;

		// TrunCation - specifies that this message was truncated due to length greater than that permitted on the transmission channel.
		int TC = (flags & 0b00000010) >>> 1;

		// Recursion Desired - this bit may be set in a query and is copied into the response.  If RD is set, it directs the name server to pursue the query recursively. Recursive query support is optional.
		int RD = flags & 0b00000001;

		flags = headerBB.get();

		// Recursion Available - this be is set or cleared in a response, and denotes whether recursive query support is available in the name server.
		int RA = (flags & 0b10000000) >>> 7;
		// message should not be a response
		if (RA == 1)
			errorCode = 1;

		// Reserved for future use.  Must be zero in all queries and responses.
		int Z = (flags & 0b01110000) >>> 4;

		// must be 0 in all queries and all responses
		if (Z > 0)
			errorCode = 1;
		// Response code - this 4 bit field is set as part of responses.
		int RCODE = flags & 0b00001111;

		// message should not be a response
		if (RCODE > 0)
			errorCode = 1;

		// an unsigned 16 bit integer specifying the number of entries in the question section.
		short QDCOUNT = headerBB.getShort();

		// message should contain a question
		if (QDCOUNT == 0)
			errorCode = 1;
		// Check that the DNS query contains only one question
		else if (QDCOUNT > 1)
			errorCode = 5;

		// an unsigned 16 bit integer specifying the number of resource records in the answer section.
		short ANCOUNT = headerBB.getShort();

		// message should not be a response
		if (ANCOUNT != 0)
			errorCode = 1;

		// an unsigned 16 bit integer specifying the number of name server resource records in the authority records section.
		short NSCOUNT = headerBB.getShort();

		// message should not be a response
		if (NSCOUNT != 0)
			errorCode = 1;

		// an unsigned 16 bit integer specifying the number of resource records in the additional records section.
		short ARCOUNT = headerBB.getShort();

		// message should not be a response
		if (ARCOUNT != 0)
			errorCode = 1;

    	return errorCode;
	}
}
