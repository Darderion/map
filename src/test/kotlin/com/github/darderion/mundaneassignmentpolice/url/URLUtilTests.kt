package com.github.darderion.mundaneassignmentpolice.url

import com.github.darderion.mundaneassignmentpolice.utils.URLUtil
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class URLUtilTests: StringSpec({
    "Function getUrl should return correct URL object" {
        val url = "https://google.com"
        URLUtil.getUrl(url).toString() shouldBe url
    }

    "Function getUrl should throw IllegalArgumentException if url was incorrect" {
        val url = "justString"
        shouldThrow<IllegalArgumentException> {
            URLUtil.getUrl(url)
        }
    }

    "URLUtil should distinguish between common and shortened urls" {
        val url = "https://google.com"
        URLUtil.isShortened(url).shouldBeFalse()

        val shortenedUrl = "https://cutt.ly/VAOZr94"
        URLUtil.isShortened(shortenedUrl).shouldBeTrue()
    }

    "URLUtil should expand various shortener URLs" {
        val urls = listOf(
            "https://cutt.ly/VAOZr94",
            "https://is.gd/gZgSmH",
            "https://bit.ly/3KvfLJR",
            "https://shorturl.at/kHQT5"
        )
        urls.forEach { URLUtil.expand(it) shouldBe "https://www.google.com/" }
    }
})