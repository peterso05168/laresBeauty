package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Map;

public final class CommonUtil {
	
	public static boolean isNull(Short s) {
		return s == null;
	}

	public static boolean isNullOrEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean isNullOrEmpty(Object o) {
		return o instanceof String ? isNullOrEmpty((String) o)
				: (o instanceof Collection ? isNullOrEmpty((Collection) o)
						: (o instanceof Map ? isNullOrEmpty((Map) o) : isNull(o)));
	}

	public static boolean isNull(Object o) {
		return o == null;
	}

	public static <T> boolean isNullOrEmpty(Collection<T> c) {
		return c == null || c.size() == 0;
	}

	public static <K, V> boolean isNullOrEmpty(Map<K, V> m) {
		return m == null || m.isEmpty();
	}
	
	public static String SHA512PasswordHash(String password, String salt) {

		String passwordToHash = password;
		String generatedPassword = null;
		try {
			// Create MessageDigest instance for MD5
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			// Add password bytes to digest
			md.update(salt.getBytes());
			// Get the hash's bytes
			byte[] bytes = md.digest(passwordToHash.getBytes());
			// This bytes[] has bytes in decimal format;
			// Convert it to hexadecimal format
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			// Get complete hashed password in hex format
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}

	public static String getSalt() throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return new String(salt);
	}
}