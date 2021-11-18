
<template>
	<form action="/api/uploadPDF"
		object="${pdf}" method="post"
		enctype="multipart/form-data"
		id="uploadFileForm"
		>
		<div>
			<label for="actual-btn">Choose File</label>
			<span id="file-chosen">No file chosen</span>
			<input type="file" name="pdf" accept="application/pdf" id="actual-btn" hidden/>
		</div>
	</form>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';

import pdf from 'vue-pdf'

@Component({
	components: {
		pdf
	},
})

export default class UploadFileComponent extends Vue {
	@Prop() setPdfName!: (name: string) => void
	@Prop() setNumPages!: (pages: number) => void

	mounted() {
		const actualBtn = document.getElementById('actual-btn') as HTMLInputElement

		const fileChosen = document.getElementById('file-chosen')

		// eslint-disable-next-line @typescript-eslint/ban-ts-comment
		// @ts-ignore: Object is possibly 'null'.
		if (actualBtn && actualBtn!.files[0]) {
			actualBtn.addEventListener('change', _ => {
				// eslint-disable-next-line @typescript-eslint/ban-ts-comment
				// @ts-ignore: Object is possibly 'null'.
				fileChosen.textContent = actualBtn!.files[0].name
				// eslint-disable-next-line @typescript-eslint/ban-ts-comment
				// @ts-ignore: Object is possibly 'null'.
				this.setPdfName(actualBtn!.files[0]!.name)
				fetch(`api/getPDFSize?pdfName=${this.$route.query.pdfName}`)
					.then(numPages => {
						this.setNumPages(Number(numPages))
					});
				})
		}

		actualBtn.onchange = function() {
		  const uploadFileButton = document.getElementById("uploadFileComponent") as HTMLFormElement
		  uploadFileButton.submit()
		};
	}
}
</script>

<style scoped>
label {
	background: #34495e;
	color: white;
	padding: 0.5rem;
	font-family: sans-serif;
	border-radius: 0.3rem;
	cursor: pointer;
	margin-top: 1rem;
}

label:hover {
	background: #e74c3c;
}

#file-chosen{
	margin-left: 0.3rem;
	font-family: sans-serif;
}
</style>
