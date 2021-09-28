<template>
	<div id="app">
		<div id="nav">
			<router-link to="/">Home</router-link> |
			<router-link to="/viewPDF">About</router-link>
		</div>
		<router-view/>

		<form action="/pdf/upload"
			object="${pdf}" method="post"
			enctype="multipart/form-data"
			>
			<div>

			<label>PDF:</label>
			<input type="file" name="pdf" accept="application/pdf" />

			</div>

			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

			<input type="submit">
		</form>
	</div>
</template>

<style lang="scss">
#app {
	font-family: Avenir, Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
	text-align: center;
	color: #2c3e50;
}

#nav {
	padding: 30px;

	a {
		font-weight: bold;
		color: #2c3e50;

		&.router-link-exact-active {
			color: #42b983;
		}
	}
}
</style>
