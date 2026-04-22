import bleach
from flask import request, jsonify

# list of sketchy phrases people use to jailbreak AI
BAD_PHRASES = [
    "ignore previous instructions",
    "ignore all instructions",
    "forget what i told you",
    "system prompt",
    "you are now a",
    "disregard"
]

def sanitize_input():
    # we only care about POST requests since they carry data
    if request.method == 'POST':
        data = request.get_json()
        
        if not data:
            return
            
        # grab the user input (it might be under 'text' or 'description')
        user_text = data.get('text', '') or data.get('description', '')
        
        if not user_text:
            return
            
        # 1. strip html tags so they can't do XSS attacks
        clean_text = bleach.clean(user_text, tags=[], strip=True)
        
        # 2. check for prompt injection
        text_lower = clean_text.lower()
        for phrase in BAD_PHRASES:
            if phrase in text_lower:
                # 3. return 400 with a clear error if we catch them
                return jsonify({
                    "error": "Security Blocked",
                    "message": "Potential prompt injection detected. Request denied."
                }), 400