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
import java.time.ZonedDateTime
import java.util.logging.Logger

fun List<Site>.named(name: String): Site? {
    return firstOrNull { it.name == name }
}

fun List<CollectionModel>.named(name: String): CollectionModel? {
    return firstOrNull { it.name == name }
}

data class CollectionModel(
    @JsonProperty("_id") val id: String,
    val lastUpdated: ZonedDateTime?,
    val createdOn: ZonedDateTime,
    val name: String,
    val slug: String,
    val singularName: String,
    val fields: List<Field> = listOf()
)

data class Field(
    val id: String,
    val type: Type,
    val slug: String,
    val name: String,
    val required: Boolean,
    val editable: Boolean
) {

    enum class Type {
        Bool,
        Color,
        Date,
        ExtFileRef,
        ImageRef,
        ItemRef,
        ItemRefSet,
        Link,
        Number,
        Option,
        PlainText,
        RichText,
        Video,
        User
    }

}

class Collections(
    private val webflow: Webflow
) {

    companion object {
        private val logger = Logger.getLogger("Collections")
    }

    private val mapper = webflow.defaultMapper()

    @Throws(WebflowException::class)
    fun list(siteId: String): List<CollectionModel> {
        return try {
            val request = webflow.client.get("${Webflow.ApiBase}/sites/$siteId/collections").build()
            val response = webflow.client.execute(request)
            if (response.isSuccessful) {
                mapper.readValue(response.body?.string() ?: "[]")
            } else {
                logger.severe("Request failed: $response | ${response.body?.string()})")
                emptyList()
            }
        } catch (e: Exception) {
            throw WebflowException("An exception occurred.", e)
        }
    }

    @Throws(WebflowException::class)
    fun get(collectionId: String): CollectionModel? {
        return try {
            val request = webflow.client.get("${Webflow.ApiBase}/collections/$collectionId").build()
            val response = webflow.client.execute(request)
            if (response.isSuccessful && response.body != null) {
                mapper.readValue(response.body!!.string())
            } else {
                logger.severe("Request failed: $response | ${response.body?.string()})")
                null
            }
        } catch (e: Exception) {
            throw WebflowException("An exception occurred.", e)
        }
    }

}
