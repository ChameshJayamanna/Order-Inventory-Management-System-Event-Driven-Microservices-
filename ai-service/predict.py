import pandas as pd
from sklearn.linear_model import LinearRegression

# Load data
df = pd.read_csv("data.csv")

# Convert date to numeric (important)
df['order_date'] = pd.to_datetime(df['order_date'])
df['day_number'] = (df['order_date'] - df['order_date'].min()).dt.days

# Train model per product
models = {}

for product_id in df['product_id'].unique():
    product_data = df[df['product_id'] == product_id]

    X = product_data[['day_number']]
    y = product_data['total_quantity']

    model = LinearRegression()
    model.fit(X, y)

    models[product_id] = model

# Predict tomorrow
tomorrow = df['day_number'].max() + 1

print("Predictions:")
for product_id, model in models.items():
    prediction = model.predict(pd.DataFrame([[tomorrow]], columns=['day_number']))
    print(f"{product_id}: {prediction[0]:.2f}")