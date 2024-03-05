package com.minhoi.memento.data.network

import com.minhoi.memento.data.dto.BoardContentResponse
import com.minhoi.memento.data.dto.VerifyCodeRequest
import com.minhoi.memento.data.dto.EmailVerificationRequest
import com.minhoi.memento.data.dto.LoginRequest
import com.minhoi.memento.data.dto.LoginResponse
import com.minhoi.memento.data.dto.MajorDto
import com.minhoi.memento.data.dto.CreateMemberRequest
import com.minhoi.memento.data.dto.BoardListResponse
import com.minhoi.memento.data.dto.MentorBoardPostDto
import com.minhoi.memento.data.dto.MentoringApplyDto
import com.minhoi.memento.data.dto.MentoringApplyRequest
import com.minhoi.memento.data.dto.MentoringMatchInfo
import com.minhoi.memento.data.dto.MentoringReceivedDto
import com.minhoi.memento.data.dto.SchoolDto
import com.minhoi.memento.data.model.BoardType
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {





    suspend fun test(): Response<String>
}