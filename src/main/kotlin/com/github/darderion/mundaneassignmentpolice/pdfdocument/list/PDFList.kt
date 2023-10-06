package com.github.darderion.mundaneassignmentpolice.pdfdocument.list

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Coordinate
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Font
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Word
import java.util.*

data class PDFList<T>(val value: MutableList<T> = mutableListOf(), val nodes: MutableList<PDFList<T>> = mutableListOf()) {
	constructor(item: T): this(mutableListOf(item))
	constructor(item: T, nodes: List<PDFList<T>>): this(mutableListOf(item), nodes.toMutableList())

	fun <Item> map(map: (value: T) -> Item): PDFList<Item> = PDFList(value.map { map(it) }.toMutableList(), nodes.map { it.map(map) }.toMutableList())

	override fun toString() = nodes.joinToString("\n") { it.toStringWithText() }

	fun text() = value.joinToString("\n")

	fun getText(): List<T> {
		val list = mutableListOf<T>()
		value.forEach { list.add(it) }
		nodes.forEach { list.addAll(it.getText()) }
		return list
	}

	private fun toStringWithText(listCount: Int = 1, map: ((value: T) -> String)? = null): String {
		var line = ""
		value.forEach {
			for (i in 0 until listCount) line += "->"
			line += "${
				if (map == null) it.toString() else map(it)
			}\n"
		}
		nodes.forEach {
			line += it.toStringWithText(listCount + 1, map)
		}
		return line
	}

	fun getSublists(): List<PDFList<T>> {
		val sublists = mutableListOf(this)
		sublists.addAll(this.nodes.map { it.getSublists() }.flatten())

		return sublists
	}

	companion object {
		/**
		 * Get a list of all <ENUMERATE> and <ITEMIZE> lists from a PDF
		 * This only works if the deepest sublist of a list is no deeper than SUB-SUB-SUB-list
		 * This method can't process nested lists of different types
		 * @param lines Text lines
		 * @return List of <ENUMERATE> and <ITEMIZE> lists
		 */
		fun getLists(lines: List<Line>): List<PDFList<Line>> {
			// Adding a line to process a text that has no lines after a list
			val lines = lines + Line(-1, -1, -1, listOf(Word("NOT A LIST ITEM", Font(0.0f), Coordinate(1000, -1))),null,Coordinate(0, 0))

			val lists: MutableList<PDFList<Line>> = mutableListOf()
			val stack: Stack<PDFList<Line>> = Stack()
			var previousPosition: Coordinate
			var previousList: PDFList<Line>? = null

			lines.forEach { line ->
				if (stack.isEmpty()) {
					if (line.first == LIST_ENUMERATE_FIRST_ITEM || line.first == LIST_ITEMIZE_FIRST_ITEM) {
						stack.add(PDFList(line.drop(2)))
						stack.peek().nodes.add(PDFList(line.drop(2)))
						stack.push(stack.peek().nodes.first())
					}
				} else {
					previousPosition = stack.peek().value.first().position
					if (previousPosition hasSameXAs line.position) {					//	1.	lorem	OR	lorem
						stack.peek().value.add(line)									//		lorem		lorem
					} else {
						if (previousPosition.x < line.position.x) {
							if (isListItem(line)) {
								stack.peek().nodes.add(PDFList(line.drop(2)))	// lorem
								stack.push(stack.peek().nodes.last())						//		1. lorem
							} else {
								lists.add(stack.first())
								stack.clear()
							}
						} else {							//		lorem	OR		lorem	OR		...	lorem	OR		... lorem
							while (!(	stack.isEmpty() ||	//	lorem			2. lorem		lorem				2. lorem
										(isListItem(line) && previousPosition hasSameXAs line.drop(2).position) ||
										previousPosition hasSameXAs line.position)) {
								previousList = stack.pop()
								if (stack.isNotEmpty()) {
									previousPosition = stack.peek().value.first().position
								}
							}
							if (stack.isEmpty()) {
								lists.add(previousList!!)
							} else {
								if (previousPosition hasSameXAs line.position) {		//		lorem
									stack.peek().value.add(line)						//	lorem
								} else {
									stack.pop()
									stack.peek().nodes.add(PDFList(line.drop(2)))	//		lorem	OR			lorem
									stack.push(stack.peek().nodes.last())						//	2.	lorem		2. lorem
								}
							}
						}
					}
				}
			}

			return lists
		}

		private fun isListItem(line: Line) =
			"•–*·".contains(line.first!!) || (
					(line.first!!.contains(".") ||
							(line.first!!.contains("(") && line.first!!.contains(")") && line.first!!.length == 3)) &&
							line.first!!.replace(".", "").isNotEmpty() &&
							line.first!!.length <= 3)

		private const val LIST_ENUMERATE_FIRST_ITEM = "1."
		private const val LIST_ITEMIZE_FIRST_ITEM = "•"
	}
}
