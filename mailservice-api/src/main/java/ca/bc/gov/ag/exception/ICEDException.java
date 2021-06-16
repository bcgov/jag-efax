/**
 * @(#)ICEDException.java
 * @author afwilcox
 * Copyright (c) 2001, B.C. Ministry of Attorney General.
 * All rights reserved.
 */

package ca.bc.gov.ag.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ICEDException extends Exception {

	private static Logger logger = LoggerFactory.getLogger(ICEDException.class);
	private static final long serialVersionUID = 1L;

	public ICEDException() {
	}

	public ICEDException(String myClass, String msg, Exception e) {
		super(msg, e);		
		logger.error(getMessage(), e);
	}

	public ICEDException(String myClass, String msg) {
		super(msg);
		logger.error(getMessage());

	}

	public ICEDException(String myClass, Exception e) {
		super(e);
		logger.error(getMessage(), e);
	}
}
