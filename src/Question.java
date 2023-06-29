import java.io.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;

/**
 * Class responsible for writing and reading question.
 * 
 * @author Louis Hogge and Juliette Waltregny
 */
public class Question{

	// instance variables
	private String QNAME;
	private short QTYPE;
	private short QCLASS;

	// constructor
	public Question(){
	}

	/**
     * Method to write a question.
     * 
     * @return question in the form of a byte array
     */
	public byte[] writeQuestion (){

		// QNAME : splitting the domain name in parts thanks to the dots
		String[] domainNameSplit = QNAME.split("\\.");
		int nbOfSplit = domainNameSplit.length;
		byte[][] domainNameSplitBytes = new byte[nbOfSplit][];
		int nbOfBytes = 0;

		// converting each part of the domain name into byte array
		for (int i = 0; i < nbOfSplit; i++) {
		    domainNameSplitBytes[i] = domainNameSplit[i].getBytes(StandardCharsets.UTF_8);
		    nbOfBytes += domainNameSplitBytes[i].length;
		}

		// +1 --> byte 0, +2 --> QTYPE, +2 --> QCLASS ==> +5
		ByteBuffer questionBB = ByteBuffer.allocate(nbOfBytes+nbOfSplit+5);

		// adding the length of each part and adding each part itself to the question
		for (int i = 0; i < nbOfSplit; i++) {
			Integer lengthInOneByte = Integer.valueOf(domainNameSplitBytes[i].length);
			questionBB.put(lengthInOneByte.byteValue());
		    questionBB.put(domainNameSplitBytes[i]);
		}
		questionBB.put((byte)0);

		// QTYPE : adding the type of query to the question
		questionBB.putShort(QTYPE);

		// QCLASS : adding the QCLASS to the question
		questionBB.putShort(QCLASS);

		// converting into byte array
		byte[] question = questionBB.array();

		return question;
	}

	/**
     * Method to read a question.
     * 
     * @param questionBB query question to decode in the form of a ByteBuffer
     * 
     * @return NAME, TYPE and error code of query in the form of a 
     * 	NameTypeErrorCode object
     */
	public NameTypeErrorCode readQuestion(ByteBuffer questionBB) {

		int errorCode = 0;

		// QNAME: getting each part of the domain name into byte array
		QNAME = "";
		int splitLength;
		while ((splitLength = questionBB.get()) > 0) {
		    byte[] split = new byte[splitLength];
		    for (int i = 0; i < splitLength; i++) {
		        split[i] = questionBB.get();
		    }
		    // converting byte array into String
		    String QNAMEPart = new String(split, StandardCharsets.UTF_8);
		    QNAME += QNAMEPart;
		    QNAME += ".";
		}
		QNAME = QNAME.substring(0, QNAME.length() - 1);

		// QTYPE
		QTYPE = questionBB.getShort();

		// QCLASS
		QCLASS = questionBB.getShort();

		// Check that the DNS query contains a question of type TXT
		if (QTYPE != 16)
			errorCode = 5;

		// converting QTYPE from short to String
		String type = "Unknown type";
		if (QTYPE == 1)
			type = "A";
		else if (QTYPE == 2)
			type = "NS";
		else if (QTYPE == 3)
			type = "MD";
		else if (QTYPE == 4)
			type = "MF";
		else if (QTYPE == 5)
			type = "CNAME";
		else if (QTYPE == 6)
			type = "SOA";
		else if (QTYPE == 7)
			type = "MB";
		else if (QTYPE == 8)
			type = "MG";
		else if (QTYPE == 9)
			type = "MR";
		else if (QTYPE == 10)
			type = "NULL";
		else if (QTYPE == 11)
			type = "WKS";
		else if (QTYPE == 12)
			type = "PTR";
		else if (QTYPE == 13)
			type = "HINFO";
		else if (QTYPE == 14)
			type = "MINFO";
		else if (QTYPE == 15)
			type = "MX";
		else if (QTYPE == 16)
			type = "TXT";
		else if (QTYPE == 252)
			type = "AXFR";
		else if (QTYPE == 253)
			type = "MAILB";
		else if (QTYPE == 254)
			type = "MAILA";
		else if (QTYPE == 255)
			type = "*";

		NameTypeErrorCode nameTypeErrorCode = new NameTypeErrorCode(QNAME, type, errorCode);

		return nameTypeErrorCode;
	}
}
