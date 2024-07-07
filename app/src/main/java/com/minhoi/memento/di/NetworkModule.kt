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
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class WithoutInterceptor

    @Provides
    @Singleton
    fun providesOkHttpClientWithInterceptor(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()

    @Provides
    @Singleton
    @WithoutInterceptor
    fun providesOkHttpClientWithoutInterceptor(): OkHttpClient =
        OkHttpClient.Builder()
            .build()

    @Provides
    @Singleton
    fun providesRetrofitClientWithInterceptor(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(client)
            .baseUrl("http://" + BuildConfig.SERVER_IP + ":8080/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @WithoutInterceptor
    fun providesRetrofitClientWithoutInterceptor(@WithoutInterceptor client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(client)
            .baseUrl("http://" + BuildConfig.SERVER_IP + ":8080/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providesAuthService(@WithoutInterceptor retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun providesBoardService(retrofit: Retrofit): BoardService =
        retrofit.create(BoardService::class.java)

    @Provides
    @Singleton
    fun providesChatService(retrofit: Retrofit): ChatService =
        retrofit.create(ChatService::class.java)

    @Provides
    @Singleton
    fun providesJoinService(@WithoutInterceptor retrofit: Retrofit): JoinService =
        retrofit.create(JoinService::class.java)

    @Provides
    @Singleton
    fun providesMatchingService(retrofit: Retrofit): MatchingService =
        retrofit.create(MatchingService::class.java)

    @Provides
    @Singleton
    fun providesMemberService(retrofit: Retrofit): MemberService =
        retrofit.create(MemberService::class.java)

    @Provides
    @Singleton
    fun providesNotificationService(retrofit: Retrofit): NotificationService =
        retrofit.create(NotificationService::class.java)

    @Provides
    @Singleton
    fun providesQuestionService(retrofit: Retrofit): QuestionService =
        retrofit.create(QuestionService::class.java)
}