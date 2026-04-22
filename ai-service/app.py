from flask import Flask
from middleware import sanitize_input

app = Flask(__name__)

# This runs the middleware before every single request
app.before_request(sanitize_input)

# Just a dummy route to test it
@app.route('/describe', methods=['POST'])
def describe():
    return {"message": "Success! Your input was safe."}

if __name__ == "__main__":
    app.run(port=5000, debug=False)