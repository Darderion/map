<template>
	<div class="home">
		<br>{{ $t('page.home.instruction') }}
		<br>{{ $t('page.home.instruction_chooseFile') }} <b><i>"{{ $t('button.chooseFile') }}"</i></b>
		<br>{{ $t('page.home.instruction_viewPDF') }} <b><i>"{{ $t('page.viewPDF.title') }}"</i></b>
		<br>{{ $t('page.home.instruction_rulesViolations') }} <b><i>"{{ $t('page.rulesViolations.title') }}"</i></b>
		<br>
		<br>{{ $t('page.home.rulesViolations.title') }}
		<br>
		<br><div style="width: 50%; margin: auto;"><ul>
			<li v-for="item in this.$t('page.home.rulesViolations.rules')" :key="item.toString()">{{item}}</li>
			</ul>
			</div>
	</div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
	components: {
	},
})
export default class Home extends Vue {
}
</script>

<style scoped>
div {
	font-size: 24px;
}
</style>
