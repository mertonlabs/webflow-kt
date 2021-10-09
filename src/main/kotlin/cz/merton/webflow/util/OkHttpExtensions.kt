/*
 * Copyright (c) 2021 Merton Labs s.r.o
 * Author: rvbiljouw
 * License: MIT
 */
package cz.merton.webflow.util

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

fun String.toJSONPayload(): RequestBody {
    return this.toRequestBody("application/json".toMediaType())
}

fun OkHttpClient.get(url: String): Request.Builder {
    return Request.Builder()
        .url(url)
        .get()
}

fun OkHttpClient.post(url: String, body: RequestBody): Request.Builder {
    return Request.Builder()
        .url(url)
        .post(body)
}

fun OkHttpClient.patch(url: String, body: RequestBody): Request.Builder {
    return Request.Builder()
        .url(url)
        .post(body)
}


fun OkHttpClient.execute(request: Request): Response {
    return newCall(request).execute()
}
