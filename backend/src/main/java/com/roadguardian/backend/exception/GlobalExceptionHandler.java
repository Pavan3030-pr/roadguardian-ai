package com.roadguardian.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(
			ResourceNotFoundException ex,
			WebRequest request) {
		ErrorResponse errorResponse = ErrorResponse.builder()
				.status(HttpStatus.NOT_FOUND.value())
				.message(ex.getMessage())
				.timestamp(LocalDateTime.now())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<ErrorResponse> handleInvalidRequest(
			InvalidRequestException ex,
			WebRequest request) {
		ErrorResponse errorResponse = ErrorResponse.builder()
				.status(HttpStatus.BAD_REQUEST.value())
				.message(ex.getMessage())
				.timestamp(LocalDateTime.now())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationException(
			AuthenticationException ex,
			WebRequest request) {
		ErrorResponse errorResponse = ErrorResponse.builder()
				.status(HttpStatus.UNAUTHORIZED.value())
				.message("Authentication failed: " + ex.getMessage())
				.timestamp(LocalDateTime.now())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponse> handleValidationException(
			MethodArgumentNotValidException ex,
			WebRequest request) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
				.status(HttpStatus.BAD_REQUEST.value())
				.message("Validation failed")
				.timestamp(LocalDateTime.now())
				.path(request.getDescription(false).replace("uri=", ""))
				.errors(errors)
				.build();
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(
			Exception ex,
			WebRequest request) {
		ErrorResponse errorResponse = ErrorResponse.builder()
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.message("An unexpected error occurred")
				.timestamp(LocalDateTime.now())
				.path(request.getDescription(false).replace("uri=", ""))
				.build();
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Data
	@AllArgsConstructor
	public static class ErrorResponse {
		private int status;
		private String message;
		private LocalDateTime timestamp;
		private String path;

		public static ErrorResponseBuilder builder() {
			return new ErrorResponseBuilder();
		}

		public static class ErrorResponseBuilder {
			private int status;
			private String message;
			private LocalDateTime timestamp;
			private String path;

			public ErrorResponseBuilder status(int status) {
				this.status = status;
				return this;
			}

			public ErrorResponseBuilder message(String message) {
				this.message = message;
				return this;
			}

			public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
				this.timestamp = timestamp;
				return this;
			}

			public ErrorResponseBuilder path(String path) {
				this.path = path;
				return this;
			}

			public ErrorResponse build() {
				return new ErrorResponse(status, message, timestamp, path);
			}
		}
	}

	@Data
	@AllArgsConstructor
	public static class ValidationErrorResponse {
		private int status;
		private String message;
		private LocalDateTime timestamp;
		private String path;
		private Map<String, String> errors;

		public static ValidationErrorResponseBuilder builder() {
			return new ValidationErrorResponseBuilder();
		}

		public static class ValidationErrorResponseBuilder {
			private int status;
			private String message;
			private LocalDateTime timestamp;
			private String path;
			private Map<String, String> errors;

			public ValidationErrorResponseBuilder status(int status) {
				this.status = status;
				return this;
			}

			public ValidationErrorResponseBuilder message(String message) {
				this.message = message;
				return this;
			}

			public ValidationErrorResponseBuilder timestamp(LocalDateTime timestamp) {
				this.timestamp = timestamp;
				return this;
			}

			public ValidationErrorResponseBuilder path(String path) {
				this.path = path;
				return this;
			}

			public ValidationErrorResponseBuilder errors(Map<String, String> errors) {
				this.errors = errors;
				return this;
			}

			public ValidationErrorResponse build() {
				return new ValidationErrorResponse(status, message, timestamp, path, errors);
			}
		}
	}
}
