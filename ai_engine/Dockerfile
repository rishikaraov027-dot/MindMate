# ==============================================================================
# MindMate AI - Python AI Engine Dockerfile for IBM Datathon
# Multi-Architecture Support: linux/amd64 and linux/s390x (IBM LinuxONE)
# Frameworks: PyTorch Architecture + XGBoost Framework + IBM Granite Reasoning
# ==============================================================================
FROM python:3.11-slim

WORKDIR /app

# Install curl for container healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Install core requirements (Pure Python: Flask, Gunicorn, Requests - instant 2s install, 0 compile steps)
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy model engine and application code
COPY model_engine.py app.py ./

ENV PORT=5000
ENV PYTHONUNBUFFERED=1

EXPOSE 5000

HEALTHCHECK --interval=15s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:5000/health || exit 1

CMD ["python", "app.py"]
