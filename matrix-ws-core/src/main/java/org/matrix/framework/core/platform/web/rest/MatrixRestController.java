package org.matrix.framework.core.platform.web.rest;

import java.util.List;
import java.util.Locale;

import org.matrix.framework.core.platform.exception.BusinessProcessException;
import org.matrix.framework.core.platform.exception.InvalidRequestException;
import org.matrix.framework.core.platform.exception.ResourceNotFoundException;
import org.matrix.framework.core.platform.exception.UserAuthorizationException;
import org.matrix.framework.core.platform.monitor.PassException;
import org.matrix.framework.core.platform.throttling.RequestLimitException;
import org.matrix.framework.core.platform.web.DefaultController;
import org.matrix.framework.core.util.ExceptionUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class MatrixRestController extends DefaultController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse error(Throwable e) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(e.getMessage());
        error.setExceptionTrace(ExceptionUtils.stackTrace(e));
        return error;
    }

    @ExceptionHandler({ BusinessProcessException.class })
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ResponseBody
    public ErrorResponse businessError(Throwable e) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(e.getMessage());
        error.setExceptionTrace(ExceptionUtils.stackTrace(e));
        return error;
    }

    @ExceptionHandler({ RequestLimitException.class })
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    @ResponseBody
    public ErrorResponse requestLimitError(Throwable e) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(e.getMessage());
        error.setExceptionTrace(ExceptionUtils.stackTrace(e));
        return error;
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse validationError(MethodArgumentNotValidException e) {
        return createValidationResponse(e.getBindingResult());
    }

    @ExceptionHandler({ BindException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse validationError(BindException e) {
        return createValidationResponse(e.getBindingResult());
    }

    private ErrorResponse createValidationResponse(BindingResult bindingResult) {
        ErrorResponse error = new ErrorResponse();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder msg = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            msg.append(fieldError.getDefaultMessage());
        }
        error.setMessage(msg.toString());
        return error;
    }

    @ExceptionHandler({ InvalidRequestException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse validationError(InvalidRequestException e) {
        Locale locale = LocaleContextHolder.getLocale();
        ErrorResponse error = new ErrorResponse();
        StringBuilder msg = new StringBuilder();
        msg.append(e.getField()).append("=>").append(this.messages.getMessage(e.getMessage(), new Object[] { locale }));
        error.setMessage(msg.toString());
        error.setExceptionTrace(msg.toString());
        return error;
    }

    @ExceptionHandler({ ResourceNotFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse notFound(ResourceNotFoundException e) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(e.getMessage());
        error.setExceptionTrace(ExceptionUtils.stackTrace(e));
        return error;
    }

    @ExceptionHandler({ UserAuthorizationException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse unauthorized(UserAuthorizationException e) {
        ErrorResponse error = new ErrorResponse();
        error.setMessage(e.getMessage());
        error.setExceptionTrace(ExceptionUtils.stackTrace(e));
        return error;
    }

    @ExceptionHandler({ PassException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public void passError(Throwable e) {
    }
}