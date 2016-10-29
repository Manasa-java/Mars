package com.mars.dao.util;

import java.util.Collection;

/**
 * 
 * @author PATTLMX
 *
 */
public class CollectionUtils {
	/**
	 * 
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}
}
