from flask import Blueprint, request, jsonify
from datetime import datetime
import os
from services.groq_client import call_groq
import json

report_bp = Blueprint('report', __name__)

@report_bp.route('/generate-report', methods=['POST'])
def generate_report():
    data = request.get_json()

    # Validate input
    if not data or 'risks' not in data or not data['risks']:
        return jsonify({
            "error": "Missing required field: risks"
        }), 400

    risks = data['risks']

    # Build prompt
    risks_text = ""
    for i, risk in enumerate(risks):
        risks_text += f"""
Risk {i+1}:
- Title: {risk.get('risk_title', 'N/A')}
- Category: {risk.get('risk_category', 'N/A')}
- Likelihood: {risk.get('likelihood', 'N/A')}
- Impact: {risk.get('impact', 'N/A')}
"""

    prompt = f"""You are a professional risk management consultant.

Generate a comprehensive risk treatment report for the following risks:

{risks_text}

Return a JSON object with exactly these fields:
{{
    "title": "Risk Treatment Report",
    "executive_summary": "2-3 sentence summary of overall risk landscape",
    "overview": "Detailed overview of all risks identified",
    "top_items": [
        {{
            "risk_title": "title",
            "severity": "Critical | High | Medium | Low",
            "key_finding": "most important finding"
        }}
    ],
    "recommendations": [
        {{
            "action": "recommendation",
            "priority": "High | Medium | Low",
            "timeline": "Immediate | Short-term | Long-term"
        }}
    ]
}}

Rules:
- Always return valid JSON only
- Be professional and specific
- Never include extra text outside the JSON"""

    try:
        result_text = call_groq(prompt, temperature=0.3, max_tokens=1500)
        result = json.loads(result_text)
        result['generated_at'] = datetime.utcnow().isoformat() + 'Z'

        return jsonify(result), 200

    except Exception as e:
        return jsonify({
            "error": "Report generation error",
            "details": str(e),
            "is_fallback": True
        }), 500