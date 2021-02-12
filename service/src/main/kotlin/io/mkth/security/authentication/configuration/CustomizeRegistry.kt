package io.mkth.security.authentication.configuration

import org.springframework.cloud.consul.serviceregistry.ConsulRegistration
import org.springframework.cloud.consul.serviceregistry.ConsulRegistrationCustomizer
import org.springframework.context.annotation.Configuration

@Configuration
class CustomizeRegistry : ConsulRegistrationCustomizer {
    override fun customize(reg: ConsulRegistration) {
        reg.service.id = (reg.service.name + "-" + "124123121")
        reg.service.name = reg.service.name + "-rpc"
        reg.service.port = 9090
        reg.service.tags = listOf("grpc")
    }
}