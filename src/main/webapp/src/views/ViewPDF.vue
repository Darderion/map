
<template>
	<div class="about">
		<PDFComponent :pdfURL="`api/viewPDF.pdf?pdfName=${this.$store.getters.getPdfName}`"></PDFComponent>
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
	mounted() {
		const locale = this.$route.query.locale as string
		if (locale != undefined && ['en', 'ru'].includes(locale)) {
			this.$i18n.locale = locale
		}
	}
}
</script>
