package io.mkth.security.authentication.repository

import io.mkth.security.authentication.model.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface CustomerRepository : ReactiveMongoRepository<User, String> {

    fun findUserByUsername(username: String) : Mono<User>
}