
<template>
<div class="upload">
	<div v-if="files.length" class="uploadDiv">
		<b-row v-for="file in files" :key="file.id" class="mb-1">
			<b-col v-if="file.active" md="1"><b-spinner label=""></b-spinner></b-col>
			<b-col v-else md="1"><button class="btn btn-dark" @click="removePDF(file)">X</button></b-col>
			<b-col md="4">{{file.name}}</b-col>
			<b-col md="7" v-if="file.error">{{$t('page.uploadMultipleFiles.error')}}</b-col>
			<b-col md="7" v-else-if="file.success && file.response.errorCode == 0">
				<b-col md="12" v-if="file.response.ruleViolations.filter(ruleViolation => ruleViolation.type == 'System').length > 0"> Couldn't process PDF</b-col>
				<b-row v-if="file.response.ruleViolations.filter(ruleViolation => ruleViolation.type == 'System').length == 0">
					<b-col md="5">{{file.response.ruleViolations.filter(ruleViolation => ruleViolation.type == 'Error').length}} {{ $t('page.uploadMultipleFiles.errorsFound')}}</b-col>
					<b-col md="5">{{file.response.ruleViolations.filter(ruleViolation => ruleViolation.type == 'Warning').length}} {{ $t('page.uploadMultipleFiles.warningsFound')}}</b-col>
					<b-col md="2"><a :href="`#/viewRulesViolations?pdfName=${file.response.name}&locale=${getLocale()}`">PDF</a></b-col>
				</b-row>
			</b-col>
		</b-row>
	</div>
	<div class="uploadFilesDiv" v-else>
		<div class="p-5">
			<h4>{{ $t('page.uploadMultipleFiles.uploadFiles1')}}<br/>{{ $t('page.uploadMultipleFiles.options')}}</h4>
			<label for="file" class="btn btn-lg btn-primary">{{ $t('page.uploadMultipleFiles.selectFiles')}}</label>
		</div>
	</div>

	<div v-show="$refs.upload && $refs.upload.dropActive" class="drop-active">
		<h3>{{ $t('page.uploadMultipleFiles.uploadFiles2')}}</h3>
	</div>

	<div class="uploadSection" v-show="files.length > 0">
		<file-upload
			class="btn btn-primary"
			post-action="/api/uploadPDF"
			:multiple="true"
			:drop="true"
			:drop-directory="true"
			v-model="files"
			ref="upload">
			<i class="fa fa-plus"></i>
			{{ $t('page.uploadMultipleFiles.addPDF')}}
		</file-upload>
		<div>
		<button type="button" class="btn btn-success mb1" v-if="!$refs.upload || !$refs.upload.active" @click.prevent="$refs.upload.active = true">
			<i class="fa fa-arrow-up" aria-hidden="true"></i>
			{{ $t('page.uploadMultipleFiles.startUploading')}}
		</button>
		<button type="button" class="btn btn-danger"  v-else @click.prevent="$refs.upload.active = false">
			<i class="fa fa-stop" aria-hidden="true"></i>
			{{ $t('page.uploadMultipleFiles.stopUploading')}}
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
		console.log(this.files);
		this.files = this.files.filter(it => it != file)
	}

	getLocale() {
		return this.$i18n.locale;
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

.upload .uploadDiv {
	grid-column: 2;
	grid-row: 2;
}

.uploadSection {
	grid-column: 2;
	grid-row: 1;
}

.uploadFilesDiv {
	grid-column: 2;
	grid-row: 1;
}

</style>
