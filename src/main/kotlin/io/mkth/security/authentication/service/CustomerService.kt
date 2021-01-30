package io.mkth.security.authentication.service

import io.grpc.Status
import io.mkth.grpc.authentication.LoginRequest
import io.mkth.security.authentication.exception.UserNotFoundException
import io.mkth.security.authentication.model.User
import io.mkth.security.authentication.repository.CustomerRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.lang.RuntimeException

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
}