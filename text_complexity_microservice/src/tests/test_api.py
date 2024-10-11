from fastapi.testclient import TestClient
from fastapi import status

from src.app.api import app
from src.app.measure import SentenceMeasures
from src.app.models.request import SentenceMeasuresRequest, MeasureName

url_measures = "/sentence_measures"

test_sentence = "Обработка текстов на естественном языке — общее направление искусственного интеллекта " \
                "и математической лингвистики."


def test_root_get():
    with TestClient(app) as client:
        response = client.get("/")
        assert response.status_code == status.HTTP_200_OK


def test_one_sentence_measures_post():
    with TestClient(app) as client:
        s_id = 0
        body = [SentenceMeasuresRequest(id=s_id, sentence=test_sentence, language="ru").dict()]
        response = client.post(url_measures, json=body)

        assert response.status_code == status.HTTP_200_OK

        json = response.json()
        assert isinstance(json, list) and len(json) == 1

        first_obj = json[0]
        assert first_obj["id"] == s_id
        assert first_obj["error"] is None

        measures = first_obj["measures"]
        assert isinstance(measures, list) and len(measures) == len(SentenceMeasures)

        actual_names = [m["name"] for m in measures]
        expected_names = [m.name for m in SentenceMeasures]
        assert all([a == b for a, b in zip(sorted(actual_names), sorted(expected_names))])


def test_request_specific_measures():
    with TestClient(app) as client:
        body = [SentenceMeasuresRequest(id=0, sentence=test_sentence, language="ru").dict()]
        measures = [m.value for m in MeasureName][:3]
        response = client.post(url_measures, json=body, params={"measure": measures})

        measures_response = response.json()[0]["measures"]
        assert isinstance(measures_response, list) and len(measures_response) == len(measures)

        actual_names = [m["name"] for m in measures_response]
        assert all([a == b for a, b in zip(sorted(actual_names), sorted(measures))])


def test_many_sentences_measures_post():
    with TestClient(app) as client:
        number = 100
        body = [SentenceMeasuresRequest(id=s_id, sentence=test_sentence, language="ru").dict() for s_id in range(number)]
        response = client.post(url_measures, json=body)

        assert response.status_code == status.HTTP_200_OK

        json = response.json()
        assert isinstance(json, list) and len(json) == number
        for item in json:
            assert item["error"] is None


def test_empty_sentence():
    with TestClient(app) as client:
        empty_sentences = ["", " "]
        body = [SentenceMeasuresRequest(id=i, sentence=s, language="ru").dict() for i, s in enumerate(empty_sentences)]
        response = client.post(url_measures, json=body)

        assert response.status_code == status.HTTP_200_OK
        for item in response.json():
            assert item["error"] is not None


def test_more_one_sentence():
    with TestClient(app) as client:
        two_sentences = " ".join([test_sentence, test_sentence])
        body = [SentenceMeasuresRequest(id=0, sentence=two_sentences, language="ru").dict()]

        response = client.post(url_measures, json=body)

        assert response.status_code == status.HTTP_200_OK
        assert response.json()[0]["error"] is not None
