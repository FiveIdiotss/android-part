package com.minhoi.memento.di

import com.minhoi.memento.BuildConfig
import com.minhoi.memento.data.network.interceptor.AuthInterceptor
import com.minhoi.memento.data.network.service.AuthService
import com.minhoi.memento.data.network.service.BoardService
import com.minhoi.memento.data.network.service.ChatService
import com.minhoi.memento.data.network.service.JoinService
import com.minhoi.memento.data.network.service.MatchingService
import com.minhoi.memento.data.network.service.MemberService
import com.minhoi.memento.data.network.service.NotificationService
import com.minhoi.memento.data.network.service.QuestionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesRetrofitClient(): Retrofit =
        Retrofit.Builder()
            .client(OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build())
            .baseUrl(BuildConfig.SERVER_IP)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).build()

    @Provides
    @Singleton
    fun providesAuthService(retrofit: Retrofit) =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun providesBoardService(retrofit: Retrofit) =
        retrofit.create(BoardService::class.java)

    @Provides
    @Singleton
    fun providesChatService(retrofit: Retrofit) =
        retrofit.create(ChatService::class.java)

    @Provides
    @Singleton
    fun providesJoinService(retrofit: Retrofit) =
        retrofit.create(JoinService::class.java)

    @Provides
    @Singleton
    fun providesMatchingService(retrofit: Retrofit) =
        retrofit.create(MatchingService::class.java)

    @Provides
    @Singleton
    fun providesMemberService(retrofit: Retrofit) =
        retrofit.create(MemberService::class.java)

    @Provides
    @Singleton
    fun providesNotificationService(retrofit: Retrofit) =
        retrofit.create(NotificationService::class.java)

    @Provides
    @Singleton
    fun providesQuestionService(retrofit: Retrofit) =
        retrofit.create(QuestionService::class.java)

}