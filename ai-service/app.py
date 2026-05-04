from flask import Flask, jsonify, request
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from flask_talisman import Talisman
from middleware import sanitize_input
from werkzeug.serving import WSGIRequestHandler
from functools import wraps

# --- 0. SERVER HEADER MASKING (RETAINS DAY 8 FIX) ---
class CustomRequestHandler(WSGIRequestHandler):
    server_version = "Secure-API"
    sys_version = ""

app = Flask(__name__)

# --- 1. FLASK-TALISMAN SETUP (RETAINS DAY 12 FIX) ---
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
    session_cookie_secure=False 
)

# --- 2. RATE LIMITER SETUP ---
limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["30 per minute"],
    storage_uri="memory://",
)

# --- 3. MOCK AUTHENTICATION DECORATOR (NEW FOR DAY 13) ---
def require_auth(role=None):
    def decorator(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
            auth_token = request.headers.get('Authorization')
            
            # TEST 1: 401 Verification (No token)
            if not auth_token:
                return jsonify({"error": "Unauthorized", "message": "Authentication token missing"}), 401
            
            # TEST 2: 403 Verification (Wrong role)
            # We will use 'Admin-Token-123' as our valid admin credential for testing
            if role == 'admin' and auth_token != 'Admin-Token-123':
                return jsonify({"error": "Forbidden", "message": "Admin privileges required"}), 403
                
            return f(*args, **kwargs)
        return decorated_function
    return decorator

# --- 4. INPUT SANITIZATION ---
app.before_request(sanitize_input)

# --- 5. ROUTES ---

@app.route('/', methods=['GET'])
def health_check():
    return "AI API is running and secured!", 200

@app.route('/describe', methods=['POST'])
def describe():
    # TEST 3: XSS Verification occurs here via the sanitize_input middleware
    return {"message": "Success! Your input was safe."}

@app.route('/generate-report', methods=['POST'])
@limiter.limit("5 per minute") # Lowered slightly for easier Day 13 testing
@require_auth(role='admin')   # Applied auth for 401/403 testing
def generate_report():
    return jsonify({"message": "Report generation started!"})

# --- 6. ERROR HANDLING ---

@app.errorhandler(429)
def ratelimit_handler(e):
    # TEST 4: 429 Verification
    return jsonify({
        "error": "Too Many Requests",
        "message": "Slow down!",
        "retry_after": 60
    }), 429

if __name__ == "__main__":
    app.run(port=5000, debug=False, request_handler=CustomRequestHandler)