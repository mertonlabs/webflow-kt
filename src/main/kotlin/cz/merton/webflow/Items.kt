/*
 * Copyright (c) 2021 Merton Labs s.r.o.
 * Author: rvbiljouw
 * License: MIT
 */

package cz.merton.webflow

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.readValue
import cz.merton.webflow.util.delete
import cz.merton.webflow.util.execute
import cz.merton.webflow.util.get
import java.util.logging.Logger


data class ItemSet(
    val items: List<Item>,
    val count: Long,
    val limit: Long,
    val offset: Long,
    val total: Long
)

data class Item(
    @JsonProperty("_archived") val archived: Boolean,
    @JsonProperty("_draft") val draft: Boolean,
    @JsonProperty("_id") val id: String,
    @JsonProperty("_cid") val cid: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("slug") val slug: String,
    @JsonIgnore @JsonAnyGetter @JsonAnySetter val fields: MutableMap<String, Any> = mutableMapOf()
)

class Items(private val webflow: Webflow, private val collectionId: String) {
    companion object {
        private val logger = Logger.getLogger("Items")
    }

    private val mapper = webflow.defaultMapper()

    @Throws(WebflowException::class)
    fun list(): ItemSet? {
        return try {
            val request = webflow.client.get("${Webflow.ApiBase}/collections/${collectionId}/items").build()
            val response = webflow.client.execute(request)
            if (response.isSuccessful) {
                mapper.readValue(response.body?.string() ?: "{}")
            } else {
                logger.severe("Request failed: $response | ${response.body?.string()})")
                null
            }
        } catch (e: Exception) {
            throw WebflowException("An exception occurred.", e)
        }
    }

    @Throws(WebflowException::class)
    fun get(itemId: String): Item? {
        return try {
            val request = webflow.client.get("${Webflow.ApiBase}/collections/$collectionId/items/$itemId").build()
            val response = webflow.client.execute(request)
            if (response.isSuccessful) {
                mapper.readValue(response.body?.string() ?: "{}")
            } else {
                logger.severe("Request failed: $response | ${response.body?.string()})")
                null
            }
        } catch (e: Exception) {
            throw WebflowException("An exception occurred.", e)
        }
    }

    @Throws(WebflowException::class)
    fun delete(itemId: String): Boolean {
        return try {
            val request = webflow.client.delete("${Webflow.ApiBase}/collections/$collectionId/items/$itemId").build()
            val response = webflow.client.execute(request)
            if (response.isSuccessful) {
                val body: Map<String, Int> = mapper.readValue(response.body?.string() ?: "{}")
                body.containsKey("deleted") && body["deleted"]!! >= 0
            } else {
                logger.severe("Request failed: $response | ${response.body?.string()})")
                false
            }
        } catch (e: Exception) {
            throw WebflowException("An exception occurred.", e)
        }
    }

    @Throws(WebflowException::class)
    fun builder(): ItemEditor {
        val collectionModel = webflow.collections().get(collectionId)
            ?: throw WebflowException("Failed to fetch collection model with ID $collectionId")
        return ItemEditor(webflow, collectionModel)
    }

    @Throws(WebflowException::class)
    fun editor(item: Item): ItemEditor {
        val collectionModel = webflow.collections().get(collectionId)
            ?: throw WebflowException("Failed to fetch collection model with ID $collectionId")
        return ItemEditor(webflow, collectionModel, item)
    }

}
