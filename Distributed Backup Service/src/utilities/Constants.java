package utilities;

import java.security.MessageDigest;

public class Constants {
	public static final char CR = 0xD;
	public static final char LF = 0xA;
	
	public static final String BACKUP = "PUTCHUNK";
	public static final String RESTORE = "GETCHUNK";
	public static final String DELETE = "DELETE";
	
	public static String sha256(String base) {
	    try{
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(base.getBytes("UTF-8"));
	        StringBuffer hexString = new StringBuffer();

	        for (int i = 0; i < hash.length; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }

	        return hexString.toString();
	    } catch(Exception ex){
	       throw new RuntimeException(ex);
	    }
	}
}
