package com.github.darderion.mundaneassignmentpolice.pdfdocument.list

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Coordinate
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Font
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Text
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Word
import java.util.*

data class PDFList<T>(val value: MutableList<T> = mutableListOf(), val nodes: MutableList<PDFList<T>> = mutableListOf()) {
	constructor(item: T): this(mutableListOf(item))
	constructor(item: T, nodes: List<PDFList<T>>): this(mutableListOf(item), nodes.toMutableList())

	fun print() {
		nodes.forEach { it.printWithText() }
	}

	private fun printWithText(listCount: Int = 1) {
		value.forEach {
			for (i in 0 until listCount) print("->")
			println(it)
		}
		nodes.forEach {
			it.printWithText(listCount + 1)
		}
	}

	companion object {
		fun getLists(lines: List<Text>): List<PDFList<Text>> {
			// Adding a line to process a text that has no lines after a list
			val lines = lines + Text(-1, -1, -1, listOf(Word("NOT A LIST ITEM", Font(0.0f), Coordinate(1000, -1))))

			val lists: MutableList<PDFList<Text>> = mutableListOf()
			val stack: Stack<PDFList<Text>> = Stack()
			var previousPosition: Coordinate
			var previousList: PDFList<Text>? = null

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
						} else {
							while (!(	stack.isEmpty() ||
										previousPosition hasSameXAs line.drop(2).position ||
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

		private fun isListItem(text: Text) =
			"•–*·".contains(text.first!!) || (
					(text.first!!.contains(".") ||
							(text.first!!.contains("(") && text.first!!.contains(")") && text.first!!.length == 3)) &&
							text.first!!.replace(".", "").isNotEmpty() &&
							text.first!!.length <= 3)

		private const val LIST_ENUMERATE_FIRST_ITEM = "1."
		private const val LIST_ITEMIZE_FIRST_ITEM = "•"
	}
}
