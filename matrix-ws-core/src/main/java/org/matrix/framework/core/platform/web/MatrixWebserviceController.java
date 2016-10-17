package org.matrix.framework.core.platform.web;

import org.matrix.framework.core.platform.exception.InvalidRequestException;
import org.matrix.framework.core.platform.exception.ResourceNotFoundException;
import org.matrix.framework.core.platform.exception.UserAuthorizationException;
import org.matrix.framework.core.platform.web.rest.ErrorResponse;
import org.matrix.framework.core.platform.web.rest.MatrixRestController;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class MatrixWebserviceController extends MatrixRestController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ErrorResponse error(Throwable e) {
        return super.error(e);
    }

    @ExceptionHandler({ ResourceNotFoundException.class })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ErrorResponse notFound(ResourceNotFoundException e) {
        return super.notFound(e);
    }

    @ExceptionHandler({ UserAuthorizationException.class })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ErrorResponse unauthorized(UserAuthorizationException e) {
        return super.unauthorized(e);
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ErrorResponse validationError(MethodArgumentNotValidException e) {
        return super.validationError(e);
    }

    @ExceptionHandler({ BindException.class })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ErrorResponse validationError(BindException e) {
        return super.validationError(e);
    }

    @ExceptionHandler({ InvalidRequestException.class })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ErrorResponse validationError(InvalidRequestException e) {
        return super.validationError(e);
    }

}