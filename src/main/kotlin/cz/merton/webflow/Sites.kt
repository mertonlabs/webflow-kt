/*
 * Copyright (c) 2021 Merton Labs s.r.o.
 * Author: rvbiljouw
 * License: MIT
 */

package cz.merton.webflow

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.readValue
import cz.merton.webflow.util.execute
import cz.merton.webflow.util.get
import cz.merton.webflow.util.post
import okhttp3.MediaType
import okhttp3.RequestBody
import java.time.ZonedDateTime
import java.util.logging.Logger

data class Site(
    @JsonProperty("_id") val id: String,
    val createdOn: ZonedDateTime,
    val name: String,
    val shortName: String,
    val lastPublished: ZonedDateTime?,
    val previewUrl: String,
    val timezone: String,
    val database: String?
)

class Sites(private val webflow: Webflow) {
    companion object {
        private val logger = Logger.getLogger("Sites")
    }

    private val mapper = webflow.defaultMapper()

    @Throws(WebflowException::class)
    fun list(): List<Site> {
        return try {
            val request = webflow.client.get("${Webflow.ApiBase}/sites").build()
            val response = webflow.client.execute(request)
            if (response.isSuccessful) {
                mapper.readValue(response.body()?.string() ?: "[]")
            } else {
                logger.severe("Request failed: $response | ${response.body()?.string()})")
                emptyList()
            }
        } catch (e: Exception) {
            throw WebflowException("An exception occurred.", e)
        }
    }

    @Throws(WebflowException::class)
    fun get(siteId: String): Site? {
        return try {
            val request = webflow.client.get("${Webflow.ApiBase}/sites/$siteId").build()
            val response = webflow.client.execute(request)
            if (response.isSuccessful && response.body() != null) {
                mapper.readValue(response.body()!!.string())
            } else {
                logger.severe("Request failed: $response | ${response.body()?.string()})")
                null
            }
        } catch (e: Exception) {
            throw WebflowException("An exception occurred.", e)
        }
    }

    @Throws(WebflowException::class)
    fun publish(siteId: String, domains: List<String>): List<Site> {
        try {
            val requestBody = RequestBody.create(MediaType.get("application/json"), mapper.writeValueAsString(mapOf("domains" to domains)))
            val request = webflow.client.post(
                url = "${Webflow.ApiBase}/sites/$siteId/publish",
                body = requestBody
            ).build()

            val response = webflow.client.execute(request)
            if (response.isSuccessful) {
                return mapper.readValue(response.body()?.string() ?: "[]")
            }
            logger.severe("Request failed: $response | ${response.body()?.string()})")
            return emptyList()
        } catch (e: Exception) {
            throw WebflowException("An exception occurred.", e)
        }
    }


}
