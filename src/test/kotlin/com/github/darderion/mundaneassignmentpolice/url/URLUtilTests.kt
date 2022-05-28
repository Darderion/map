package com.github.darderion.mundaneassignmentpolice.url

import com.github.darderion.mundaneassignmentpolice.utils.URLUtil
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

    "Function getDomainName should return a domain name without a subdomain" {
        val url = "https://www.google.com"
        val domainName = "google.com"
        URLUtil.getDomainName(url) shouldBe domainName
    }

    "Function equalDomainName should return true if domain names are equal ignoring character case" {
        val urlA = "https://www.google.com"
        val urlB = "https://www.Google.com"
        URLUtil.equalDomainName(urlA, urlB).shouldBeTrue()
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
