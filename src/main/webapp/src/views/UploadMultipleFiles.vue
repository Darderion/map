
<template>
	<div class="multipleFileUpload">
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
export default class UploadMultipleFiles extends Vue {
}
</script>

<style scoped>
* {
	font-size: 24px;
}
</style>
