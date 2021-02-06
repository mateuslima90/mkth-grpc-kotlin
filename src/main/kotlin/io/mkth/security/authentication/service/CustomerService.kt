package io.mkth.security.authentication.service

import io.mkth.grpc.authentication.LoginRequest
import io.mkth.security.authentication.exception.UserNotFoundException
import io.mkth.security.authentication.model.Pages
import io.mkth.security.authentication.model.User
import io.mkth.security.authentication.model.UserDTO
import io.mkth.security.authentication.repository.CustomerRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class CustomerService(private val customerRepository: CustomerRepository) {

    fun findUser(username: String): Mono<User> {
        return customerRepository.findUserByUsername(username)
                .switchIfEmpty(Mono.empty())
                .onErrorResume { error -> throw UserNotFoundException("User not found $error") }
    }

    fun findAllUser(): Flux<User> {
        return customerRepository.findAll()
    }

    fun findAllUsersByPage(p: Pageable): Mono<Pages> {
        return this.customerRepository.count()
                .flatMap {
                    this.customerRepository.findAllByIdNotNullOrderByIdAsc(p)
                            .map { user -> UserDTO(user.id,user.username, user.email) }
                            .buffer()
                            .map { u -> Pages(u,p.pageNumber, p.pageSize, totalPages(it,p.pageSize), it) }
                            .toMono()
                            .defaultIfEmpty(Pages(listOf(),p.pageNumber, p.pageSize, totalPages(it,p.pageSize), it))
                }
    }

    fun authenticate(user: LoginRequest): Mono<Boolean> {
        return this.findUser(user.username)
                .map { c -> validatePassword(user.password, c.password!!) }
                .defaultIfEmpty(false)
                .onErrorResume { error -> throw UserNotFoundException("User not found $error") }
    }

    fun createUser(user: User): Mono<User> {
        return customerRepository.save(user)
    }

    private fun validatePassword(requestPassword: String, dbPassword: String): Boolean {
        return requestPassword == dbPassword
    }

    private fun totalPages(totalElements: Long, size: Int) = totalElements/size
}