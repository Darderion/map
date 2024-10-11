from spacy_conll import init_parser


class ConllConverter:
    _spacy_models = {
        "ru": "ru_core_news_sm",
    }

    def __init__(self, lang: str):
        if lang not in ConllConverter.supported_languages():
            raise ValueError("Unsupported language")

        self.nlp = init_parser(ConllConverter._spacy_models[lang],
                               "spacy",
                               conversion_maps={"deprel": {"ROOT": "root"}})

    def text2conll_str(self, text: str) -> str:
        doc = self.nlp(text)
        return doc._.conll_str

    @staticmethod
    def supported_languages() -> list[str]:
        return list(ConllConverter._spacy_models.keys())

    @staticmethod
    def all_language_converters() -> dict[str, 'ConllConverter']:
        languages = ConllConverter.supported_languages()
        return dict([(lang, ConllConverter(lang)) for lang in languages])
