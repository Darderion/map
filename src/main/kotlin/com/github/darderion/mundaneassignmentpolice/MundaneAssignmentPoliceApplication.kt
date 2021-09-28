package com.github.darderion.mundaneassignmentpolice

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.encryption.AccessPermission
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.awt.image.RenderedImage
import java.io.File
import java.io.IOException


@SpringBootApplication
open class MundaneAssignmentPoliceApplication

fun main(args: Array<String>) {
	runApplication<MundaneAssignmentPoliceApplication>(*args)
}
