import bleach
import re
from flask import request, jsonify

# List of sketchy phrases to prevent prompt injection
BAD_PHRASES = [
    "ignore previous instructions",
    "ignore all instructions",
    "forget what i told you",
    "system prompt",
    "you are now a",
    "disregard",
    "bypass"
]

# Common SQL injection patterns
SQL_PATTERNS = [
    "select *", 
    "insert into", 
    "update ", 
    "delete from", 
    "drop table", 
    "'='1", 
    "\"=\"1"
]

# Regex for detecting email addresses (PII Audit)
EMAIL_PATTERN = r'[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}'

def sanitize_input():
    # Only process POST requests as they carry the JSON payload
    if request.method == 'POST':
        # 1. Ensure the request is JSON
        if not request.is_json:
            return jsonify({"error": "Bad Request", "message": "Request must be JSON format."}), 400
            
        data = request.get_json()
        if not data:
            return jsonify({"error": "Bad Request", "message": "Request body cannot be empty."}), 400
            
        # 2. Grab the user input
        # This checks 'input', 'text', or 'description' in that order.
        user_text = data.get('input') or data.get('text') or data.get('description') or ""
        
        # 3. Check for empty input specifically for the /describe route
        if request.path == '/describe' and not str(user_text).strip():
            return jsonify({
                "error": "Bad Request", 
                "message": "Input text cannot be empty for description."
            }), 400

        # --- PII AUDIT CHECK (DAY 9) ---
        if re.search(EMAIL_PATTERN, str(user_text)):
            return jsonify({
                "error": "Privacy Blocked",
                "message": "Personally Identifiable Information (email) detected."
            }), 400
            
        # 4. XSS PROTECTION: DETECT & BLOCK (Updated for Day 20 Demo)
        # We strip all tags and compare. If the text changed, it contained malicious HTML/Scripts.
        original_text = str(user_text)
        clean_text = bleach.clean(original_text, tags=[], strip=True)
        
        if clean_text != original_text:
            return jsonify({
                "error": "Security Blocked",
                "message": "Malicious content (HTML/Script) detected in input."
            }), 400
            
        text_lower = clean_text.lower()
        
        # 5. Check for SQL Injection
        for pattern in SQL_PATTERNS:
            if pattern in text_lower:
                return jsonify({
                    "error": "Security Blocked",
                    "message": "Potential SQL injection detected."
                }), 400

        # 6. Check for Prompt Injection
        for phrase in BAD_PHRASES:
            if phrase in text_lower:
                return jsonify({
                    "error": "Security Blocked",
                    "message": "Potential prompt injection detected."
                }), 400
                
    return None