import os

import pytest

from src.app import measure_calculator as mc
from src.app.measure import SentenceMeasures
from src.tests import config


def test_measure_calculator():
    conll_file = "conll_sentence.txt"
    conll_file_path = os.path.join(config.resource_folder, conll_file)

    with open(conll_file_path, encoding='utf-8') as f:
        conll_sentence = f.read()

    for measure in SentenceMeasures:
        try:
            _ = mc.calculate_for_sentence(conll_sentence, measure)
        except:
            pytest.fail("Unexpected Error")
