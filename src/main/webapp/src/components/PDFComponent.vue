
<template>
	<div>
		<pdf
			v-for="i in this.$store.getters.getNumPages"
			:key="i"
			:src="pdfURL"
			:page="i"
			@loaded="pdfView"
			style="display: inline-block; width: 55%"
		></pdf>
	</div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';

import pdf from 'vue-pdf'

@Component({
	components: {
		pdf
	},
})

export default class PDFComponent extends Vue {
	@Prop() private pdfURL!: string
	@Prop() pdfView!: () => void;
}
</script>

<style scoped>
span {
	margin-bottom: 8px;
}
</style>
