package com.thomasvital.login.aggregate;


import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HttpErrorInfo {
  private   ZonedDateTime timestamp;
  private   String path;
  private   HttpStatus httpStatus;
  private   String message;

  

  public HttpErrorInfo(HttpStatus httpStatus, String path, String message) {
    timestamp = ZonedDateTime.now();
    this.httpStatus = httpStatus;
    this.path = path;
    this.message = message;
  }
 
}
