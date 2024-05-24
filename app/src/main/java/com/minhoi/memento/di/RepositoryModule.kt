package com.minhoi.memento.di

import com.minhoi.memento.repository.board.BoardRepository
import com.minhoi.memento.repository.board.BoardRepositoryImpl
import com.minhoi.memento.repository.chat.ChatRepository
import com.minhoi.memento.repository.chat.ChatRepositoryImpl
import com.minhoi.memento.repository.join.JoinRepository
import com.minhoi.memento.repository.join.JoinRepositoryImpl
import com.minhoi.memento.repository.login.LoginRepository
import com.minhoi.memento.repository.login.LoginRepositoryImpl
import com.minhoi.memento.repository.member.MemberRepository
import com.minhoi.memento.repository.member.MemberRepositoryImpl
import com.minhoi.memento.repository.question.QuestionRepository
import com.minhoi.memento.repository.question.QuestionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsBoardRepository(
        boardRepositoryImpl: BoardRepositoryImpl,
    ): BoardRepository

    @Binds
    @Singleton
    abstract fun bindsChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl,
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindsJoinRepository(
        joinRepositoryImpl: JoinRepositoryImpl,
    ): JoinRepository

    @Binds
    @Singleton
    abstract fun bindsLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl,
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindsMemberRepository(
        memberRepositoryImpl: MemberRepositoryImpl,
    ): MemberRepository

    @Binds
    @Singleton
    abstract fun bindsQuestionRepository(
        questionRepositoryImpl: QuestionRepositoryImpl,
    ): QuestionRepository

}