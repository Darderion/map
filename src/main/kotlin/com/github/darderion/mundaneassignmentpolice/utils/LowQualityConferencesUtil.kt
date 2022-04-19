package com.github.darderion.mundaneassignmentpolice.utils

import org.jsoup.Jsoup
import java.time.Duration
import java.time.LocalDateTime

class LowQualityConferencesUtil {
	companion object {

		private const val beallslistURL = "https://beallslist.net/"

		private const val dataValidityPeriodInHours = 10

		private var lastDataUpdatingTime: LocalDateTime = LocalDateTime.MIN

		private var lowQualityConferencesList = listOf<String>()

		fun getList(): List<String> {
			val timeElapsedSinceLastParsing =
				Duration.between(lastDataUpdatingTime, LocalDateTime.now())

			if (timeElapsedSinceLastParsing.toHours() > dataValidityPeriodInHours) {
				lowQualityConferencesList = parseBeallslist()
				lastDataUpdatingTime = LocalDateTime.now()
			}

			return lowQualityConferencesList
		}

		private fun parseBeallslist(): List<String> {
			val document = Jsoup.connect(beallslistURL).get()
			val beallslist = document.getElementsByClass("wp-block-column")
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