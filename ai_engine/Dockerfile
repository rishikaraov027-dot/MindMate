# ==============================================================================
# MindMate AI - Python AI Engine Dockerfile for IBM Datathon
# Multi-Architecture Support: linux/amd64 and linux/s390x (IBM LinuxONE)
# Frameworks: PyTorch 2.x + XGBoost 2.x + IBM Granite Reasoning
# ==============================================================================
FROM python:3.11-slim

WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Install Python requirements
COPY requirements.txt .
RUN pip install --no-cache-dir --upgrade pip && \
    (pip install --no-cache-dir torch --index-url https://download.pytorch.org/whl/cpu || pip install --no-cache-dir torch) && \
    pip install --no-cache-dir -r requirements.txt

# Copy model engine and application code
COPY model_engine.py app.py ./

ENV PORT=5000
ENV PYTHONUNBUFFERED=1

EXPOSE 5000

HEALTHCHECK --interval=15s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:5000/health || exit 1

CMD ["python", "app.py"]
