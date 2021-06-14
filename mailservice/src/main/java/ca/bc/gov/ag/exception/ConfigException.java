/**
 * @(#)ConfigException.java
 * @author afwilcox
 * Copyright (c) 2001, B.C. Ministry of Attorney General.
 * All rights reserved.
 */
package ca.bc.gov.ag.exception;

public class ConfigException extends ICEDException {

	private static final long serialVersionUID = 1L;

	public ConfigException(String msg, Exception e) {
		super(ConfigException.class.getName(), msg, e);
	}

	public ConfigException(String msg) {
		super(ConfigException.class.getName(), msg);
	}

}
