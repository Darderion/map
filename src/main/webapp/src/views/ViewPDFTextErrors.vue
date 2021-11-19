
<template>
	<div class="about">
		<PDFTextComponent :viewModeName="`Errors`"/>
	</div>
</template>

<script lang="ts">

import { Component, Prop, Vue } from 'vue-property-decorator';

import PDFTextComponent from '../components/PDFTextComponent.vue'

@Component({
	components: {
		PDFTextComponent
	},
})

export default class ViewPDFText extends Vue {
}
</script>
