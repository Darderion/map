<template>
	<ul class="menu cf">
		<li><router-link to="/">Home</router-link></li>
		<li><router-link :to="`/viewPDF?pdfName=${this.$store.getters.getPdfName}&numPages=${this.$store.getters.getNumPages}`">View PDF</router-link></li>
		<li>
			<router-link :to="`/viewPDFText?pdfName=${this.$store.getters.getPdfName}`">View as text</router-link>
			<ul class="submenu">
				<li><router-link :to="`/viewPDFText?pdfName=${this.$store.getters.getPdfName}`">Plain</router-link></li>
				<li><router-link :to="`/viewPDFTextSections?pdfName=${this.$store.getters.getPdfName}`" @click.native="InlineButtonClickHandler">Highlight sections</router-link></li>
				<li><router-link :to="`/viewPDFTextErrors?pdfName=${this.$store.getters.getPdfName}`">Highlight errors</router-link></li>
			</ul>
		</li>
		<li><a href="">Rules' violations</a></li>
		<li><router-link to="/about">About</router-link></li>
	</ul>
</template>


<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';

@Component({
	components: {
	},
})

export default class MenuComponent extends Vue {
}
</script>

<style scoped>
/* Clearing floats */
.cf:before,
.cf:after {
  content: " ";
  display: table;
}

.cf:after {
  clear: both;
}

.cf {
  *zoom: 1;
}

.cf * {
	z-index: 500;
}

/* Mini reset, no margins, paddings or bullets */
.menu,
.submenu {
  margin: 0;
  padding: 0;
  list-style: none;
}

/* Main level */
.menu {
  margin: 50px auto;
  width: 800px;
  /* http://www.red-team-design.com/horizontal-centering-using-css-fit-content-value */
  width: -moz-fit-content;
  width: -webkit-fit-content;
  width: fit-content;
}

.menu > li {
  background: #34495e;
  float: left;
  position: relative;
  transform: skewX(25deg);
}

.menu a {
  display: block;
  color: #fff;
  text-transform: uppercase;
  text-decoration: none;
  font-family: Arial, Helvetica;
  font-size: 14px;
}

.menu li:hover {
  background: #e74c3c;
}

.menu > li > a {
  transform: skewX(-25deg);
  padding: 1em 2em;
}

/* Dropdown */
.submenu {
  position: absolute;
  width: 200px;
  left: 50%; margin-left: -100px;
  transform: skewX(-25deg);
  transform-origin: left top;
}

.submenu li {
  background-color: #34495e;
  position: relative;
  overflow: hidden;
}

.submenu > li > a {
  padding: 1em 2em;
}

.submenu > li::after {
  content: '';
  position: absolute;
  top: -125%;
  height: 100%;
  width: 100%;
  box-shadow: 0 0 50px rgba(0, 0, 0, .9);
}

/* Odd stuff */
.submenu > li:nth-child(odd){
  transform: skewX(-25deg) translateX(0);
}

.submenu > li:nth-child(odd) > a {
  transform: skewX(25deg);
}

.submenu > li:nth-child(odd)::after {
  right: -50%;
  transform: skewX(-25deg) rotate(3deg);
}

/* Even stuff */
.submenu > li:nth-child(even){
  transform: skewX(25deg) translateX(0);
}

.submenu > li:nth-child(even) > a {
  transform: skewX(-25deg);
}

.submenu > li:nth-child(even)::after {
  left: -50%;
  transform: skewX(25deg) rotate(3deg);
}

/* Show dropdown */
.submenu,
.submenu li {
  opacity: 0;
  visibility: hidden;
}

.submenu li {
  transition: .2s ease transform;
}

.menu > li:hover .submenu,
.menu > li:hover .submenu li {
  opacity: 1;
  visibility: visible;
}

.menu > li:hover .submenu li:nth-child(even){
  transform: skewX(25deg) translateX(15px);			
}

.menu > li:hover .submenu li:nth-child(odd){
  transform: skewX(-25deg) translateX(-15px);			
}

</style>