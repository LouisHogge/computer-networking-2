import java.nio.*;
import java.util.*;

/**
 * Class responsible for writing and reading message.
 * 
 * @author Louis Hogge and Juliette Waltregny
 * 
 * @see Header.java
 * @see Question.java
 * @see ResourceRecord.java
 * @see NameTypeErrorCode.java
 */
public class Message{

	// instance variables
	Header header;
	Question question;
	ResourceRecord resourceRecord;

	/**
     * Constructor
     * 
     * @see Header.java
     * @see Question.java
     * @see ResourceRecord.java
     */
	public Message(){
		header = new Header();
		question = new Question();
		resourceRecord = new ResourceRecord();
	}

	/**
     * Method to write a message.
     * 
     * @param base32QNAME domain name encoded in base 32 in the form of a 
     * 	String
     * @param httpResponse HTTP response in the form of a String
     * 
     * @see Header.java
     * @see writeHeader()
     * 
     * @see Question.java
     * @see writeQuestion()
     * 
     * @see ResourceRecord.java
     * @see writeResourceRecord()
     * 
     * @return message in the form of a byte array
     */
	public byte[] writeMessage(String base32QNAME, String httpResponse){

		// writing of the header
		byte[] headerBytes = header.writeHeader(httpResponse);
		int headerBytesLength = headerBytes.length;

		// writing of the question
		byte[] questionBytes = question.writeQuestion();
		int questionBytesLength = questionBytes.length;

		// writing of the resouce records
		byte[] resourceRecordBytes = resourceRecord.writeResourceRecord(base32QNAME, httpResponse);
		int resourceRecordBytesLength = resourceRecordBytes.length;

		// calculation of the message length
		Integer messageLength = Integer.valueOf(headerBytesLength + questionBytesLength + resourceRecordBytesLength);

		// concatenation of the message length, the header and the question
		ByteBuffer messageBB = ByteBuffer.allocate(2 + headerBytesLength + questionBytesLength + resourceRecordBytesLength);
		messageBB.putShort(messageLength.shortValue());
        messageBB.put(headerBytes);
        messageBB.put(questionBytes);
        messageBB.put(resourceRecordBytes);
		
		// converting into byte array
		byte[] message = messageBB.array();

		return message;
	}

	/**
     * Method to write a message without answer and with RCODE = 1.
     * 
     * @see Header.java
     * @see formatErrorHeader()
     * 
     * @see Question.java
     * @see writeQuestion()
     * 
     * @return message in the form of a byte array
     */
	public byte[] writeFormatError(){

		// writing of the header
		byte[] headerBytes = header.formatErrorHeader();
		int headerBytesLength = headerBytes.length;

		// writing of the question
		byte[] questionBytes = question.writeQuestion();
		int questionBytesLength = questionBytes.length;

		// calculation of the message length
		Integer messageLength = Integer.valueOf(headerBytesLength + questionBytesLength);

		// concatenation of the message length and the header
		ByteBuffer messageBB = ByteBuffer.allocate(2 + headerBytesLength + questionBytesLength);
		messageBB.putShort(messageLength.shortValue());
        messageBB.put(headerBytes);
        messageBB.put(questionBytes);
		
		// converting into byte array
		byte[] message = messageBB.array();

		return message;
	}

	/**
     * Method to write a message without answer and with RCODE = 3.
     * 
     * @see Header.java
     * @see nameErrorHeader()
     * 
     * @see Question.java
     * @see writeQuestion()
     * 
     * @return message in the form of a byte array
     */
	public byte[] writeNameError(){

		// writing of the header
		byte[] headerBytes = header.nameErrorHeader();
		int headerBytesLength = headerBytes.length;

		// writing of the question
		byte[] questionBytes = question.writeQuestion();
		int questionBytesLength = questionBytes.length;

		// calculation of the message length
		Integer messageLength = Integer.valueOf(headerBytesLength + questionBytesLength);

		// concatenation of the message length and the header
		ByteBuffer messageBB = ByteBuffer.allocate(2 + headerBytesLength + questionBytesLength);
		messageBB.putShort(messageLength.shortValue());
        messageBB.put(headerBytes);
        messageBB.put(questionBytes);
		
		// converting into byte array
		byte[] message = messageBB.array();

		return message;
	}

	/**
     * Method to write a message without answer and with RCODE = 5.
     * 
     * @see Header.java
     * @see refusedHeader()
     * 
     * @see Question.java
     * @see writeQuestion()
     * 
     * @return message in the form of a byte array
     */
	public byte[] writeRefused(){

		// writing of the header
		byte[] headerBytes = header.refusedHeader();
		int headerBytesLength = headerBytes.length;

		// writing of the question
		byte[] questionBytes = question.writeQuestion();
		int questionBytesLength = questionBytes.length;

		// calculation of the message length
		Integer messageLength = Integer.valueOf(headerBytesLength + questionBytesLength);

		// concatenation of the message length and the header
		ByteBuffer messageBB = ByteBuffer.allocate(2 + headerBytesLength + questionBytesLength);
		messageBB.putShort(messageLength.shortValue());
        messageBB.put(headerBytes);
        messageBB.put(questionBytes);
		
		// converting into byte array
		byte[] message = messageBB.array();

		return message;
	}

	/**
     * Method to read a message.
     * 
     * @param query query message to decode in the form of a byte array
     * 
     * @see Header.java
     * @see readHeader(ByteBuffer)
     * 
     * @see Question.java
     * @see readQuestion(ByteBuffer)
     * 
     * @see NameTypeErrorCode.java
     * @see putErrorCode(int)
     * 
     * @return NAME, TYPE and error code of query in the form of a 
     * 	NameTypeErrorCode object
     */
	public NameTypeErrorCode readMessage(byte[] query) {

		int headerBytesLength = 12;
		int questionBytesLength = query.length - headerBytesLength;

		// convert query into a ByteBuffer in order to separate it between header and question
		ByteBuffer queryBB = ByteBuffer.wrap(query);

		// header part
		byte[] headerBytes = new byte[headerBytesLength];
	    queryBB.get(headerBytes, 0, headerBytes.length);
	    ByteBuffer headerBB = ByteBuffer.wrap(headerBytes);
		int errorCodeHeader = header.readHeader(headerBB);

		// question part
		byte[] questionBytes = new byte[questionBytesLength];
	    queryBB.get(questionBytes, 0, questionBytes.length);
	    ByteBuffer questionBB = ByteBuffer.wrap(questionBytes);
		NameTypeErrorCode nameTypeErrorCode = question.readQuestion(questionBB);

		// error code from header more important
		if (errorCodeHeader > 0)
			nameTypeErrorCode.putErrorCode(errorCodeHeader);

		return nameTypeErrorCode;
	}
}
