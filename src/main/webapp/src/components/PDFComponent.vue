
<template>
	<div>
		<pdf
			v-for="i in Number(this.$route.query.numPages)"
			:key="i"
			:src="pdfURL"
			:page="i"
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
	numPages = Number(
		this.$route.query.numPages?
		this.$route.query.numPages: 0
	)
}
</script>
