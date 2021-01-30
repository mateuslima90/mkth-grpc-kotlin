package io.mkth.security.authentication.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class User(@Id val id: String? = null,
                val username: String? = null,
                val name: String? = null,
                val email: String? = null,
                val password: String? = null)