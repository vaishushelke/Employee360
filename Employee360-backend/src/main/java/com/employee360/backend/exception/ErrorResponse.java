package com.employee360.backend.exception;

import java.time.LocalDate;

public class ErrorResponse {

	private LocalDate timestamp;
	private int status;
	private String message;
	
	
	public ErrorResponse() {
		super();
		// TODO Auto-generated constructor stub
	}


	public ErrorResponse(LocalDate timestamp, int status, String message) {
		super();
		this.timestamp = timestamp;
		this.status = status;
		this.message = message;
	}


	public LocalDate getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(LocalDate timestamp) {
		this.timestamp = timestamp;
	}


	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	@Override
	public String toString() {
		return "ErrorResponse [status=" + status + ", message=" + message + "]";
	}
	
	
	
	
}
