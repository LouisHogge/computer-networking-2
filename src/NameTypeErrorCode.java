/**
 * Class responsible for containing both name, type and error code of a query 
 *  into single object
 * 
 * @author Louis Hogge and Juliette Waltregny
 */
final class NameTypeErrorCode {

    private final String name;
    private final String type;
    private int errorCode;

    // constructor
    public NameTypeErrorCode(String name, String type, int errorCode) {
        this.name = name;
        this.type = type;
        this.errorCode = errorCode;
    }

    /**
     * Method to get the name from the NameTypeErrorCode object.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Method to get the type from the NameTypeErrorCode object.
     * 
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Method to get the errorCode from the NameTypeErrorCode object.
     * 
     * @return errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Method to change the errorCode in the NameTypeErrorCode object.
     * 
     * @param errorCode new error code
     */
    public void putErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
