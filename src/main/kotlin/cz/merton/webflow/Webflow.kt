/*
 * Copyright (c) 2021 Merton Labs s.r.o.
 * Author: rvbiljouw
 * License: MIT
 */

package cz.merton.webflow

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.OkHttpClient

class Webflow(private val apiToken: String) {
    companion object {
        internal val ApiBase = "https://api.webflow.com"
    }

    internal val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .header("Authorization", "Bearer $apiToken")
                .header("accept-version", "1.0.0")
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }.build()

    fun defaultMapper(): ObjectMapper {
        return ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .registerModule(KotlinModule.Builder().build())
            .registerModule(JavaTimeModule())
    }

    fun sites(): Sites {
        return Sites(this)
    }

    fun collections(): Collections {
        return Collections(this)
    }

    fun items(collectionId: String): Items {
        return Items(this, collectionId)
    }

}
