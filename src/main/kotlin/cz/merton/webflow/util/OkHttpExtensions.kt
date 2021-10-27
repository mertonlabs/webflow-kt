/*
 * Copyright (c) 2021 Merton Labs s.r.o
 * Author: rvbiljouw
 * License: MIT
 */
package cz.merton.webflow.util

import okhttp3.*
import java.util.logging.Logger

fun String.toJSONPayload(): RequestBody {
    return RequestBody.create(MediaType.get("application/json"), this)
}

fun OkHttpClient.get(url: String): Request.Builder {
    return Request.Builder()
        .url(url)
        .get()
}

fun OkHttpClient.delete(url: String): Request.Builder {
    return Request.Builder()
        .url(url)
        .delete()
}


fun OkHttpClient.post(url: String, body: RequestBody): Request.Builder {
    return Request.Builder()
        .url(url)
        .post(body)
}

fun OkHttpClient.patch(url: String, body: RequestBody): Request.Builder {
    return Request.Builder()
        .url(url)
        .patch(body)
}


fun OkHttpClient.execute(request: Request): Response {
    val call = newCall(request).execute()
    if (!call.isSuccessful && call.code() == 429) {
        Logger.getLogger("Webflow").warning("Rate limit hit - sleeping for 1m")
        Thread.sleep(60000)
        return execute(request)
    }
    return call
}
