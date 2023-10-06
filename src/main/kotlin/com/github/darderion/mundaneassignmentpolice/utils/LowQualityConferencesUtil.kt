package com.github.darderion.mundaneassignmentpolice.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime

class LowQualityConferencesUtil {
	companion object {

		private const val beallslistURL = "https://beallslist.net/"

		private const val beallslistFilePath = "beallslist.html"

		private const val dataValidityPeriodInHours = 10

		private var lastDataUpdatingTime: LocalDateTime = LocalDateTime.MIN

		private var lowQualityConferencesList = listOf<String>()

		fun getList(): List<String> {
			val timeElapsedSinceLastParsing =
				Duration.between(lastDataUpdatingTime, LocalDateTime.now())

			if (timeElapsedSinceLastParsing.toHours() > dataValidityPeriodInHours) {
				lowQualityConferencesList = parseBeallslist(getBeallslistDocument())
				lastDataUpdatingTime = LocalDateTime.now()
			}

			return lowQualityConferencesList
		}

		private fun getBeallslistDocument() : Document {
			return try {
				Jsoup.connect(beallslistURL)
					.timeout(20 * 1000)    //timeout in millis
					.get()
			} catch (e: IOException){
				Jsoup.parse(ResourcesUtil.getResourceText(beallslistFilePath))
			}
		}

		private fun parseBeallslist(beallslistDocument: Document): List<String> {
			val beallslist = beallslistDocument.getElementsByClass("wp-block-column")
				.first()!!.getElementsByTag("ul")
				.slice(IntRange(0, 1))
				.map { ul ->
					ul.getElementsByTag("a")
						.map { li -> li.attr("href") }
				}
				.flatten()

			return beallslist
		}
	}
}
