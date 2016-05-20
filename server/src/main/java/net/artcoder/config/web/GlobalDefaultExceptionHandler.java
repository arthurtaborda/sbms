package net.artcoder.config.web;

import net.artcoder.dto.ErrorDto;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
class GlobalDefaultExceptionHandler extends DefaultHandlerExceptionResolver {

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity defaultErrorHandler(HttpServletRequest request, HttpServletResponse response,
											  Object handler, Exception ex) throws Exception {
		super.doResolveException(request, response, handler, ex);
		Integer errorCode = response.getStatus();
		String message = ex.getMessage();

		if(ex instanceof AccessDeniedException) {
			errorCode = 403;
		} else if(ex instanceof IllegalStateException) {
			errorCode = 400;
		}

		ResponseStatus status = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
		if (status != null) {
			errorCode = status.value().value();
			message = status.reason();
		}
		ErrorDto error = new ErrorDto(errorCode, message);

		return ResponseEntity.status(errorCode).body(error);
	}
}