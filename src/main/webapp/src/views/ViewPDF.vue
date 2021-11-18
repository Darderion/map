
<template>
	<div class="about">
		<PDFComponent :pdfURL="`api/viewPDF.pdf?pdfName=${this.pdfName}`"></PDFComponent>
	</div>
</template>

<script lang="ts">

import { Component, Prop, Vue } from 'vue-property-decorator';

import PDFComponent from '../components/PDFComponent.vue'

@Component({
	components: {
		Keypress: () => import('vue-keypress'),
		PDFComponent
	},
})

export default class ViewPDF extends Vue {
	@Prop() private pdfName!: string;
}
</script>
