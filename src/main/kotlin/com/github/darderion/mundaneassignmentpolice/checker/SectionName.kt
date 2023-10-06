package com.github.darderion.mundaneassignmentpolice.checker

enum class SectionName(val title: String) {
	TABLE_OF_CONTENT("Оглавление"),
	INTRODUCTION("Введение"),
	PROBLEM_STATEMENT("Постановка задачи"),
	REVIEW("Обзор"),
	CONTENT("Контент"),
	CONCLUSION("Заключение"),
	BIBLIOGRAPHY("Список литературы");

	companion object {
		infix fun getByTitle(title: String) = values().find { it.title == title } ?: CONTENT
	}
}
