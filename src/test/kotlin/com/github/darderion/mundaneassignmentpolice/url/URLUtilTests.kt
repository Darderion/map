package com.github.darderion.mundaneassignmentpolice.url

import com.github.darderion.mundaneassignmentpolice.utils.URLUtil
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject

class URLUtilTests: StringSpec({
    beforeTest {
        mockkObject(URLUtil)
        every { URLUtil.expand(any()) } returnsArgument 0
    }

    afterTest {
        unmockkObject(URLUtil)
    }

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
        val shortenedUrl = "https://t.ly/FX8U"
        val expandedUrl = "https://www.google.com/"

        every { URLUtil.expand(url) } returns expandedUrl
        every { URLUtil.expand(shortenedUrl) } returns expandedUrl

        URLUtil.isRedirect(url).shouldBeFalse()
        URLUtil.isRedirect(shortenedUrl).shouldBeTrue()
    }
})
