
<template>
	<div class="about">
		<div v-if="this.$store.getters.pdfName">
			<PDFTextComponent :viewModeName="`Sections`"/>
		</div>
		<div v-else>
			<NoPDFComponent/>
		</div>
	</div>
</template>

<script lang="ts">

import { Component, Prop, Vue } from 'vue-property-decorator';

import PDFTextComponent from '../components/PDFTextComponent.vue'
import NoPDFComponent from '../components/NoPDFComponent.vue'

@Component({
	components: {
		Keypress: () => import('vue-keypress'),
		PDFTextComponent,
		NoPDFComponent
	},
})

export default class ViewPDFText extends Vue {
}
</script>
