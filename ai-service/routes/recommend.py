from flask import Blueprint, request, jsonify
from datetime import datetime
import os
from groq import Groq
from dotenv import load_dotenv
import json

load_dotenv()

recommend_bp = Blueprint('recommend', __name__)
client = Groq(api_key=os.getenv("GROQ_API_KEY"))

@recommend_bp.route('/recommend', methods=['POST'])
def recommend():
    data = request.get_json()

    # Validate input
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

    prompt = f"""You are a professional risk management consultant.

Given the following risk, provide exactly 3 actionable recommendations.

Risk Title: {risk_title}
Risk Category: {risk_category}
Likelihood: {likelihood}
Impact: {impact}

Return a JSON array with exactly 3 recommendations, each with these fields:
{{
    "recommendations": [
        {{
            "action_type": "Preventive | Detective | Corrective",
            "description": "Clear actionable recommendation",
            "priority": "High | Medium | Low"
        }}
    ],
    "generated_at": "timestamp"
}}

Rules:
- Always return exactly 3 recommendations
- Each must be specific and actionable
- Always return valid JSON only
- Never include extra text outside the JSON"""

    try:
        response = client.chat.completions.create(
            model="llama-3.3-70b-versatile",
            messages=[{"role": "user", "content": prompt}],
            temperature=0.3,
            max_tokens=800
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