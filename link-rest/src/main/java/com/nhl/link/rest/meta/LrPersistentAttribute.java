package com.nhl.link.rest.meta;

import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.ObjAttribute;

/**
 * Represents a persistent attribute.
 * 
 * @since 1.12
 */
public interface LrPersistentAttribute extends LrAttribute {

	/**
	 * @since 1.12
     */
	int getJdbcType();

	/**
	 * @since 1.12
	 * @deprecated since 2.4
     */
	@Deprecated
	ObjAttribute getObjAttribute();

	/**
	 * @since 1.12
	 * @deprecated since 2.4
     */
	@Deprecated
	DbAttribute getDbAttribute();

	/**
	 * @since 2.4
     */
	String getColumnName();

	/**
	 * @since 2.4
     */
	boolean isMandatory();
}
