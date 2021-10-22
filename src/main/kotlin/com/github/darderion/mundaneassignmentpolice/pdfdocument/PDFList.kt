package com.github.darderion.mundaneassignmentpolice.pdfdocument

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Text

/*
Reasons for creating new gaming consoles:
1. To get more revenue in forms of:
	1.1. Dollars
	1.2. Yens
2. To get a product that can be advertised

<List>
	<Text></Text>
	<Nodes>
		<Node>
			<Text>To get more revenue in forms of:</Text>
			<Nodes>
				<Node><Text>Dollars</Text><Nodes></Nodes></Node>
				<Node><Text>Yens</Text><Nodes></Nodes></Node>
			</Nodes>
		</Node>
		<Node>
			<Text>To get a product that can be advertised</Text>
			<Nodes></Nodes>
		</Node>
	</Nodes>
</List>
 */

data class PDFList<T>(val value: MutableList<T> = mutableListOf(), val nodes: MutableList<PDFList<T>> = mutableListOf()) {
	constructor(item: T): this(mutableListOf(item))
	constructor(item: T, nodes: List<PDFList<T>>): this(mutableListOf(item), nodes.toMutableList())
}
