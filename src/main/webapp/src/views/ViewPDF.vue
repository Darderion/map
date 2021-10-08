
<template>
	<div class="about">
		<a v-for="ruleViolation in this.pdfRuleViolations" :href="`#pdfLine${ruleViolation.line}-${ruleViolation.page}`" :key="ruleViolation.toString() + (new Date()).getTime()">{{ruleViolation.message}} --> Line {{ruleViolation.line}}, page {{ruleViolation.page}}<br></a>
		Text:
		<div v-for="text in this.pdfLines" :key="text.toString() + (new Date()).getTime()" :id="`pdfLine${text.line}-${text.page}`" v-html="text.content"></div>
		Images:
		<img v-for="pdfImage in this.pdfImages" :key="pdfImage"
		:src="'data:image/png;base64, '+pdfImage"/>
	</div>
</template>

<script lang="ts">

import { Component, Prop, Vue } from 'vue-property-decorator';

import PDFText from '../classes/PDFText'
import RuleViolation from '../classes/RuleViolation'

@Component
export default class ViewPDF extends Vue {
	@Prop() private pdfName!: string;
	@Prop() private pdfText!: string
	@Prop() private pdfImages!: string[]
	@Prop() private pdfLines!: PDFText[]
	@Prop() private pdfRuleViolations!: RuleViolation[]

	private receivedLines = false
	private receivedRuleViolations = false

	updatePDFView() {
		if (this.receivedLines && this.receivedRuleViolations) {
			this.pdfRuleViolations.forEach(it => {
				document.getElementById(`pdfLine${it.line}-${it.page}`)!.style.backgroundColor = 'red'
			})
		}
	}

	updated() {
		this.updatePDFView()
	}

	setPDFText(text: string) {
		this.pdfText = text
	}

	setPDFImages(text: string) {
		this.pdfImages = text.slice(1, -1).split(',').map(it => it.slice(1, -1))
	}

	setPDFLines(text: string) {
		this.pdfLines = JSON.parse(text)
		this.receivedLines = true
		this.updatePDFView()
	}

	setPDFRuleViolations(text: string) {
		this.pdfRuleViolations = JSON.parse(text)
		this.receivedRuleViolations = true
		this.updatePDFView()
	}

	mounted() {
		/*
		fetch(`api/viewPDF?pdfName=${this.pdfName}`)
			.then((response) => response.text().then(this.setPDFText));
			*/

		fetch(`api/viewPDFLines?pdfName=${this.pdfName}`)
			.then((response) => response.text().then(this.setPDFLines));

		fetch(`api/viewRuleViolations?pdfName=${this.pdfName}`)
			.then((response) => response.text().then(this.setPDFRuleViolations));
			
		fetch(`api/viewPDFImages?pdfName=${this.pdfName}`)
			.then((response) => response.text().then(this.setPDFImages));
	}
}

</script>

