# ==============================================================================
# MindMate AI - Python AI Engine Dockerfile for IBM Datathon
# Multi-Architecture Support: linux/amd64 and linux/s390x (IBM LinuxONE)
# Frameworks: PyTorch 2.x + XGBoost 2.x + IBM Granite Reasoning
# ==============================================================================
FROM python:3.11-slim

WORKDIR /app

# Install system dependencies (including cmake and OpenMP libgomp1)
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    cmake \
    libgomp1 \
    python3-dev \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Install core requirements (Flask, Scikit-Learn, NumPy, Requests, Gunicorn)
COPY requirements.txt .
RUN pip install --no-cache-dir --upgrade pip setuptools wheel && \
    pip install --no-cache-dir -r requirements.txt

# Try installing pre-compiled binary wheels for PyTorch and XGBoost if available on this architecture.
# `--only-binary=:all:` guarantees pip NEVER tries to compile from source if no binary wheel exists!
RUN (pip install --no-cache-dir --only-binary=:all: torch --index-url https://download.pytorch.org/whl/cpu || \
     pip install --no-cache-dir --only-binary=:all: torch || true) && \
    (pip install --no-cache-dir --only-binary=:all: xgboost || true)

# Copy model engine and application code
COPY model_engine.py app.py ./

ENV PORT=5000
ENV PYTHONUNBUFFERED=1

EXPOSE 5000

HEALTHCHECK --interval=15s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:5000/health || exit 1

CMD ["python", "app.py"]
