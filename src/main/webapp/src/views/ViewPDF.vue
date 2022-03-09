
<template>
	<div class="about">
		<div v-if="this.$store.getters.pdfName">
			<PDFComponent :pdfURL="`api/viewPDF.pdf?pdfName=${this.$store.getters.getPdfName}`"></PDFComponent>
		</div>
		<div v-else>
			<NoPDFComponent/>
		</div>
	</div>
</template>

<script lang="ts">

import { Component, Prop, Vue } from 'vue-property-decorator';

import PDFComponent from '../components/PDFComponent.vue'
import NoPDFComponent from '../components/NoPDFComponent.vue'

@Component({
	components: {
		Keypress: () => import('vue-keypress'),
		PDFComponent,
		NoPDFComponent
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
