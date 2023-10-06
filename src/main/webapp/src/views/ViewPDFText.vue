
<template>
	<div class="about">
		<PDFTextComponent :viewModeName="`Text`"/>
	</div>
</template>

<script lang="ts">

import { Component, Prop, Vue } from 'vue-property-decorator';

import PDFTextComponent from '../components/PDFTextComponent.vue'

@Component({
	components: {
		Keypress: () => import('vue-keypress'),
		PDFTextComponent
	},
})

export default class ViewPDFText extends Vue {
	mounted() {
		if (this.$route.query.locale) {
			this.$i18n.locale = this.$route.query.locale as string;
		}
	}
}
</script>
