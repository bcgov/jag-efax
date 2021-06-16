package ca.bc.gov.ag.exception;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvisor {

	private Logger logger = LoggerFactory.getLogger(ExceptionControllerAdvisor.class);
	
	/** General internal server error. */
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler({ Exception.class, MailException.class })
    public ResponseEntity<Object> handleInternal(Exception ex, HttpServletRequest request) {
    	logger.error("Internal server error occurred", ex);
		return getError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", null, request);
    }

	/** Thrown when the JSON request body is malformed. */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
		return getError(HttpStatus.BAD_REQUEST, "JSON parse error", Arrays.asList(ex.getCause().getMessage()), request);
	}	

	/** Thrown on validation errors. */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        
		//Get all errors
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        
		return getError(HttpStatus.BAD_REQUEST, "Validation failed", errors, request);
	}

	/** Thrown when an attachment URI is malformed. */
	@ExceptionHandler(URISyntaxException.class)
	public ResponseEntity<Object> handleURISyntaxException(URISyntaxException ex, HttpServletRequest request) {
		return getError(HttpStatus.BAD_REQUEST, "Malformed attachment URI", Arrays.asList(ex.getMessage()), request);
	}
	
	private ResponseEntity<Object> getError(HttpStatus httpStatus, String message, List<String> errors, HttpServletRequest request) {
		ApiError body = new ApiError();
		body.setTimestamp(new Date());
		body.setStatus(httpStatus.value());
		body.setMessage(message);
		body.setErrors(errors);
		body.setPath(request.getRequestURI());
        
		return new ResponseEntity<>(body, httpStatus);
	}

}
