
<template>
	<div class="about">
		<h1>{{ $t('noPDFUploaded') }}</h1>
	</div>
</template>

<script lang="ts">

import { Component, Prop, Vue } from 'vue-property-decorator';

@Component({
	components: {
	},
})

export default class NoPDFComponent extends Vue {
}
</script>
