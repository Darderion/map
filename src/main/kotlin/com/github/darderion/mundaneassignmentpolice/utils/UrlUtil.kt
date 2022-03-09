package com.github.darderion.mundaneassignmentpolice.utils

import java.net.HttpURLConnection
import java.net.URL

class UrlUtil {
    companion object {
        fun isShortened(url: URL) = !expand(url).host.contains(url.host, true)


        fun expand(shortenedUrl: URL): URL {
            val connection = shortenedUrl.openConnection() as HttpURLConnection
            try {
                with(connection) {
                    instanceFollowRedirects = true
                    requestMethod = "HEAD"
                    connect()
                    return when (responseCode) {
                        301 -> {  // HttpURLConnection doesn't automatically follow redirects from a protocol to another
                            val redirect = headerFields.filter {
                                it.key.equals("location", true)
                            }.map { it.value.first() }.first()
                            expand(URL(redirect))
                        }
                        HttpURLConnection.HTTP_OK -> this.url
                        else -> throw UnsupportedOperationException("Unexpected HTTP status code")
                    }
                }
            } catch (e: Exception) {
                throw UnsupportedOperationException(e)
            }
            finally {
                connection.disconnect()
            }
        }
    }
}