package com.minhoi.memento.data.network

import com.minhoi.memento.data.dto.MajorDto
import com.minhoi.memento.data.dto.SchoolDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface APIService {

    @GET("/api/schools")
    suspend fun getSchools(): Response<List<SchoolDto>>

    @GET("/api/{schoolName}")
    suspend fun getMajors(
        @Path(value = "schoolName") schoolName: String
    ): Response<List<MajorDto>>

    @POST("/api/member/signup")
    suspend fun signUp(
        @Body member: MemberDto
    ): Response<String>
}