# ── Stage 1: Build ──────────────────────────────────────────────────────────────
FROM python:3.11-slim AS builder

WORKDIR /app

# Upgrade system packages to patch OS-level CVEs with network resilience
RUN apt-get update -o Acquire::Retries=5 && \
    apt-get upgrade -y -o Acquire::Retries=5 --no-install-recommends && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY requirements.txt .

# 1. Upgrade critical environment tools to latest safe versions
# 2. Use a VirtualEnv for isolation to avoid base image "pollution"
# 3. Security: Aggressive surgery to remove any legacy/vendored vulnerable versions (Trivy target)
RUN python -m venv /opt/venv && \
    /opt/venv/bin/pip install --no-cache-dir --upgrade pip "setuptools>=75.8.0" "wheel>=0.46.2" "jaraco.context>=6.1.0" --retries 10 && \
    /opt/venv/bin/pip install --no-cache-dir --default-timeout=120 --retries=10 \
    -r requirements.txt && \
    find /opt/venv -type d -name "jaraco.context-5.*" -exec rm -rf {} + || true && \
    find /opt/venv -type d -name "wheel-0.45*" -exec rm -rf {} + || true && \
    find /opt/venv -path "*/setuptools/_vendor/jaraco.context*" -exec rm -rf {} + || true && \
    find /opt/venv -path "*/setuptools/_vendor/wheel*" -exec rm -rf {} + || true

# ── Stage 2: Runtime ───────────────────────────────────────────────────────────
FROM python:3.11-slim

WORKDIR /app

# Upgrade system packages and PRE-EMPTIVELY purge base image vulnerabilities
RUN apt-get update -o Acquire::Retries=5 && \
    apt-get upgrade -y -o Acquire::Retries=5 --no-install-recommends && \
    find /usr/local/lib/python* -name "wheel*" -exec rm -rf {} + || true && \
    find /usr/local/lib/python* -name "setuptools*" -exec rm -rf {} + || true && \
    find /usr/local/lib/python* -name "jaraco.context*" -exec rm -rf {} + || true && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy the VirtualEnv from builder
COPY --from=builder /opt/venv /opt/venv

# Set environment path to use venv
ENV PATH="/opt/venv/bin:$PATH"

# Copy application source
COPY . .

# Run as non-root user
RUN useradd --no-create-home --shell /bin/false appuser && \
    chown -R appuser:appuser /app
USER appuser

EXPOSE 8088

# Use JSON form for CMD
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8088"]
