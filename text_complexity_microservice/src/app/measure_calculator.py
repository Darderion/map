import itertools
import math

from textcomplexity.utils import conllu
from textcomplexity.utils.text import Text

from src.app.measure import MeasureType, Measure


def calculate_for_sentence(conll_sentence: str, measure: Measure, punct_tags: list[str] = None) -> float:
    if not punct_tags:
        punct_tags = ["PUNCT"]

    conll_sentence = conll_sentence.split("\n")
    sentences, graphs = zip(*conllu.read_conllu_sentences(conll_sentence, ignore_case=True))
    if len(sentences) > 1:
        raise ValueError()
    tokens = list(itertools.chain.from_iterable(sentences))
    graph = graphs[0]

    try:
        match measure.type:
            case MeasureType.text:
                tokens = [t for t in tokens if t.pos not in punct_tags]
                text = Text.from_tokens(tokens)
                return measure.func(text)
            case MeasureType.sentence:
                return measure.func(tokens)
            case MeasureType.punctuation:
                return measure.func(tokens, punct_tags)
            case MeasureType.graph:
                return measure.func(graph)
            case _:
                raise TypeError()
    except (ZeroDivisionError, ValueError):
        return math.nan
