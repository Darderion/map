package com.github.darderion.mundaneassignmentpolice.pdfdocument.text

data class Section(val title: String, val titleIndex: Int, val sectionIndex: Int = titleIndex + 1) {
}
