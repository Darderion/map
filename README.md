
<div id="top"></div>

# Mundane Assignment Police

---

Web-app that assists in processing qualification works by pointing out common mistakes in uploaded PDF files.

App is aimed at processing works of students from Mathematics and Mechanics Faculty of Saint Petersburg State University.

Supported mistakes that can be caught:

* Incorrect usage of <span style="background-color: #222; color: #55c">⠀-⠀</span>
* Incorrect usage of <span style="background-color: #222; color: #55c">⠀--⠀</span>
* Incorrect usage of <span style="background-color: #222; color: #55c">⠀---⠀</span>
* <span style="background-color: #222; color: #55c">⠀"Quotation marks"⠀</span> instead of <span style="background-color: #222; color: #55c">⠀“Quotation marks”⠀</span>
* Litlinks <span style="background-color: #222; color: #b55">⠀[?]⠀</span>
* Litlinks as <span style="background-color: #222; color: #b55">⠀[1] [2]⠀</span> instead of <span style="background-color: #222; color: #55c">⠀[1, 2]⠀</span>
* <span style="background-color: #222; color: #b55"> Text (Sentence inside brackets)⠀</span> instead of <span style="background-color: #222; color: #55c">⠀Text (sentence inside brackets)⠀</span>
* <span style="background-color: #222; color: #b55"> Text. [1]⠀</span> instead of <span style="background-color: #222; color: #55c">⠀Text [1].⠀</span>
* Lists that only contain a single item
* Indexes for sections titled *INTRODUCTION*, *CONCLUSION*, *BIBLIOGRAPHY*
* Symbols <span style="background-color: #222; color: #b55"> ":", ".", "," </span> in section's title
* <span style="background-color: #222; color: #b55">Short URL</span> instead of <span style="background-color: #222; color: #55c">full URL</span>
* Different styles of URL (https://google.com, http://google.com, www.google.com)
* Incorrect order of bibliography references
* Different versions of the same abbreviation (DfS and DFS)
* Incorrect order of sections
* "Low-quality conferences" as references (According to https://beallslist.net/)
* Spaces around brackets
* Single-digit numbers written as numbers

### Built With

* [Vue.js](https://vuejs.org/)
* [Spring](https://spring.io/)
* [PDFBox](https://pdfbox.apache.org/)

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->
## Usage

* Maven:
  * `$ bash buildscript.sh`
  * `$ cd build`
  * `$ bash server.sh`
  * `$ bash webapp.sh`

1. Upload your PDF file
2. Review mistakes

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTRIBUTING -->
## Contributing

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#top">back to top</a>)</p>
