
<template>
	<div class="about">
		Text:
		<div id="pdfText" v-html="this.pdfText"></div>
		Images:
		<img v-for="pdfImage in this.pdfImages" :key="pdfImage"
		:src="'data:image/png;base64, '+pdfImage"/>
	</div>
</template>

<script lang="ts">

import { Component, Prop, Vue } from 'vue-property-decorator';

@Component
export default class ViewPDF extends Vue {
	@Prop() private pdfName!: string;
	@Prop() private pdfText!: string
	@Prop() private pdfImages!: string[]

	setPDFText(text: string) {
		this.pdfText = text
	}

	setPDFImages(text: string) {
		this.pdfImages = text.slice(1, -1).split(',').map(it => it.slice(1, -1))
	}

	mounted() {
		fetch(`api/viewPDF?pdfName=${this.pdfName}`)
			.then((response) => response.text().then(this.setPDFText));
			
		fetch(`api/viewPDFImages?pdfName=${this.pdfName}`)
			.then((response) => response.text().then(this.setPDFImages));
	}
}

</script>

