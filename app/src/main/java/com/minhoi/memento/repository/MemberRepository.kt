package com.minhoi.memento.repository

import com.minhoi.memento.data.network.APIService
import com.minhoi.memento.data.network.RetrofitClient

class MemberRepository {

    private val loggedInRetrofitClient =
        RetrofitClient.getLoggedInInstance().create(APIService::class.java)

    suspend fun getApplyList(memberId: Long) = loggedInRetrofitClient.getMyApply(memberId, "SEND")
}