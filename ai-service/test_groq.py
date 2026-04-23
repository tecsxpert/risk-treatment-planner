import os
from groq import Groq
from dotenv import load_dotenv

load_dotenv()

client = Groq(api_key=os.getenv("GROQ_API_KEY"))

with open("prompts/describe_prompt.txt", "r") as f:
    prompt_template = f.read()

test_inputs = [
    {
        "risk_title": "Data Breach",
        "risk_category": "Cybersecurity",
        "likelihood": "High",
        "impact": "Critical"
    },
    {
        "risk_title": "Server Downtime",
        "risk_category": "Infrastructure",
        "likelihood": "Medium",
        "impact": "High"
    },
    {
        "risk_title": "Employee Data Theft",
        "risk_category": "Internal Security",
        "likelihood": "Low",
        "impact": "High"
    },
    {
        "risk_title": "Third Party Vendor Failure",
        "risk_category": "Operational",
        "likelihood": "Medium",
        "impact": "Medium"
    },
    {
        "risk_title": "Regulatory Non-Compliance",
        "risk_category": "Legal",
        "likelihood": "Low",
        "impact": "Critical"
    }
]

for i, test in enumerate(test_inputs):
    print(f"\n--- Test {i+1}: {test['risk_title']} ---")
    prompt = prompt_template.format(**test)
    try:
        response = client.chat.completions.create(
            model="llama-3.3-70b-versatile",
            messages=[{"role": "user", "content": prompt}],
            temperature=0.3,
            max_tokens=500
        )
        print(response.choices[0].message.content)
    except Exception as e:
        print(f"Error: {e}")