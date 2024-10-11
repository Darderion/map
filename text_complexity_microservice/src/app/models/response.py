from typing import Optional

from pydantic import BaseModel


class MeasureResult(BaseModel):
    name: str
    value: float


class SentenceMeasuresResponse(BaseModel):
    id: int
    measures: list[MeasureResult] = []
    error: Optional[str] = None
