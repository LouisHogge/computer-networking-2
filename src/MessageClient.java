import java.nio.*;
import java.util.*;

/**
 * Class responsible for writing and reading message.
 * 
 * @author Louis Hogge
 * 
 * @see Header.java
 * @see Question.java
 * @see ResourceRecord.java
 */
public class MessageClient{

	// instance variables
	// private int headerBytesLength = 12;
	// private int questionBytesLength;
	HeaderClient header;
	QuestionClient question;
	//ResourceRecord resourceRecord;

	/**
     * Constructor
     * 
     * @param ID id of query in the form of a Short
     * 
     * @see Header.java
     * @see Question.java
     */
	public MessageClient(/*Short ID*/){
		header = new HeaderClient(/*ID*/);
		question = new QuestionClient();
		//resourceRecord = new ResourceRecord();
	}

	/**
     * Method to write a message.
     * 
     * @param domainName domain name in the form of a String
     * @param questionTypeString question type in the form of a String
     * 
     * @see Header.java
     * @see writeHeader()
     * 
     * @see Question.java
     * @see writeQuestion()
     * 
     * @return message in the form of a byte array
     */
	public byte[] writeMessageClient(String domainName, String questionTypeString){

		// generate ID
		Random random = new Random();
		short ID = (short)random.nextInt(32767);

		// writing of the header
		byte[] headerBytes = header.writeHeaderClient(ID);
		int headerBytesLength = headerBytes.length;

		// writing of the question
		byte[] questionBytes = question.writeQuestionClient(domainName, questionTypeString);
		int questionBytesLength = questionBytes.length;

		// calculation of the message length
		Integer messageLength = Integer.valueOf(headerBytesLength + questionBytesLength);

		// concatenation of the message length, the header and the question
		ByteBuffer messageBB = ByteBuffer.allocate(2 + headerBytesLength + questionBytesLength);
		messageBB.putShort(messageLength.shortValue());
        messageBB.put(headerBytes);
        messageBB.put(questionBytes);
		
		// converting into byte array
		byte[] message = messageBB.array();

		return message;
	}
}