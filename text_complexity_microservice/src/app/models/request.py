from enum import Enum

from pydantic import BaseModel

from src.app.conll_converter import ConllConverter
from src.app.measure import SentenceMeasures


MeasureName = Enum("Measure", dict([(m.name, m.name) for m in SentenceMeasures]), type=str)

Language = Enum("Language", dict([(lang, lang) for lang in ConllConverter.supported_languages()]), type=str)


class SentenceMeasuresRequest(BaseModel):
    id: int
    sentence: str
    language: Language
