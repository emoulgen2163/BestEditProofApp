package com.emoulgen.vibecodingapp.domain.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String = "",
    val token: String,
    val isEditor: Boolean = false
)
