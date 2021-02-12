package io.mkth.security.authentication.exception

import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ErrorHandler {

    @ExceptionHandler(value = [UserNotFoundException::class])
    fun handlerUserNotFound(): StatusRuntimeException? {
        return Status.NOT_FOUND.asRuntimeException()
    }
}