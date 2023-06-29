import java.io.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;

/**
 * Class responsible for writing and reading question.
 * 
 * @author Louis Hogge
 */
public class QuestionClient{

	// instance variables
	

	// constructor
	public QuestionClient(){
	}

	/**
     * Method to write a question.
     * 
     * @param domainName domain name in the form of a String
     * @param questionTypeString question type in the form of a String
     * 
     * @return question in the form of a byte array
     */
	public byte[] writeQuestionClient(String domainName, String questionTypeString){

		// QNAME : splitting the domain name in parts thanks to the dots
		String[] domainNameSplit = domainName.split("\\.");
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
		short questionType = -1;
		if (questionTypeString.equals("A"))
			questionType = 1;
		else if (questionTypeString.equals("TXT"))
			questionType = 16;
		questionBB.putShort(questionType);

		// QCLASS : adding the QCLASS to the question
		short clientQCLASS = 1;
		questionBB.putShort(clientQCLASS);

		// converting into byte array
		byte[] question = questionBB.array();

		return question;
	}
}
