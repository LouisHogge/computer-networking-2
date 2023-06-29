/**
 * Class responsible for Name error exceptions (RCODE = 3)
 * 
 * @author Louis Hogge and Juliette Waltregny
 */
public class NameErrorException extends Exception { 
    public NameErrorException(String errorMessage) {
        super(errorMessage);
    }
}
