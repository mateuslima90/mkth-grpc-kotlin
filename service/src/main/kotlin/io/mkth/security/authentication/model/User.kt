package io.mkth.security.authentication.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class User(@Id val id: String? = null,
                @Indexed(unique = true) val username: String? = null,
                val name: String? = null,
                @Indexed(unique = true) val email: String? = null,
                val password: String? = null)


data class UserDTO(val id: String?,
                   val username: String?,
                   val email: String?)

data class Pages(val content: List<UserDTO>,
                 var page: Int,
                 var size: Int,
                 var TotalPages: Long,
                 var TotalElements: Long)