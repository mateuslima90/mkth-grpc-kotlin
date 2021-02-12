package io.mkth.security.authentication.service

import io.grpc.CallOptions
import io.grpc.Status
import io.mkth.grpc.authentication.*
import io.mkth.security.authentication.model.User
import io.mkth.security.authentication.model.UserDTO
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.data.domain.PageRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Duration
import java.util.stream.Collectors

@GrpcService
class GrpcAuthenticationService(private val customerService: CustomerService)
    : ReactorLoginServiceGrpc.LoginServiceImplBase() {

    override fun authenticate(request: Mono<LoginRequest>): Mono<LoginResponse> {
        return request
                .flatMap { customer -> customerService.authenticate(customer) }
                .map { LoginResponse.newBuilder().setMessage(it).build() }
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
                .flatMap { customerService.saveUser(it)}
                .map { buildSaveCustomerResponse(it) }
    }

    override fun streamUser(request: Mono<Empty>?): Flux<FindCustomerResponse> {
        return customerService.findAllUser()
                .map { buildFindCustomerResponse(it) }
                .delayElements(Duration.ofMillis(2000L))
    }

    override fun findAllUser(request: Mono<FindAllUserRequest>): Mono<FindAllUserResponse> {
        return request
                .flatMap { r ->  customerService.findAllUsersByPage(PageRequest.of(r.page, r.size))}
                .map { c -> buildFindAllUser(c.page, c.size, c.TotalPages, c.TotalElements, c.content, ) }
    }

    override fun updateUser(request: Mono<UpdateUserRequest>): Mono<UpdateUserResponse> {
        return request
                .flatMap { r -> customerService.updateUser(User(username = r.username, email = r.email)) }
                .map { UpdateUserResponse.newBuilder().setResponse(true).build() }
                .switchIfEmpty { Mono.error(Status.NOT_FOUND
                        .withDescription("not found").asRuntimeException()) }
    }

    override fun deleteUser(request: Mono<DeleteUserRequest>): Mono<DeleteUserResponse> {
        return request
                .flatMap { r -> customerService.deleteUser(r.username) }
                .map { DeleteUserResponse.newBuilder().setResponse(true).build() }
    }

    private fun findResponse(customer: User): Mono<FindCustomerResponse> {
        return Mono.justOrEmpty(customer)
                .map { buildFindCustomerResponse(customer) }
    }

    private fun buildUser(u: UserDTO): io.mkth.grpc.authentication.User {
        return io.mkth.grpc.authentication.User.newBuilder()
                .setId(u.id)
                .setUsername(u.username)
                .setEmail(u.email)
                .build()
    }

    private fun buildFindAllUser(page: Int, size: Int, totalPages: Long,
                                 totalElements: Long, content: List<UserDTO>): FindAllUserResponse {

        val result = content.stream().map { u -> buildUser(u) }.collect(Collectors.toList()).asIterable()

        return FindAllUserResponse.newBuilder()
                .setPage(page)
                .setSize(size)
                .setTotalPages(totalPages.toInt())
                .setTotalElements(totalElements.toInt())
                .addAllUser(result)
                .build()
    }

    private fun buildFindCustomerResponse(user: User) =
            FindCustomerResponse.newBuilder()
                    .setId(user.id)
                    .setUsername(user.username)
                    .setName(user.name)
                    .setEmail(user.email)
                    .build()

    private fun buildSaveCustomerResponse(user: User) =
            SaveCustomerResponse.newBuilder()
                    .setId(user.id)
                    .setUsername(user.username)
                    .setEmail(user.email)
                    .build()

    override fun getCallOptions(methodId: Int): CallOptions {
        return super.getCallOptions(methodId)
    }
}