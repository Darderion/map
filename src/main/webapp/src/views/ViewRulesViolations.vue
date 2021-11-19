
<template>
	<div>
		<ul id="rulesViolations">
			<li v-for="ruleViolation in this.$store.getters.getRuleViolations"
			:key="'rulesViolations'+ruleViolation.toString()">{{ruleViolation.message}} > Line {{ruleViolation.lines[0].line}}, page {{ruleViolation.lines[0].page}}</li>
		</ul>
		<div id="ruleViolation">
			<pdf
				:src="`api/viewPDF.pdf?pdfName=${this.$store.getters.getPdfName}`"
				:page="curPage + 1"
				style="display: inline-block; width: 100%"
			></pdf>
		</div>
	</div>
</template>

<script lang="ts">

import { Component, Prop, Vue } from 'vue-property-decorator';

import PDFTextComponent from '../components/PDFTextComponent.vue'
import pdf from 'vue-pdf'

@Component({
	components: {
		PDFTextComponent,
		pdf
	},
})

export default class ViewRulesViolations extends Vue {
	listElement = (document.getElementById('rulesViolations') as HTMLUListElement) ?
		document.getElementById('rulesViolations') as HTMLUListElement: undefined

	curPage = 0
	
	mounted() {
		if (this.listElement == undefined) {
			this.listElement = document.getElementById('rulesViolations') as HTMLUListElement
		}
		this.listElement.onclick = (event: MouseEvent) => {
			// eslint-disable-next-line @typescript-eslint/ban-ts-comment
			// @ts-ignore: Object is possibly 'null'.
			const element = (event.target as HTMLElement)
			if (element.tagName != "LI") return;
			
			this.singleSelect(element);
		}

		// prevent unneeded selection of list elements on clicks
		this.listElement.onmousedown = function() {
			return false;
		};
	}

	singleSelect(li: HTMLElement) {
		// eslint-disable-next-line @typescript-eslint/ban-ts-comment
		// @ts-ignore: Object is possibly 'null'.
		let selected = this.listElement.querySelectorAll('.selected');
		for(let elem of selected) {
			elem.classList.remove('selected');
		}
		li.classList.add('selected');

		// eslint-disable-next-line @typescript-eslint/ban-ts-comment
		// @ts-ignore: Object is possibly 'null'.
		const page = Number(li.textContent.split(' ')[li.textContent.split(' ').length - 1])
		this.curPage = page
	}
}
</script>

<style scoped>
#ruleViolation {
	float: right;
	width: 75%;
}

ul {
	float: left;
	width: 20%;
}

.selected {
	background: #e74c3c;
}

li {
	cursor: pointer;
}
</style>
