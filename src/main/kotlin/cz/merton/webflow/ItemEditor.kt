/*
 * Copyright (c) 2021 Merton Labs s.r.o.
 * Author: rvbiljouw
 * License: MIT
 */

package cz.merton.webflow

import com.fasterxml.jackson.module.kotlin.readValue
import cz.merton.webflow.util.execute
import cz.merton.webflow.util.patch
import cz.merton.webflow.util.post
import cz.merton.webflow.util.toJSONPayload
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.logging.Logger

class ItemEditor(
    private val webflow: Webflow,
    private val collection: CollectionModel,
    private val item: Item? = null,
) {
    companion object {
        private val logger = Logger.getLogger("ItemEditor")
    }

    private val mapper = webflow.defaultMapper()
    private val edits = mutableMapOf<String, Any?>()

    init {
        if(item != null) {
            edits["_archived"] = item.archived
            edits["_draft"] = item.draft
        } else {
            edits["_archived"] = false
            edits["_draft"] = false
        }
    }

    @Throws(WebflowException::class)
    fun set(key: String, value: Any?): ItemEditor {
        val hasOriginal = collection.fields.any { it.slug == key }
        if (!hasOriginal) {
            logger.severe("Available fields: ${collection.fields}")
            throw WebflowException("Can't set field \"$key\" because it doesn't exist on the item.")
        }
        edits[key] = value
        return this
    }

    @Throws(WebflowException::class)
    fun commit(): Item? = if (item == null) create() else update()

    @Throws(WebflowException::class)
    private fun update(): Item? {
        try {
            val requestBody = RequestBody.create(
                MediaType.get("application/json"), mapper.writeValueAsString(mapOf("fields" to edits))
            )

            val request = webflow.client.patch(
                url = "${Webflow.ApiBase}/collections/${item?.cid}/items/${item?.id}",
                body = requestBody
            ).build()

            val response = webflow.client.execute(request)
            return if (response.isSuccessful) {
                mapper.readValue(response.body()?.string() ?: "{}")
            } else {
                logger.severe("Request failed: $response | ${response.body()?.string()})")
                null
            }
        } catch (e: Exception) {
            throw WebflowException("An exception occurred.", e)
        }
    }

    @Throws(WebflowException::class)
    private fun create(): Item? {
        return try {
            val requestBody = mapper.writeValueAsString(
                mapOf("collection_id" to collection.id, "fields" to edits)
            ).toJSONPayload()

            val request = webflow.client.post(
                url = "${Webflow.ApiBase}/collections/${collection.id}/items",
                body = requestBody
            ).build()

            val response = webflow.client.execute(request)
            if (response.isSuccessful) {
                mapper.readValue(response.body()?.string() ?: "{}")
            } else {
                logger.severe("Request failed: $response | ${response.body()?.string()})")
                null
            }
        } catch (e: Exception) {
            throw WebflowException("An exception occurred.", e)
        }
    }

}
