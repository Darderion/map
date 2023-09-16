import json
import os

from flask import Flask, request, jsonify
import transformers
import onnxruntime

app = Flask(__name__, static_url_path="")
sess_options = onnxruntime.SessionOptions()

model_path = os.path.join("ml_microservice", "models", "baseline_cpu_128.onnx")
sess_options.optimized_model_filepath = model_path
session = onnxruntime.InferenceSession(model_path, sess_options, providers=['CPUExecutionProvider'])

device = "cpu"
tokenizer_name = "cointegrated/rubert-tiny2"

tokenizer = transformers.AutoTokenizer.from_pretrained(tokenizer_name)



with open(os.path.join('ml_microservice', 'labels.json'), 'r') as f:
    labels_raw = json.loads(f.read())
    labels = {int(index): value for index, value in enumerate(labels_raw)}


def infer(text, max_length=128):
    t = tokenizer(text, padding="max_length", truncation=True,
                  max_length=max_length, return_tensors='pt')
    t = {k: v.cpu().reshape(1, 128).numpy() for k, v in t.items()}
    ort_outputs = session.run(None, t)
    label = ort_outputs[0].argmax()
    return {'label': label}


@app.route("/predict", methods=['POST'])
def predict():
    data = request.get_json(force=True)
    result = int(infer(data['data'], max_length=128)['label'])
    label = labels[result]
    return jsonify({
        "label": label
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8084)

