from flask import Flask, jsonify
import pandas as pd
from sklearn.linear_model import LinearRegression

app = Flask(__name__)

def train_models():
    df = pd.read_csv("data.csv")

    df['order_date'] = pd.to_datetime(df['order_date'])
    df['day_number'] = (df['order_date'] - df['order_date'].min()).dt.days

    models = {}

    for product_id in df['product_id'].unique():
        product_data = df[df['product_id'] == product_id]

        X = product_data[['day_number']]
        y = product_data['total_quantity']

        model = LinearRegression()
        model.fit(X, y)

        models[product_id] = model

    return models, df

@app.route("/predict", methods=["GET"])
def predict():
    models, df = train_models()

    tomorrow = df['day_number'].max() + 1

    predictions = {}

    for product_id, model in models.items():
        pred = model.predict(pd.DataFrame([[tomorrow]], columns=['day_number']))
        predictions[product_id] = round(float(pred[0]), 2)

    return jsonify(predictions)

if __name__ == "__main__":
    app.run(port=5000)