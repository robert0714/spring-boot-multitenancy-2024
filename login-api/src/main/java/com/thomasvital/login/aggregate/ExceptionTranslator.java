package com.thomasvital.login.aggregate;
 

import org.springframework.boot.web.servlet.error.ErrorAttributes; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;  
import org.springframework.web.bind.annotation.ExceptionHandler; 
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
 
import org.springframework.web.bind.annotation.ResponseBody; 

@RestControllerAdvice
@Slf4j
public class ExceptionTranslator {

	private final ErrorAttributes errorAttributes;

	public ExceptionTranslator(ErrorAttributes errorAttributes) {
		this.errorAttributes = errorAttributes;
	}

	@ExceptionHandler(Exception.class)
	public @ResponseBody ResponseEntity<HttpErrorInfo> handleValidationExceptions(final WebRequest request,
			Exception ex) {
		ex.printStackTrace();
		if (ex instanceof HttpClientErrorException) {
			HttpClientErrorException ex1 = (HttpClientErrorException) ex;
			HttpStatus httpStatus1 = HttpStatus.valueOf(ex1.getStatusCode().value());
			return createHttpErrorInfo(httpStatus1, request, ex);
		}
		return createHttpErrorInfo(BAD_REQUEST, request, ex);
	}

	private ResponseEntity<HttpErrorInfo> createHttpErrorInfo(HttpStatus httpStatus, WebRequest request, Exception ex) {

//		final String path = request.getPath().pathWithinApplication().value();
		final String path = ((ServletWebRequest) request).getRequest().getRequestURI().toString();
		final String message = ex.getMessage();
		log.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
		return new ResponseEntity<HttpErrorInfo>(new HttpErrorInfo(httpStatus, path, message), httpStatus);
	}
}