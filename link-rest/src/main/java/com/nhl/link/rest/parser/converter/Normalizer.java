package com.nhl.link.rest.parser.converter;

import com.nhl.link.rest.LinkRestException;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class that ensures that client-provided numeric values are
 * converted to the right numeric type. This is especially important for ID
 * values that are used in comparisons.
 * 
 * @since 1.12
 */
public class Normalizer {

	static interface TypeNormalizer {
		Object normalize(Object value);
	}

	@SuppressWarnings("serial")
	private static final Map<Class<?>, TypeNormalizer> NORMALIZERS = new HashMap<Class<?>, TypeNormalizer>() {
		{
			put(Long.class, new TypeNormalizer() {
				@Override
				public Object normalize(Object value) {

					if (!(value instanceof Number)) {
						return value;
					}

					if (value instanceof Long) {
						return value;
					}

					return ((Number) value).longValue();
				}
			});
		}
	};

	@Deprecated
	public static Object normalize(Object value, String numericType) {
		try {
			return normalize(value, Class.forName(numericType));
		} catch (ClassNotFoundException e) {
			throw new LinkRestException(Response.Status.INTERNAL_SERVER_ERROR,
					"Unknown class: " + numericType, e);
		}
	}

	/**
	 * @since 1.24
     */
	public static Object normalize(Object value, Class<?> numericType) {
		TypeNormalizer normalizer = NORMALIZERS.get(numericType);
		return normalizer == null ? value : normalizer.normalize(value);
	}
}
