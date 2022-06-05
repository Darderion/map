package com.github.darderion.mundaneassignmentpolice.utils

import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class URLUtil {
    companion object {
        fun getUrl(url: String) =
            try {
                if (url.startsWith("http")) URL(url) else URL("http://$url")
            } catch (e: MalformedURLException) {
                throw IllegalArgumentException("""Incorrect URL: "$url"""", e)
            }

        fun removeProtocol(url: String) = url.removePrefix("http://").removePrefix("https://")

        fun getDomainName(url: String) = getUrl(url).host.removePrefix("www.")

        fun partAfterDomain(url: String) = removeProtocol(url).dropWhile { it != '/' }

        fun equalDomainName(urlA: String, urlB: String) = getDomainName(urlA).equals(getDomainName(urlB), true)

        fun isRedirect(url: String) = !equalDomainName(url, expand(url))

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
                connectTimeout = 2000
                connect()
                return when (responseCode) {
                    in 300..399 -> {
                        // HttpURLConnection doesn't automatically follow redirects from one protocol to another
                        val redirect = headerFields.filter {
                            it.key.equals("location", true)
                        }.map { it.value.first() }.first()
                        expand(redirect)
                    }
                    HttpURLConnection.HTTP_OK -> this.url.toString()
                    else -> throw InvalidOperationException("Unexpected HTTP status code: $responseCode")
                }
            }
        }
    }
}

class InvalidOperationException : Exception {
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
}
