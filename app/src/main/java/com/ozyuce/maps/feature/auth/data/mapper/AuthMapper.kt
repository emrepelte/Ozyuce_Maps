package com.ozyuce.maps.feature.auth.data.mapper

import com.ozyuce.maps.feature.auth.data.remote.dto.AuthResponseDto
import com.ozyuce.maps.feature.auth.data.remote.dto.LoginRequestDto
import com.ozyuce.maps.feature.auth.data.remote.dto.RegisterRequestDto
import com.ozyuce.maps.feature.auth.data.remote.dto.UserDto
import com.ozyuce.maps.feature.auth.domain.AuthResult
import com.ozyuce.maps.feature.auth.domain.User

/**
 * Auth DTO ? Domain mapper'lar?
 */
object AuthMapper {
    
    fun mapToLoginRequest(email: String, password: String): LoginRequestDto {
        return LoginRequestDto(
            email = email,
            password = password
        )
    }
    
    fun mapToRegisterRequest(
        email: String,
        password: String,
        name: String,
        role: String
    ): RegisterRequestDto {
        return RegisterRequestDto(
            name = name,
            email = email,
            password = password,
            role = role
        )
    }
    
    fun mapToDomain(dto: AuthResponseDto): AuthResult {
        return AuthResult(
            token = dto.token,
            user = mapUserToDomain(dto.user)
        )
    }
    
    private fun mapUserToDomain(dto: UserDto): User {
        return User(
            id = dto.id,
            name = dto.name,
            email = dto.email,
            role = dto.role
        )
    }
}
