package com.github.darderion.mundaneassignmentpolice.pdfdocument

infix fun PDFArea.inside(region: PDFRegion) = region.contains(this)

class PDFRegion private constructor(areas: Set<PDFArea>) {
	private val areas: MutableSet<PDFArea>

	/**
	 * Method that either adds or removes areas from region depending on if they were already in region or not
	 * @param areas List of areas to either add or remove from region
	 * @return PDFRegion
	 */
	fun except(vararg areas: PDFArea) = this.also {
		areas.toSet().forEach {
			if (this.areas.contains(it)) {
				this.areas.remove(it)
			} else {
				this.areas.add(it)
			}
		}
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
