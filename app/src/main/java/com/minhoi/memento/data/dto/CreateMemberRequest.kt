package com.minhoi.memento.data.dto

data class CreateMemberRequest(
    val email: String,
    val name: String,
    val password: String,
    val year: Int,
    val gender: String,
    val schoolName: String,
    val majorId: Int
)
