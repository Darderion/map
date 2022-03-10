package com.github.darderion.mundaneassignmentpolice.utils

import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class URLUtil {
    companion object {
        fun getUrl(url: String) =
            try {
                URL(url)
            } catch (e: MalformedURLException) {
                throw IllegalArgumentException("Incorrect url: $url", e)
            }

        fun isShortened(url: String): Boolean {
            val currentUrl = getUrl(url)
            val expandedUrl = getUrl(expand(url))
            return !expandedUrl.host.contains(currentUrl.host, true)
        }

        fun expand(shortenedUrl: String): String {
            val url = getUrl(shortenedUrl)
            val connection = url.openConnection() as HttpURLConnection
            try {
               return getExpandedUrl(connection)
            } catch (e: Exception) {
                when (e) {
                    is InvalidOperationException -> throw e
                    else -> throw InvalidOperationException(e)
                }
            } finally {
                connection.disconnect()
            }
        }

        private fun getExpandedUrl(connection: HttpURLConnection): String {
            with(connection) {
                instanceFollowRedirects = true
                requestMethod = "HEAD"
                connect()
                return when (responseCode) {
                    301 -> {  // HttpURLConnection doesn't automatically follow redirects from a protocol to another
                        val redirect = headerFields.filter {
                            it.key.equals("location", true)
                        }.map { it.value.first() }.first()
                        expand(redirect)
                    }
                    HttpURLConnection.HTTP_OK -> this.url.toString()
                    else -> throw InvalidOperationException("Unexpected HTTP status code")
                }
            }
        }
    }
}

class InvalidOperationException: Exception {
    constructor(message: String): super(message)
    constructor(cause: Throwable): super(cause)
}