import bleach
import re  # New import for PII detection
from flask import request, jsonify

# list of sketchy phrases people use to jailbreak AI
BAD_PHRASES = [
    "ignore previous instructions",
    "ignore all instructions",
    "forget what i told you",
    "system prompt",
    "you are now a",
    "disregard",
    "bypass"
]

# list of common sql injection patterns
SQL_PATTERNS = [
    "select *", 
    "insert into", 
    "update ", 
    "delete from", 
    "drop table", 
    "'='1", 
    "\"=\"1"
]

# Simple Regex for detecting email addresses (Common PII)
EMAIL_PATTERN = r'[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}'

def sanitize_input():
    # we only care about POST requests since they carry data
    if request.method == 'POST':
        # ensure it's actually JSON data
        if not request.is_json:
            return jsonify({"error": "Bad Request", "message": "Request must be JSON format."}), 400
            
        data = request.get_json()
        if not data:
            return jsonify({"error": "Bad Request", "message": "Request body cannot be empty."}), 400
            
        # grab the user input
        user_text = data.get('text', '') or data.get('description', '')
        
        # 1. Check for empty input
        if not user_text or str(user_text).strip() == "":
            return jsonify({
                "error": "Bad Request", 
                "message": "Input text cannot be empty."
            }), 400

        # --- DAY 9: PII AUDIT CHECK ---
        # If an email is detected, we block it to prevent personal data from hitting logs or AI prompts
        if re.search(EMAIL_PATTERN, str(user_text)):
            return jsonify({
                "error": "Privacy Blocked",
                "message": "Personally Identifiable Information (email) detected. Please remove personal data."
            }), 400
            
        # 2. strip html tags for XSS protection
        clean_text = bleach.clean(str(user_text), tags=[], strip=True)
        text_lower = clean_text.lower()
        
        # 3. Check for SQL Injection
        for pattern in SQL_PATTERNS:
            if pattern in text_lower:
                return jsonify({
                    "error": "Security Blocked",
                    "message": "Potential SQL injection detected. Request denied."
                }), 400

        # 4. Check for prompt injection
        for phrase in BAD_PHRASES:
            if phrase in text_lower:
                return jsonify({
                    "error": "Security Blocked",
                    "message": "Potential prompt injection detected. Request denied."
                }), 400