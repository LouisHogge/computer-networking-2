import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.lang.*;

/**
 * Class responsible for writing resource records.
 * 
 * @author Louis Hogge and Juliette Waltregny
 */
public class ResourceRecord{

	// constructor
	public ResourceRecord(){
	}

	/**
     * Method to write an answer's resource records.
     * 
     * @param base32QNAME domain name encoded in base 32 in the form of a 
     * 	String
     * @param httpResponse HTTP response in the form of a String
     * 
     * @see Base32.java
     * @see decode(String)
     * 
     * @return answer's resource records in the form of a byte array
     */
	public byte[] writeResourceRecord(String base32QNAME, String httpResponse){

		// NAME part
		// decode base32
        String coded_url = base32QNAME.replaceAll("=", ""); // handle padding
        String decoded_url = new String(Base32.decode(coded_url), StandardCharsets.UTF_8);

        // decoded_url : splitting the domain name in parts thanks to the dots
		String[] domainNameSplit = decoded_url.split("\\.");
		int nbOfSplit = domainNameSplit.length;
		byte[][] domainNameSplitBytes = new byte[nbOfSplit][];
		int nbOfBytes = 0;

		// converting each part of the domain name into byte array
		for (int i = 0; i < nbOfSplit; i++) {
		    domainNameSplitBytes[i] = domainNameSplit[i].getBytes(StandardCharsets.UTF_8);
		    nbOfBytes += domainNameSplitBytes[i].length;
		}

		// RDATA part
		byte[] httpResponseBytes = Base64.getDecoder().decode(httpResponse);
		int httpResponseBytesLength = httpResponseBytes.length;

		int httpResponsePart = (int) Math.ceil((double)httpResponseBytesLength / 255);

		// creating resource records byte buffer
		// +1 --> byte 0, +2 --> TYPE, +2 --> CLASS, +4 --> TTL, +2 --> RDLENGTH ==> +11
		ByteBuffer resourceRecordBB = ByteBuffer.allocate(nbOfBytes+nbOfSplit+httpResponsePart+httpResponseBytesLength+11);

		// NAME
		// adding the length of each part and adding each part itself to the resource records
		for (int i = 0; i < nbOfSplit; i++) {
			Integer lengthInOneByte = Integer.valueOf(domainNameSplitBytes[i].length);
			resourceRecordBB.put(lengthInOneByte.byteValue());
		    resourceRecordBB.put(domainNameSplitBytes[i]);
		}
		resourceRecordBB.put((byte)0);

		// TYPE : adding the type of query to the question
		short TYPE = 16; // TXT
		resourceRecordBB.putShort(TYPE);

		// CLASS : adding the CLASS to the question
		short CLASS = 1; // IN
		resourceRecordBB.putShort(CLASS);

		// TTL : adding the TTL to the question
		int TTL = 0; // no need for caching
		resourceRecordBB.putInt(TTL);

		// RDLENGTH : adding the RDLENGTH to the question
		short RDLENGTH = (short)(httpResponseBytesLength + httpResponsePart);
		resourceRecordBB.putShort(RDLENGTH);

		// RDATA : adding the RDATA to the question 255 bytes per 255 bytes (last >255 bytes part properly handled)
		for (int i = 0; i < httpResponsePart; i++) {
			
			int limit = 255;
			if (i == httpResponsePart - 1)
				limit = httpResponseBytesLength - ((httpResponsePart-1) * 255);
			
			Integer lengthInOneByte = Integer.valueOf(limit);
			resourceRecordBB.put(lengthInOneByte.byteValue());

		    resourceRecordBB.put(httpResponseBytes, (i*255), limit);
		}

		// converting into byte array
		byte[] resourceRecord = resourceRecordBB.array();

		return resourceRecord;
	}
}
