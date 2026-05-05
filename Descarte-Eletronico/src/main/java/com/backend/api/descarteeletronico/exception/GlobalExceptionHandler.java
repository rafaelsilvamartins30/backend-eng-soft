package com.backend.api.descarteeletronico.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(
      ResourceNotFoundException exception, HttpServletRequest request) {
    return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request, Set.of());
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponseDTO> handleBusinessException(
      BusinessException exception, HttpServletRequest request) {
    return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request, Set.of());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDTO> handleValidationException(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    Set<String> details =
        exception.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toSet());

    return buildResponse(HttpStatus.BAD_REQUEST, "Dados de entrada inválidos", request, details);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleUnexpectedException(
      Exception exception, HttpServletRequest request) {
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado", request, Set.of());
  }

  private ResponseEntity<ErrorResponseDTO> buildResponse(
      HttpStatus status, String message, HttpServletRequest request, Set<String> details) {
    ErrorResponseDTO response =
        new ErrorResponseDTO(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getRequestURI(),
            details);

    return ResponseEntity.status(status).body(response);
  }
}
