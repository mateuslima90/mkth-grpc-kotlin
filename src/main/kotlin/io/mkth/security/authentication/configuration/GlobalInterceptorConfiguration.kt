package io.mkth.security.authentication.configuration

import io.grpc.ServerInterceptor
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class GlobalInterceptorConfiguration {

    @GrpcGlobalServerInterceptor
    fun logServerInterceptor(): ServerInterceptor? {
        return LogGrpcInterceptor()
    }
}