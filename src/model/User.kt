package co.eware.model

import io.ktor.auth.Principal

data class User(val displayName: String) : Principal