package com.ozyuce.maps.feature.auth.domain.model

enum class UserRole {
    DRIVER,
    CUSTOMER,
    ADMIN;

    companion object {
        fun fromString(value: String): UserRole {
            return when (value.uppercase()) {
                "DRIVER" -> DRIVER
                "CUSTOMER" -> CUSTOMER
                "ADMIN" -> ADMIN
                else -> throw IllegalArgumentException("Invalid user role: $value")
            }
        }
    }
}
