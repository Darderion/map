package com.github.darderion.mundaneassignmentpolice.pdfdocument

infix fun PDFArea.inside(region: PDFRegion) = region.contains(this)

class PDFRegion private constructor(areas: Set<PDFArea>) {
	private val areas: MutableSet<PDFArea>

	fun except(vararg areas: PDFArea): PDFRegion {
		areas.toSet().forEach {
			if (this.areas.contains(it)) {
				this.areas.remove(it)
			} else {
				this.areas.add(it)
			}
		}
		return this
	}

	init {
		this.areas = areas.toMutableSet()
	}

	fun contains(area: PDFArea) = areas.contains(area)

	companion object {
		val EVERYWHERE: PDFRegion
		get() = PDFRegion(PDFArea.values().toSet())

		val NOWHERE: PDFRegion
		get() = PDFRegion(setOf())
	}
}
