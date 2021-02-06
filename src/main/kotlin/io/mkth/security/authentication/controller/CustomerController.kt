package io.mkth.security.authentication.controller

import io.mkth.security.authentication.model.Pages
import io.mkth.security.authentication.model.User
import io.mkth.security.authentication.model.UserDTO
import io.mkth.security.authentication.service.CustomerService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@RestController
class CustomerController(private val customerService: CustomerService) {

    @PostMapping("/customer")
    fun saveUser(@RequestBody user: User) : Mono<User> {
        return customerService.createUser(user)
    }

    @GetMapping("/customer/{username}")
    fun getUser(@PathVariable username: String) : Mono<User> {
        return customerService.findUser(username)
    }

    @GetMapping("/customers")
    fun getAllUser() : Flux<User> {
        return customerService.findAllUser();
    }

    @GetMapping("/allUsers")
    fun getAllUser(@RequestParam("page") page: Int,
                   @RequestParam("size") size: Int): Mono<Pages> {
        return customerService.findAllUsersByPage(PageRequest.of(page, size))
    }

    @GetMapping("/stream/customers", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun getAllUserInStream() : Flux<User> {
        return customerService.findAllUser()
                .delayElements(Duration.ofMillis(2500L))
    }
}