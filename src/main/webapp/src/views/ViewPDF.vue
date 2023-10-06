
<template>
	<div class="about">
		<b-spinner style="width: 10rem; height: 10rem;" label="" v-show="!pdfComponent"></b-spinner>
		<PDFComponent :pdfURL="`${this.$store.getters.getAPI}viewPDF.pdf?pdfName=${this.$route.query.pdfName}`" :pdfView="pdfView"></PDFComponent>
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
	pdfComponent = false

	pdfView() {
		this.pdfComponent = true
		console.log('pdfView')
	}
	
	mounted() {
		const locale = this.$route.query.locale as string
		if (locale != undefined && ['en', 'ru'].includes(locale)) {
			this.$i18n.locale = locale
		}
	}
}
</script>
