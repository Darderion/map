package com.github.darderion.mundaneassignmentpolice

import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
open class MundaneAssignmentPoliceApplication

fun main(args: Array<String>) {
	runApplication<MundaneAssignmentPoliceApplication>(*args)
}
