from flask import Blueprint, request, jsonify
from datetime import datetime
import os
from groq import Groq
from dotenv import load_dotenv
import json

load_dotenv()

describe_bp = Blueprint('describe', __name__)
client = Groq(api_key=os.getenv("GROQ_API_KEY"))

def load_prompt():
    with open("prompts/describe_prompt.txt", "r") as f:
        return f.read()

@describe_bp.route('/describe', methods=['POST'])
def describe():
    data = request.get_json()

    required_fields = ['risk_title', 'risk_category', 'likelihood', 'impact']
    for field in required_fields:
        if not data or field not in data or not data[field]:
            return jsonify({
                "error": f"Missing required field: {field}"
            }), 400

    risk_title = str(data['risk_title'])[:200]
    risk_category = str(data['risk_category'])[:100]
    likelihood = str(data['likelihood'])[:50]
    impact = str(data['impact'])[:50]

    try:
        prompt_template = load_prompt()
        prompt = prompt_template.format(
            risk_title=risk_title,
            risk_category=risk_category,
            likelihood=likelihood,
            impact=impact
        )

        response = client.chat.completions.create(
            model="llama-3.3-70b-versatile",
            messages=[{"role": "user", "content": prompt}],
            temperature=0.3,
            max_tokens=500
        )

        result = json.loads(response.choices[0].message.content)
        result['generated_at'] = datetime.utcnow().isoformat() + 'Z'

        return jsonify(result), 200

    except Exception as e:
        return jsonify({
            "error": "AI service error",
            "details": str(e),
            "is_fallback": True
        }), 500