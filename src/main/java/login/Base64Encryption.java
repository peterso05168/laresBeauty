package login;

import java.util.Base64;

public class Base64Encryption {
    public static String encryption(String rawToken) throws Exception {
    	String encryptedToken = null;
    	try{
    	byte[] encodedBytes = Base64.getEncoder().encode(rawToken.getBytes());
        encryptedToken = new String(encodedBytes);
    	} catch(Exception e) {
			System.out.println(e);
	    }
        return encryptedToken;
     }
    public static String decryption(String encryptedToken) throws Exception {
    	String rawToken = null;
    	try{
			byte[] encodedBytes = encryptedToken.getBytes();
			byte[] decodedBytes = Base64.getDecoder().decode(encodedBytes);
			rawToken = new String(decodedBytes);
		} catch(Exception e) {
				System.out.println(e);
		}
    	return rawToken;
    }
}
