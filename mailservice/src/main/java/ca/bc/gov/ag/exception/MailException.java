/**
 * @(#)MailException.java
 * @author afwilcox
 * Copyright (c) 2001, B.C. Ministry of Attorney General.
 * All rights reserved.
 */
package ca.bc.gov.ag.exception;

public class MailException extends ICEDException {

	private static final long serialVersionUID = 1L;

	public MailException(String msg, Exception e) {
		super(MailException.class.getName(), msg, e);
	}

	public MailException(String msg) {
		super(MailException.class.getName(), msg);
	}

}
