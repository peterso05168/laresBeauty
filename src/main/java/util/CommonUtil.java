package util;

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
	
}