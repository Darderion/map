
<template>
	<div class="about">
		<img alt="Vue logo" src="../assets/logo.png">
		<br><a href="https://github.com/darderion/map"><b>Github</b> {{ $t('page.about.github')}}</a>
		<br>
		<br>{{ $t('page.about.suggestions')}}
		<br>
		<br><b>Discord</b>: Darderion#8682
		<br>
		<br><b>Mail</b>: Eobard1SA@gmail.com
	</div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
	components: {
	},
})
export default class About extends Vue {}
</script>

<style scoped>
* {
	font-size: 24px;
}
</style>
