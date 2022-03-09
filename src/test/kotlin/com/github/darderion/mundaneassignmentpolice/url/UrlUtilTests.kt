package com.github.darderion.mundaneassignmentpolice.url

import com.github.darderion.mundaneassignmentpolice.utils.UrlUtil
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class UrlUtilTests: StringSpec({
    "Function getUrl should return correct URL object" {
        val url = "https://google.com"
        UrlUtil.getUrl(url).toString() shouldBe url
    }

    "Function getUrl should throw IllegalArgumentException if url was incorrect" {
        val url = "justString"
        shouldThrow<IllegalArgumentException> {
            UrlUtil.getUrl(url)
        }
    }

    "UrlUtil should distinguish between common and shortened urls" {
        val url = "https://google.com"
        UrlUtil.isShortened(url).shouldBeFalse()

        val shortenedUrl = "https://cutt.ly/VAOZr94"
        UrlUtil.isShortened(shortenedUrl).shouldBeTrue()
    }

    "UrlUtil should expand various shortener URLs" {
        val urls = listOf(
            "https://cutt.ly/VAOZr94",
            "https://is.gd/gZgSmH",
            "https://bit.ly/3KvfLJR",
            "https://shorturl.at/kHQT5"
        )
        urls.forEach { UrlUtil.expand(it) shouldBe "https://www.google.com/" }
    }
})