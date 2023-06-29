import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

/**
 * Class responsible for making HTTP requests and returning HTTP responses
 * 
 * @author Louis Hogge and Juliette Waltregny
 */
public class RequestHTTP {

    // constructor
    public RequestHTTP(){
    }

    /**
     * Method to make the HTTP request and send a HTTP response coded in 
     *  base64
     * 
     * @param url_32 String coded in base32 containing the url used for HTTP 
     *  request
     * 
     * @return encodedResponse the HTTP response coded in base64
     */
    
    public String makeHTTPrequest(String url_32) throws IOException, NameErrorException {

        // 6: decode base32
        String coded_url = url_32.replaceAll("=", ""); // handle padding
        String decoded_url = new String(Base32.decode(coded_url), StandardCharsets.UTF_8); //decoding base32

        // check if url valid
        if(!isURLValid(decoded_url))
            throw new NameErrorException("Name Error");
        
        // 7: http request
        URL con_url = new URL(decoded_url);
        HttpURLConnection connection = (HttpURLConnection) con_url.openConnection();
        connection.setRequestMethod("GET");

        int rCode = connection.getResponseCode();   //print 200 if good, -1 otherwise

        BufferedReader input;
        StringBuffer resp = null;
        String inLine;

        if (rCode != HttpURLConnection.HTTP_OK) //response code != 200
            throw new NameErrorException("Name Error");
            
        // response code = 200
        // reading the HTTP response
        resp = new StringBuffer();
        input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while((inLine = input.readLine()) != null){
            resp.append(inLine);
        } 
        input.close();
        
        //8: code the response in base64            
        String encodedResponse = Base64.getEncoder().encodeToString(resp.toString().getBytes());

        //9: verify the length of the response does'nt exceed the threshold and truncate it if the case
        int threshold = 60000; // arbitrary threshold (to not exceed 2^16-1)

        if (encodedResponse.getBytes().length > threshold){
            String truncated_response = encodedResponse.substring(0, threshold); 
            encodedResponse = truncated_response;
        }

        return encodedResponse;       
    }

    /**
     * Method to check if sent url is valid 
     * 
     * @param url String url to be checked
     * 
     * @return true if url is valid, false otherwise
     */
    private boolean isURLValid(String url)
    {
        //Try creating a URL from given string
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) { 
            // If there was an Exception while creating the url
            return false;
        }
    }
}
