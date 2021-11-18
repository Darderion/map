
<template>
	<div class="about">
		<Keypress key-event="keyup" :key-code="69" @success="switchToRulesView" />
		<Keypress key-event="keyup" :key-code="83" @success="switchToAreasView" />
		<!--
		<div id="ruleViolationsDiv">
			<a v-for="ruleViolation in this.pdfRuleViolations" :href="`#pdfLine${ruleViolation.lines[0].line}-${ruleViolation.lines[0].page}`" :key="ruleViolation.toString() + (new Date()).getTime()">{{ruleViolation.message}} > Line {{ruleViolation.lines[0].line}}, page {{ruleViolation.lines[0].page}}<br></a>
		</div>
		-->
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
import Section from '../classes/Section'
import { ViewMode } from '../classes/ViewMode'

@Component({
	components: {
		Keypress: () => import('vue-keypress')
	},
})

export default class ViewPDF extends Vue {
	@Prop() private pdfName!: string;
	@Prop() private viewMode!: ViewMode;
	@Prop() private pdfText!: string
	@Prop() private pdfImages!: string[]
	@Prop() private pdfLines!: PDFText[]
	@Prop() private pdfRuleViolations!: RuleViolation[]
	@Prop() private pdfSections: Section[] = []

	private receivedLines = false
	private receivedRuleViolations = false

	updatePDFView() {
		if (this.receivedLines && this.receivedRuleViolations && !!document.getElementById(`pdfLine${this.pdfLines[this.pdfLines.length - 1].line}-${this.pdfLines[this.pdfLines.length - 1].page}`)) {
			this.pdfLines.forEach(it => {
				if (!document.getElementById(`pdfLine${it.line}-${it.page}`)) {
					console.log(it.line + " " + it.page)
				}
				document.getElementById(`pdfLine${it.line}-${it.page}`)!.style.backgroundColor = 'white'
				document.getElementById(`pdfLine${it.line}-${it.page}`)!.style.color = 'black'
			})
			if (this.viewMode == ViewMode.Rules) {
				console.log(this.pdfRuleViolations)
				this.pdfRuleViolations.forEach(it => {
					it.lines.forEach(it => {
						document.getElementById(`pdfLine${it.line}-${it.page}`)!.style.backgroundColor = 'red'
					})
				})
			}
			if (this.viewMode == ViewMode.Areas) {
				console.log(this.pdfLines)
				this.pdfLines.forEach(it => {
					document.getElementById(`pdfLine${it.line}-${it.page}`)!.style.color = 'white'
					document.getElementById(`pdfLine${it.line}-${it.page}`)!.style.backgroundColor =
						(it.area == 'BIBLIOGRAPHY') ? '#522' :
						(it.area == 'TABLE_OF_CONTENT') ? '#225' :
						(it.area == 'SECTION') ? '#252' :
						// (this.pdfSections.filter(section => section.titleIndex == it.documentIndex).length == 1 ? '#252' : `#${(this.pdfSections.filter(section => section.sectionIndex <= it.documentIndex).length * 2) % 10}00`) :
						(it.area == 'FOOTNOTE') ? '#822' :
						(it.area == 'PAGE_INDEX') ? '#555' :
						(it.area == 'TITLE_PAGE') ? '#255' : 'White'
				})
			}
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

	switchToRulesView() {
		this.viewMode = ViewMode.Rules
		this.updatePDFView()
	}

	switchToAreasView() {
		this.viewMode = ViewMode.Areas
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

		fetch(`api/viewPDFSections?pdfName=${this.pdfName}`)
			.then((response) => response.text().then(text => {
				this.pdfSections = JSON.parse(text)
			}));
	}
}

</script>

