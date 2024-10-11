FROM python:3.10-slim-buster
WORKDIR /code
COPY ./requirements.txt /code/requirements.txt
RUN pip install --no-cache-dir --upgrade -r /code/requirements.txt
COPY ./src/app /code/src/app
CMD ["uvicorn", "src.app.api:app", "--host", "0.0.0.0", "--port", "8000"]
