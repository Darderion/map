from fastapi import FastAPI, Query
from fastapi.responses import ORJSONResponse

from src.app import measure_calculator
from src.app.conll_converter import ConllConverter
from src.app.measure import SentenceMeasures
from src.app.models.request import SentenceMeasuresRequest, MeasureName
from src.app.models.response import SentenceMeasuresResponse, MeasureResult

app = FastAPI(title="text-complexity-service", default_response_class=ORJSONResponse)

conll_converters = {}


@app.on_event("startup")
async def startup_event():
    for lang, conv in ConllConverter.all_language_converters().items():
        conll_converters[lang] = conv


@app.get("/", include_in_schema=False)
async def openapi():
    return app.openapi()


@app.post("/sentence_measures", response_model=list[SentenceMeasuresResponse])
async def sentence_measures(body: list[SentenceMeasuresRequest], measure: set[MeasureName] = Query(default=None)):
    response = []
    if not measure:
        measure = [m for m in MeasureName]
    measure = [SentenceMeasures[m] for m in measure]

    for item in body:
        if not item.sentence.strip():
            response.append(SentenceMeasuresResponse(id=item.id, error="Empty sentence"))
            continue

        conll_sentences = conll_converters[item.language].text2conll_str(item.sentence).split("\n\n")
        if len(conll_sentences) != 1:
            response.append(SentenceMeasuresResponse(id=item.id, error="Required one sentence"))
            continue

        conll_sentence = conll_sentences[0]
        calculated_measures = [(m.name, measure_calculator.calculate_for_sentence(conll_sentence, m))
                               for m in measure]
        calculated_measures = [MeasureResult(name=res[0], value=res[1]) for res in calculated_measures]
        response.append(SentenceMeasuresResponse(id=item.id, measures=calculated_measures))
    return response
