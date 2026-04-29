import bleach
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

# list of common sql injection patterns (Day 5 Fix)
SQL_PATTERNS = [
    "select *", 
    "insert into", 
    "update ", 
    "delete from", 
    "drop table", 
    "'='1", 
    "\"=\"1"
]

def sanitize_input():
    # we only care about POST requests since they carry data
    if request.method == 'POST':
        # ensure it's actually JSON data
        if not request.is_json:
            return jsonify({"error": "Bad Request", "message": "Request must be JSON format."}), 400
            
        data = request.get_json()
        if not data:
            return jsonify({"error": "Bad Request", "message": "Request body cannot be empty."}), 400
            
        # grab the user input (it might be under 'text' or 'description')
        user_text = data.get('text', '') or data.get('description', '')
        
        # 1. FIX: Check for empty input (Test 1)
        if not user_text or str(user_text).strip() == "":
            return jsonify({
                "error": "Bad Request", 
                "message": "Input text cannot be empty."
            }), 400
            
        # 2. strip html tags so they can't do XSS attacks
        clean_text = bleach.clean(str(user_text), tags=[], strip=True)
        text_lower = clean_text.lower()
        
        # 3. FIX: Check for SQL Injection (Test 2)
        for pattern in SQL_PATTERNS:
            if pattern in text_lower:
                return jsonify({
                    "error": "Security Blocked",
                    "message": "Potential SQL injection detected. Request denied."
                }), 400

        # 4. Check for prompt injection (Test 3)
        for phrase in BAD_PHRASES:
            if phrase in text_lower:
                return jsonify({
                    "error": "Security Blocked",
                    "message": "Potential prompt injection detected. Request denied."
                }), 400