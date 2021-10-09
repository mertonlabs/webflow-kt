/*
 * Copyright (c) 2021 Merton Labs s.r.o.
 * Author: rvbiljouw
 * License: MIT
 */

package cz.merton.webflow

class WebflowException(
    message: String,
    throwable: Throwable? = null
) : Exception(
    message,
    throwable
)
