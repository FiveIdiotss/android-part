package com.minhoi.memento.repository

import com.minhoi.memento.data.network.APIService
import com.minhoi.memento.data.network.RetrofitClient

class JoinRepository {

    private val retrofitClient = RetrofitClient.getInstance().create(APIService::class.java)

    suspend fun getSchools() = retrofitClient.getSchools()

    suspend fun getMajors(name: String) = retrofitClient.getMajors(name)

}