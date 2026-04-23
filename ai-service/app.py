from flask import Flask, jsonify
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from middleware import sanitize_input

app = Flask(__name__)

# setting up rate limiter to stop people from spamming the api
# default limit for the whole app is 30 requests a minute
limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["30 per minute"],
    storage_uri="memory://",
)

# this runs the bouncer function to clean the input text
app.before_request(sanitize_input)

# simple route to check if input is safe
@app.route('/describe', methods=['POST'])
def describe():
    return {"message": "Success! Your input was safe."}

# specific route for reports with a stricter limit of 10 per minute
@app.route('/generate-report', methods=['POST'])
@limiter.limit("10 per minute")
def generate_report():
    return jsonify({"message": "Report generation started!"})

# returns a 429 error message with retry_after if someone hits the rate limit
@app.errorhandler(429)
def ratelimit_handler(e):
    return jsonify({
        "error": "Too Many Requests",
        "message": "Slow down! You hit the rate limit.",
        "retry_after": 60
    }), 429

if __name__ == "__main__":
    app.run(port=5000, debug=False)