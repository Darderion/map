
<template>
	<div class="about">
		<div id="pdfText" v-html="this.pdfText">

		</div>
	</div>
</template>

<script lang="ts">

import { Component, Prop, Vue } from 'vue-property-decorator';

@Component
export default class ViewPDF extends Vue {
	@Prop() private pdfName!: string;

	@Prop() private pdfText!: string

	setPDFText(text: string) {
		this.pdfText = text
	}

	mounted() {
		fetch(`api/viewPDF?pdfName=${this.pdfName}`)
			.then((response) => response.text().then(this.setPDFText));
	}
}

</script>

