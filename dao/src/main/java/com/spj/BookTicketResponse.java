package com.spj;

import org.springframework.http.HttpStatus;

public class BookTicketResponse {
  private boolean success;
  private String message;
  private HttpStatus status;

  public BookTicketResponse(boolean success, String message, HttpStatus status) {
    this.success = success;
    this.message = message;
    this.status = status;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }
}
