<template>
	<div class="home">
		<!--
		<div class="container mb-5 bg-black bg-opacity-10 topbar"></div>
		-->
		<div class="container my-auto" v-show="this.$store.getters.getFileNames.length == 0">
			<!--
		<div class="container text-center mb-5">
			<button type="button" class="btn btn-primary fs-1 px-4 py-3 shadow-lg">
			<i class="bi bi-file-earmark-arrow-up-fill me-1"></i> Upload PDF
			</button>
		</div>
		-->

		<div class="row gx-6 gy-4 justify-content-lg-center">
			<div class="col-md-auto">
			<h1 class="mb-3">{{ $t('page.home.instruction') }}</h1>
			<ol class="ps-3">
				<li class="mt-2 ps-2">{{ $t('page.home.instruction1') }}</li>
				<li class="mt-2 ps-2">{{ $t('page.home.instruction2') }}</li>
				<li class="mt-2 ps-2">{{ $t('page.home.instruction3') }}</li>
			</ol>
			</div>
			<div class="col-md-auto">
			<h1 class="mb-3">{{ $t('page.home.ruleViolations') }}</h1>
			<ul class="ps-3">
				<li class="mt-2 ps-2">
				{{ $t('page.home.ruleViolations1') }}
				</li>
				<li class="mt-2 ps-2">{{ $t('page.home.rulesViolations.rules[0]') }}</li>
				<li class="mt-2 ps-2">{{ $t('page.home.rulesViolations.rules[10]') }}</li>
				<li class="mt-2 ps-2">{{ $t('page.home.rulesViolations.rules[11]') }}</li>
				<li class="mt-2 ps-2">{{ $t('page.home.ruleViolations2') }}</li>
			</ul>
			</div>
		</div>
		</div>
		<MultipleFileUpload/>
	</div>
</template>

<script lang="ts">
import MultipleFileUpload from '../components/MultipleFileUpload.vue'

import { Component, Vue } from 'vue-property-decorator';

@Component({
	components: {
		MultipleFileUpload
	},
})
export default class Home extends Vue {
}
</script>

<style scoped>
div {
	font-size: 24px;
}
.gx-6 {
	--bs-gutter-x: 6rem;
}
li::marker {
	font-weight: bold;
	margin-right: 5rem;
}
li {
	text-align: left;
}
</style>
