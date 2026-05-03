from flask import Flask, jsonify
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from middleware import sanitize_input
from werkzeug.serving import WSGIRequestHandler

# --- 0. SERVER HEADER MASKING (THE DAY 8 FIX) ---
# We set these as class variables to bypass the property setter error
class CustomRequestHandler(WSGIRequestHandler):
    server_version = "Secure-API"
    sys_version = ""

app = Flask(__name__)

# --- 1. RATE LIMITER SETUP ---
limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["30 per minute"],
    storage_uri="memory://",
)

# --- 2. INPUT SANITIZATION ---
app.before_request(sanitize_input)

# --- 3. SECURITY HEADERS ---
@app.after_request
def add_security_headers(response):
    # Fixes 'Missing Anti-clickjacking Header'
    response.headers['X-Frame-Options'] = 'DENY'
    
    # Fixes 'X-Content-Type-Options Header Missing'
    response.headers['X-Content-Type-Options'] = 'nosniff'
    
    # Fixes 'CSP: Failure to Define Directive with No Fallback'
    response.headers['Content-Security-Policy'] = (
        "default-src 'none'; "
        "script-src 'self'; "
        "connect-src 'self'; "
        "img-src 'self'; "
        "style-src 'self'; "
        "base-uri 'none'; "
        "form-action 'self'; "
        "frame-ancestors 'none';"
    )
    
    # Application-level Server Header
    response.headers['Server'] = 'Secure-API'
    
    return response

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
    # We pass the CustomRequestHandler here to ensure the server stays quiet
    app.run(port=5000, debug=False, request_handler=CustomRequestHandler)