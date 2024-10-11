import conllu as conllu
import pytest

from src.app.conll_converter import ConllConverter


def test_supported_languages():
    ru_lang = "ru"
    assert ru_lang in ConllConverter.supported_languages()
    try:
        _ = ConllConverter(ru_lang)
    except ValueError:
        pytest.fail("Unexpected Error")

    en_lang = "en"
    assert en_lang not in ConllConverter.supported_languages()
    with pytest.raises(ValueError):
        _ = ConllConverter(en_lang)


def test_text2conll_str():
    text = "Обработка текстов на естественном языке — общее направление искусственного интеллекта " \
           "и математической лингвистики."
    converter = ConllConverter("ru")
    conll = converter.text2conll_str(text)

    sentences = conllu.parse(conll)
    assert len(sentences) == 1

    sentence = sentences[0]
    assert len(sentence) == 14

    expected_ids = list(range(1, 15))
    actual_ids = [token['id'] for token in sentence]
    assert actual_ids == expected_ids

    expected_words = text.replace(".", " .").split()
    actual_words = [token['form'] for token in sentence]
    assert actual_words == expected_words
