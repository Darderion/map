
<template>
	<div class="about">
		{{ $t('page.viewText.text')}}
		<div v-for="text in this.pdfLines" :key="text.toString() + (new Date()).getTime()" :id="`pdfLine${text.index}-${text.page}`" v-html="text.content"></div>
		{{ $t('page.viewText.images')}}
		<img v-for="pdfImage in this.pdfImages" :key="pdfImage"
		:src="'data:image/png;base64, '+pdfImage"/>
	</div>
</template>

<script lang="ts">

import { Component, Prop, Vue } from 'vue-property-decorator';

import PDFLine from '../classes/PDFLine'
import RuleViolation from '../classes/RuleViolation'
import Section from '../classes/Section'
import { ViewMode } from '../classes/ViewMode'

@Component({
	components: {
	},
})

export default class PDFTextComponent extends Vue {
	@Prop() private pdfName!: string;
	@Prop() private viewModeName!: string;
	@Prop() private pdfText!: string
	@Prop() private pdfImages!: string[]
	@Prop() private pdfLines!: PDFLine[]
	@Prop() private pdfRuleViolations!: RuleViolation[]
	@Prop() private pdfSections: Section[] = []

	private receivedLines = false
	private receivedRuleViolations = false

	viewMode = (this.viewModeName == 'Text') ? ViewMode.Text : (this.viewModeName == 'Errors') ? ViewMode.Rules : ViewMode.Areas

	updatePDFView() {
		if (this.receivedLines &&
			this.receivedRuleViolations &&
			!!document.getElementById(`pdfLine${this.pdfLines[this.pdfLines.length - 1].index}-${this.pdfLines[this.pdfLines.length - 1].page}`)) {
			this.pdfLines.forEach(it => {
				if (!document.getElementById(`pdfLine${it.index}-${it.page}`)) {
				}
				document.getElementById(`pdfLine${it.index}-${it.page}`)!.style.backgroundColor = '#abc'
				document.getElementById(`pdfLine${it.index}-${it.page}`)!.style.color = 'black'
			})
			if (this.viewMode == ViewMode.Rules) {
				this.pdfRuleViolations.forEach(it => {
					it.lines.forEach(it => {
						document.getElementById(`pdfLine${it.index}-${it.page}`)!.style.backgroundColor = 'red'
					})
				})
			}
			if (this.viewMode == ViewMode.Areas) {
				this.pdfLines.forEach(it => {
					document.getElementById(`pdfLine${it.index}-${it.page}`)!.style.color = '#abc'
					document.getElementById(`pdfLine${it.index}-${it.page}`)!.style.backgroundColor =
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

	mounted() {
		/*
		fetch(`api/viewPDF?pdfName=${this.pdfName}`)
			.then((response) => response.text().then(this.setPDFText));
			*/

		fetch(`api/viewPDFLines?pdfName=${this.$store.getters.getPdfName}`)
			.then((response) => response.text().then(this.setPDFLines));

		fetch(`api/viewRuleViolations?pdfName=${this.$store.getters.getPdfName}`)
			.then((response) => response.text().then(this.setPDFRuleViolations));
			
		fetch(`api/viewPDFImages?pdfName=${this.$store.getters.getPdfName}`)
			.then((response) => response.text().then(this.setPDFImages));

		fetch(`api/viewPDFSections?pdfName=${this.$store.getters.getPdfName}`)
			.then((response) => response.text().then(text => {
				this.pdfSections = JSON.parse(text)
			}));
	}
}
</script>
