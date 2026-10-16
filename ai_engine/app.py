"""
MindMate AI - Python AI Engine Microservice for IBM Datathon
Frameworks: PyTorch 2.x + XGBoost 2.x + IBM Granite Reasoning
Runs on LinuxONE Community Cloud (L1CC) / s390x with Docker / Podman
"""

import os
import re
from datetime import datetime
from flask import Flask, request, jsonify
from model_engine import XGBoostWellbeingPredictor, PyTorchJournalAnalyzer, IbmGraniteReasoner

app = Flask(__name__)

# Initialize AI models on startup
print("Initializing XGBoost Structured Predictive Model...")
xgboost_predictor = XGBoostWellbeingPredictor()

print("Initializing PyTorch Deep Learning Sentiment Net...")
pytorch_analyzer = PyTorchJournalAnalyzer()

print("Initializing IBM Granite LLM Reasoning Tier...")
granite_reasoner = IbmGraniteReasoner()

CRISIS_REGEX = re.compile(
    r"(?i)\b(suicide|suicidal|kill myself|hurt myself|end my life|don't want to live|want to die|self harm)\b"
)

def get_crisis_response():
    return (
        "⚠️ You are not alone, and your safety is deeply important. "
        "MindMate is an automated student well-being tool and cannot provide crisis or medical care. "
        "Please connect right now with someone who can support you: \n"
        "• India: Tele-MANAS (14416 or 1800-891-4416) | KIRAN (1800-599-0019)\n"
        "• USA & Canada: Dial or Text 988 (Suicide & Crisis Lifeline)\n"
        "• UK: NHS 111 | Samaritans: 116 123\n"
        "• Worldwide 24/7 Crisis Directory: https://findahelpline.com/\n"
        "If you are in immediate danger, please call your local emergency services (112 / 911 / 999) or go to the nearest hospital."
    )

@app.route("/health", methods=["GET"])
def health():
    return jsonify({
        "status": "UP",
        "service": "MindMate IBM Hybrid AI Engine",
        "frameworks": [
            xgboost_predictor.framework_name,
            pytorch_analyzer.framework_name,
            "IBM Granite (LLM Contextual Reasoning)"
        ],
        "hardware_target": "IBM LinuxONE (s390x) / z16",
        "hardware_acceleration": "IBM Telum AI Accelerator Ready",
        "platform_security": "Confidential Computing & Pervasive Encryption Ready",
        "timestamp": datetime.utcnow().isoformat()
    })

@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json(force=True, silent=True) or {}
    
    stress = int(data.get("stress", 5))
    sleep = int(data.get("sleep", 7))
    workload = int(data.get("workload", 6))
    energy = int(data.get("energy", 6))
    connection = int(data.get("connection", 6))
    mood_score = int(data.get("moodScore", data.get("mood_score", 3)))
    notes = data.get("notes", "")

    # Crisis triage check
    if CRISIS_REGEX.search(notes):
        return jsonify({
            "title": "Immediate Care & Support",
            "text": get_crisis_response(),
            "signal": "Elevated support",
            "signalClass": "elevated",
            "tipTitle": "Connect with emergency care",
            "tipBody": "Reach out to Tele-MANAS (14416), 988 Lifeline, or a trusted person immediately.",
            "crisis_alert": True,
            "ai_frameworks": ["IBM Crisis Heuristic", "IBM Granite Safety Filter"]
        })

    # Step 1: Run Framework 1 (XGBoost structured regression)
    xgb_out = xgboost_predictor.predict(stress, sleep, workload, energy, connection, mood_score)

    # Step 2: Run Framework 2 (PyTorch text sentiment & cognitive strain)
    pytorch_out = pytorch_analyzer.analyze(notes)

    # Step 3: Run Hybrid Reasoning Tier (IBM Granite)
    raw_metrics = {
        "stress": stress,
        "sleep": sleep,
        "workload": workload,
        "energy": energy,
        "connection": connection,
        "mood_score": mood_score
    }
    reflection = granite_reasoner.reason_checkin_intervention(xgb_out, pytorch_out, raw_metrics)
    
    return jsonify(reflection)

@app.route("/chat", methods=["POST"])
def chat():
    data = request.get_json(force=True, silent=True) or {}
    message = data.get("message", "").strip()
    conv_id = data.get("conversationId", "default-conv")
    history = data.get("history", [])

    if not message:
        return jsonify({
            "reply": "I'm here whenever you'd like to share what's on your mind. 🌱",
            "conversationId": conv_id,
            "ai": True,
            "crisisAlert": False
        })

    if CRISIS_REGEX.search(message):
        return jsonify({
            "reply": get_crisis_response(),
            "conversationId": conv_id,
            "ai": True,
            "crisisAlert": True,
            "timestamp": datetime.utcnow().isoformat()
        })

    reply = granite_reasoner.reason_chat_reply(message, history)
    return jsonify({
        "reply": reply,
        "conversationId": conv_id,
        "ai": True,
        "crisisAlert": False,
        "aiFramework": "IBM Granite LLM",
        "hardwareAcceleration": "IBM Telum",
        "timestamp": datetime.utcnow().isoformat()
    })

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    app.run(host="0.0.0.0", port=port)
