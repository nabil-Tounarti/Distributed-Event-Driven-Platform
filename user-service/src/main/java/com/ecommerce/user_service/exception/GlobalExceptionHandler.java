package com.ecommerce.user_service.exception;

import com.ecommerce.user_service.dto.ErrorResponse;
import com.ecommerce.user_service.exception.auth.AuthenticationException;
import com.ecommerce.user_service.exception.auth.ConflictException;
import com.ecommerce.user_service.exception.auth.ResourceNotFoundException;
import com.ecommerce.user_service.exception.jwt.*;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
    ErrorResponse error = new ErrorResponse(Instant.now(), status.value(), message);

    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(JwtExpiredException.class)
  public ResponseEntity<ErrorResponse> handleExpired(JwtExpiredException ex) {
    return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
  }

  @ExceptionHandler(JwtInvalidSignatureException.class)
  public ResponseEntity<ErrorResponse> handleInvalid(JwtInvalidSignatureException ex) {
    return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
  }

  @ExceptionHandler(JwtParsingException.class)
  public ResponseEntity<ErrorResponse> handleParsing(JwtParsingException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(JwtGenerationException.class)
  public ResponseEntity<ErrorResponse> handleGeneration(JwtGenerationException ex) {
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  @ExceptionHandler(JwtJwksException.class)
  public ResponseEntity<ErrorResponse> handleJwks(JwtJwksException ex) {
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<ErrorResponse> handleGeneric(JwtException ex) {
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
    return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
    return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
  }
}
