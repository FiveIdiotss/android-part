package com.minhoi.memento.data.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonClient {
    val instance: Gson by lazy {
        GsonBuilder().create()
    }
}