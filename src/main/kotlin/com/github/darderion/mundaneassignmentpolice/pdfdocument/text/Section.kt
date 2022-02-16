package com.github.darderion.mundaneassignmentpolice.pdfdocument.text

data class Section(
	val title: String,
	val titleIndex: Int,
	val contentIndex: Int = titleIndex + 1) {
}
