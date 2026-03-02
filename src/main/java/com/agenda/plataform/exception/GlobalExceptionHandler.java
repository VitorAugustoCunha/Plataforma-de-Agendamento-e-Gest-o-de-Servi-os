package com.agenda.plataform.exception;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.agenda.plataform.dto.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        log.error("Recurso não encontrado: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .code(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .timestamp(OffsetDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<ErrorResponse> handleAppointmentConflictException(
            AppointmentConflictException ex, WebRequest request) {
        
        log.error("Conflito de agendamento: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .code(HttpStatus.CONFLICT.value())
            .message(ex.getMessage())
            .timestamp(OffsetDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    @ExceptionHandler(InvalidBusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBusinessRuleException(
            InvalidBusinessRuleException ex, WebRequest request) {
        
        log.error("Regra de negócio violada: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .code(422)
            .message(ex.getMessage())
            .timestamp(OffsetDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        
        return ResponseEntity.status(422).body(errorResponse);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        log.error("Argumento inválido: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .timestamp(OffsetDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.error("Erro de validação: {}", ex.getMessage());
        
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .message("Erro de validação")
            .details(details)
            .timestamp(OffsetDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        log.error("Erro interno do servidor: ", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("Erro interno do servidor")
            .timestamp(OffsetDateTime.now())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
