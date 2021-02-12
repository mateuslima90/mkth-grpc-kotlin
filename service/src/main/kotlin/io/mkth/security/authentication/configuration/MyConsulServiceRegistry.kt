package io.mkth.security.authentication.configuration

import com.ecwid.consul.v1.ConsulClient
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties
import org.springframework.cloud.consul.discovery.HeartbeatProperties
import org.springframework.cloud.consul.discovery.TtlScheduler
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry
import org.springframework.context.annotation.Configuration

//@Configuration
class MyConsulServiceRegistry(client: ConsulClient?, properties: ConsulDiscoveryProperties?,
                              ttlScheduler: TtlScheduler?, heartbeatProperties: HeartbeatProperties?)
    : ConsulServiceRegistry(client, properties, ttlScheduler, heartbeatProperties) {


    override fun register(reg: ConsulRegistration) {

        //reg.getService().setId(reg.getService().getName() + "-" + reg.getService().getAddress() + "-" + reg.getService().getPort());

        reg.service.id = (reg.service.name + "-" + reg.service.address + "-" + 9090)
        reg.service.name = reg.service.name + "-rpc"
        reg.service.port = 9090
        super.register(reg)
    }
}