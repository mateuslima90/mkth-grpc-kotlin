package io.mkth.security.authentication.service

import io.mkth.grpc.authentication.LoginRequest
import io.mkth.security.authentication.exception.UserNotFoundException
import io.mkth.security.authentication.model.Pages
import io.mkth.security.authentication.model.User
import io.mkth.security.authentication.model.UserDTO
import io.mkth.security.authentication.repository.CustomerRepository
import org.mindrot.jbcrypt.BCrypt
import org.reactivestreams.Subscription
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono

@Service
class CustomerService(private val customerRepository: CustomerRepository) {

    fun findUser(username: String): Mono<User> {
        return customerRepository.findUserByUsername(username)
                .switchIfEmpty(Mono.empty())
                .onErrorResume { error -> throw UserNotFoundException(error.message!!) }
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
                //.subscribeOn(Schedulers.parallel())
                .defaultIfEmpty(false)
                .onErrorResume { error -> throw UserNotFoundException(error.message!!) }
    }

    fun saveUser(user: User): Mono<User> {
        return Mono.just(applyHashingPasswordSaveUser(user))
                .flatMap { customerRepository.save(user) }
                .onErrorResume { Mono.error(ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "Failed to processing this entity", it.cause)) }
    }

    fun updateUser(user: User): Mono<User> {
        return Mono.just(user)
                .flatMap { customerRepository.findUserByUsername(user.username!!) }
                .flatMap { customerRepository.save(
                        User(id = it.id, username = user.username, email = user.email,name = it.name, password= it.password))
                }
                .switchIfEmpty(Mono.empty())
    }

    fun deleteUser(username: String): Mono<User> {
        return customerRepository.deleteByUsername(username)
    }

    private fun validatePassword(requestPassword: String, dbPassword: String): Boolean {
        return verifyHashingPassword(requestPassword, dbPassword)
    }

    private fun totalPages(totalElements: Long, size: Int) = totalElements/size

    private fun applyHashingPasswordSaveUser(user: User) : User {
        user.password = applyHashingPassword(user.password!!)
        return user
    }

    private fun applyHashingPassword(password: String) : String {
        return BCrypt.hashpw(password, BCrypt.gensalt(10))
    }

    private fun verifyHashingPassword(password: String, hashingPassword: String): Boolean {
        return BCrypt.checkpw(password, hashingPassword)
    }
}