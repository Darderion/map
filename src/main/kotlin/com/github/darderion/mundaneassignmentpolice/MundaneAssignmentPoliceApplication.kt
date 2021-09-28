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

fun testPDFBox() {
	PDDocument.load(File("/home/dio/projects/kotlin/mundane-assignment-police/src/test/pdf.pdf")).use { document ->
		val ap: AccessPermission = document.getCurrentAccessPermission()
		if (!ap.canExtractContent()) {
			throw IOException("You do not have permission to extract text")
		}
		val stripper = PDFTextStripper()

		// This example uses sorting, but in some cases it is more useful to switch it off,
		// e.g. in some files with columns where the PDF content stream respects the
		// column order.
		stripper.sortByPosition = true
		for (p in 1..document.getNumberOfPages()) {
			// Set the page interval to extract. If you don't, then all pages would be extracted.
			stripper.startPage = p
			stripper.endPage = p

			// let the magic happen
			val text = stripper.getText(document)

			// do some nice output with a header
			val pageStr = String.format("page %d:", p)
			println(pageStr)
			for (i in 0 until pageStr.length) {
				print("-")
			}
			println()
			println(text.trim { it <= ' ' })
			println()

			// If the extracted text is empty or gibberish, please try extracting text
			// with Adobe Reader first before asking for help. Also read the FAQ
			// on the website:
			// https://pdfbox.apache.org/2.0/faq.html#text-extraction
		}
	}
}

@Throws(IOException::class)
fun getImagesFromPDF(document: PDDocument): List<RenderedImage>? {
	val images: MutableList<RenderedImage> = ArrayList()
	for (page in document.pages) {
		images.addAll(getImagesFromResources(page.resources)!!)
	}
	return images
}

@Throws(IOException::class)
private fun getImagesFromResources(resources: PDResources): List<RenderedImage>? {
	val images: MutableList<RenderedImage> = ArrayList()
	for (xObjectName in resources.xObjectNames) {
		val xObject = resources.getXObject(xObjectName)
		if (xObject is PDFormXObject) {
			images.addAll(getImagesFromResources(xObject.resources)!!)
		} else if (xObject is PDImageXObject) {
			images.add(xObject.image)
		}
	}
	return images
}

fun main(args: Array<String>) {
	runApplication<MundaneAssignmentPoliceApplication>(*args)
}
