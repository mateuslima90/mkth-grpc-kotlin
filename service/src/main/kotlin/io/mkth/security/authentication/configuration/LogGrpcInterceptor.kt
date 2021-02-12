package io.mkth.security.authentication.configuration

import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant




class LogGrpcInterceptor : ServerInterceptor {

    private val log: Logger = LoggerFactory.getLogger(LogGrpcInterceptor::class.java)

    override fun <ReqT : Any?, RespT : Any?>
            interceptCall(call: ServerCall<ReqT, RespT>?,
                          headers: io.grpc.Metadata?,
                          next: ServerCallHandler<ReqT, RespT>?): ServerCall.Listener<ReqT> {
        log.info(call!!.methodDescriptor.fullMethodName + " " + "GRPC call at: {}", Instant.now())

        val listener: ServerCall.Listener<ReqT>

        listener = try {
            next!!.startCall(call, headers)
        } catch (ex: Throwable) {
            log.error("Uncaught exception from grpc service")
            call.close(Status.INTERNAL
                    .withCause(ex)
                    .withDescription("Uncaught exception from grpc service"), null)
            return object : ServerCall.Listener<ReqT>() {}
        }

        return listener
    }

}