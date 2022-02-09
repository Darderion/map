
<template>
<div class="upload">
	<ul v-if="files.length">
		<li v-for="file in files" :key="file.id">
			<span><button class="btn btn-dark" @click="removePDF(file)">X</button></span> -
			<span>{{file.name}}</span>
			<span> - </span>
			<!--
				<span>{{file.size}}</span> -
			-->
			<span v-if="file.error">{{file.error}}</span>
			<span v-else-if="file.success && file.response.errorCode == 0">{{file.response.numberOfViolations}} errors found <a :href="`#/viewRulesViolations?pdfName=${file.response.name}`">PDF</a></span>
			<span v-else-if="file.active">active</span>
			<span v-else></span>
		</li>
	</ul>
	<ul v-else>
		<div class="p-5">
			<h4>Drop files anywhere to upload<br/>or</h4>
			<label for="file" class="btn btn-lg btn-primary">Select Files</label>
		</div>
	</ul>

	<div v-show="$refs.upload && $refs.upload.dropActive" class="drop-active">
		<h3>Drop files to upload</h3>
	</div>

	<div class="uploadSection" v-show="files.length">
		<file-upload
			class="btn btn-primary"
			post-action="api/uploadMultipleFiles"
			:multiple="true"
			:drop="true"
			:drop-directory="true"
			v-model="files"
			ref="upload">
			<i class="fa fa-plus"></i>
			Select a file
		</file-upload>
		<div>
		<button type="button" class="btn btn-success" v-if="!$refs.upload || !$refs.upload.active" @click.prevent="$refs.upload.active = true">
			<i class="fa fa-arrow-up" aria-hidden="true"></i>
			Start Upload
		</button>
		<button type="button" class="btn btn-danger"  v-else @click.prevent="$refs.upload.active = false">
			<i class="fa fa-stop" aria-hidden="true"></i>
			Stop Upload
		</button>
		</div>
	</div>
</div>
</template>

<script lang="ts">
import VueUploadComponent from 'vue-upload-component/src/FileUpload.vue'

import { Component, Vue } from 'vue-property-decorator';

@Component({
	components: {
		FileUpload: VueUploadComponent
	},
})

export default class MultipleFileUpload extends Vue {
	files: any[] = [];

	removePDF(file: any) {
		this.files = this.files.filter(it => it != file)
	}
}
</script>

<style scoped>

.upload {
	display: grid;
	grid-template-columns: 1fr 3fr 1fr;
	grid-template-rows: 200px auto 500px;
	align-items: center;
}

.upload ul {
	grid-column: 2;
	grid-row: 2;
}

.uploadSection {
	grid-column: 2;
	grid-row: 1;
}

</style>
