package com.github.darderion.mundaneassignmentpolice.rules

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.url.then
import com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.urlbuilder.URLRuleBuilder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.utils.InvalidOperationException
import com.github.darderion.mundaneassignmentpolice.utils.LowQualityConferencesUtil
import com.github.darderion.mundaneassignmentpolice.utils.URLUtil

const val shortenedUrlRuleName = "Сокращённая ссылка"
val shortenedUrlRuleArea = PDFRegion.NOWHERE.except(PDFArea.FOOTNOTE, PDFArea.BIBLIOGRAPHY)

val urlShortenersListRule = URLRuleBuilder<URLRuleBuilder<*>>()
        .called(shortenedUrlRuleName)
        .inArea(shortenedUrlRuleArea)
        .type(RuleViolationType.Error)
        .disallow { urls ->
            val urlShorteners = URLUtil.getUrlShorteners()
            urls.filter { url ->
                urlShorteners.any { URLUtil.equalDomainName(it, url.text) }
            }.map { it to it.lines }
        }.getRule()

val shortUrlRule = URLRuleBuilder<URLRuleBuilder<*>>()
        .called(shortenedUrlRuleName)
        .inArea(shortenedUrlRuleArea)
        .type(RuleViolationType.Warning)
        .disallow { urls ->
            urls.filter { url ->
                URLUtil.getDomainName(url.text).replace(".", "").length in (3..5)
            }.map { it to it.lines }
        }.getRule()

val allowedDomainNamesWithRedirect = listOf("doi.org", "dx.doi.org")

val urlWithRedirectRule = URLRuleBuilder<URLRuleBuilder<*>>()
        .called(shortenedUrlRuleName)
        .inArea(shortenedUrlRuleArea)
        .type(RuleViolationType.Warning)
        .disallow { urls ->
            urls.filterNot { url ->
                allowedDomainNamesWithRedirect.any { URLUtil.equalDomainName(it, url.text) }
            }.filter { url ->
                try {
                    URLUtil.isRedirect(url.text)
                } catch (_: InvalidOperationException) {
                    false
                }
            }.map { it to it.lines }
        }.getRule()

val RULE_SHORTENED_URLS = urlShortenersListRule then shortUrlRule then urlWithRedirectRule

val RULE_URLS_UNIFORMITY = URLRuleBuilder<URLRuleBuilder<*>>()
        .called("Ссылки разных видов")
        .inArea(PDFRegion.NOWHERE.except(PDFArea.FOOTNOTE, PDFArea.BIBLIOGRAPHY))
        .disallow { urls ->
            var filteredUrls = urls.filter { url ->
                !url.text.startsWith("https://www")
            }
            if (urls.size == filteredUrls.size) {
                filteredUrls = filteredUrls.filter { url ->
                    !url.text.startsWith("www")
                }
                if (urls.size == filteredUrls.size) {
                    filteredUrls = filteredUrls.filter { url ->
                        !url.text.startsWith("htt")
                    }
                }
            }
            filteredUrls.map { it to it.lines }
        }.getRule()



val RULE_LOW_QUALITY_CONFERENCES = URLRuleBuilder<URLRuleBuilder<*>>()
        .called("Ссылка на низкокачественную конференцию")
        .inArea(PDFArea.BIBLIOGRAPHY)
        .disallow { urls ->
            val lowQualityConferencesList = LowQualityConferencesUtil.getList()
                    .map {
                        it.removePrefix("http://").removePrefix("https://")
                    }
            urls.filter { url ->
                lowQualityConferencesList
                        .any { conference -> url.text.contains(conference) }
            }.map { it to it.lines }
        }.getRule()