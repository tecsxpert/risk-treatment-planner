from flask import Flask, jsonify
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from flask_talisman import Talisman
from middleware import sanitize_input
from werkzeug.serving import WSGIRequestHandler

# --- 0. SERVER HEADER MASKING (RETAINS DAY 8 FIX) ---
class CustomRequestHandler(WSGIRequestHandler):
    server_version = "Secure-API"
    sys_version = ""

app = Flask(__name__)

# --- 1. FLASK-TALISMAN SETUP (DAY 12 UPGRADE) ---
# Replaces manual header management with a production-grade wrapper.
# We keep force_https=False for local development on http://127.0.0.1
csp = {
    'default-src': '\'none\'',
    'script-src': '\'self\'',
    'connect-src': '\'self\'',
    'img-src': '\'self\'',
    'style-src': '\'self\'',
    'base-uri': '\'none\'',
    'form-action': '\'self\'',
    'frame-ancestors': '\'none\''
}

Talisman(
    app, 
    force_https=False, 
    content_security_policy=csp,
    strict_transport_security=True,
    session_cookie_secure=False # Set to True if using HTTPS/Production
)

# --- 2. RATE LIMITER SETUP ---
limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["30 per minute"],
    storage_uri="memory://",
)

# --- 3. INPUT SANITIZATION ---
app.before_request(sanitize_input)

# --- 4. ROUTES ---

@app.route('/', methods=['GET'])
def health_check():
    return "AI API is running and secured!", 200

@app.route('/describe', methods=['POST'])
def describe():
    return {"message": "Success! Your input was safe."}

@app.route('/generate-report', methods=['POST'])
@limiter.limit("10 per minute")
def generate_report():
    return jsonify({"message": "Report generation started!"})

# --- 5. ERROR HANDLING ---

@app.errorhandler(429)
def ratelimit_handler(e):
    return jsonify({
        "error": "Too Many Requests",
        "message": "Slow down!",
        "retry_after": 60
    }), 429

if __name__ == "__main__":
    # CustomRequestHandler ensures the 'Server' header remains masked
    app.run(port=5000, debug=False, request_handler=CustomRequestHandler)