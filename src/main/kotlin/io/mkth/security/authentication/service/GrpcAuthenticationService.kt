package io.mkth.security.authentication.service

import io.grpc.CallOptions
import io.grpc.Status
import io.mkth.grpc.authentication.*
import io.mkth.security.authentication.model.User
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Duration
import java.util.stream.Stream

@GrpcService
class GrpcAuthenticationService(private val customerService: CustomerService)
    : ReactorLoginServiceGrpc.LoginServiceImplBase() {

    override fun login(request: Mono<LoginRequest>): Mono<LoginResponse> {
        return request
                .flatMap { customer -> customerService.authenticate(customer) }
                .map { LoginResponse.newBuilder().setMessage(it.toString()).build() }
    }

    override fun findUser(request: Mono<FindCustomerRequest>): Mono<FindCustomerResponse> {
        return request
                .flatMap { customer -> customerService.findUser(customer.username) }
                .flatMap { customer -> findResponse(customer) }
                .switchIfEmpty { Mono.error(Status.NOT_FOUND
                        .withDescription("not found").asRuntimeException()) }
    }

    override fun saveUser(request: Mono<SaveCustomerRequest>): Mono<SaveCustomerResponse> {
        return request
                .map { c -> User(username = c.username, name = c.name,
                        email = c.email, password = c.password) }
                .flatMap { customerService.createUser(it)}
                .map { buildSaveCustomerResponse(it) }
    }

    override fun streamUser(request: Mono<Empty>?): Flux<FindCustomerResponse> {
        return customerService.findAllUser()
                .map { buildFindCustomerResponse(it) }
                .delayElements(Duration.ofMillis(2000L))
    }

    private fun findResponse(customer: User): Mono<FindCustomerResponse> {
        return Mono.justOrEmpty(customer)
                .map { buildFindCustomerResponse(customer) }
    }

    private fun buildFindCustomerResponse(user: User) =
            FindCustomerResponse.newBuilder()
                    .setUsername(user.username)
                    .setName(user.name)
                    .setEmail(user.email)
                    .build()

    private fun buildSaveCustomerResponse(user: User) =
            SaveCustomerResponse.newBuilder()
                    .setId(user.id)
                    .setUsername(user.username)
                    .setName(user.name)
                    .setEmail(user.email)
                    .setPassword(user.password)
                    .build()

    override fun getCallOptions(methodId: Int): CallOptions {
        return super.getCallOptions(methodId)
    }
}