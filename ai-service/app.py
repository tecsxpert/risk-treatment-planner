from flask import Flask, jsonify
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from middleware import sanitize_input

app = Flask(__name__)

# --- 1. RATE LIMITER SETUP ---
# Prevents spamming; default is 30 requests per minute
limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["30 per minute"],
    storage_uri="memory://",
)

# --- 2. INPUT SANITIZATION ---
# Runs the bouncer function to clean text before it hits any route
app.before_request(sanitize_input)

# --- 3. SECURITY HEADERS (THE DAY 7 FIX) ---
# This adds a protective shield to every single response
@app.after_request
def add_security_headers(response):
    # Fixes 'Missing Anti-clickjacking Header'
    response.headers['X-Frame-Options'] = 'DENY'
    
    # Fixes 'X-Content-Type-Options Header Missing'
    response.headers['X-Content-Type-Options'] = 'nosniff'
    
    # Fixes 'CSP: Failure to Define Directive with No Fallback'
    # By setting default-src to 'none' and then specifically allowing 'self', 
    # we provide the strict fallback ZAP wants.
    response.headers['Content-Security-Policy'] = "default-src 'none'; script-src 'self'; connect-src 'self'; img-src 'self'; style-src 'self'; frame-ancestors 'none';"
    
    # Fixes 'Server Leaks Version Information'
    # Overwrites the default Flask/Werkzeug server header
    response.headers['Server'] = 'Secure-API'
    
    return response

# --- 4. ROUTES ---

# Front door for ZAP scan to return 200 OK
@app.route('/', methods=['GET'])
def health_check():
    return "AI API is running and secured!", 200

# Simple route to check if input is safe
@app.route('/describe', methods=['POST'])
def describe():
    return {"message": "Success! Your input was safe."}

# Specific route for reports with a stricter limit of 10 per minute
@app.route('/generate-report', methods=['POST'])
@limiter.limit("10 per minute")
def generate_report():
    return jsonify({"message": "Report generation started!"})

# --- 5. ERROR HANDLING ---

# Returns a 429 error if someone hits the rate limit
@app.errorhandler(429)
def ratelimit_handler(e):
    return jsonify({
        "error": "Too Many Requests",
        "message": "Slow down! You hit the rate limit.",
        "retry_after": 60
    }), 429

if __name__ == "__main__":
    # Keeping debug off for the security scan
    app.run(port=5000, debug=False)