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
