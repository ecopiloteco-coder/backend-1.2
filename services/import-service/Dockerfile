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
# 2. Install app dependencies in an isolated prefix
# 3. Security: Explicitly purge ANY legacy or vendored vulnerable versions (Trivy target)
RUN pip install --no-cache-dir --upgrade pip "setuptools>=75.8.0" "wheel>=0.46.2" "jaraco.context>=6.1.0" --retries 10 && \
    pip install --no-cache-dir --default-timeout=120 --retries=10 \
    --prefix=/install -r requirements.txt && \
    find /install -type d -name "jaraco.context-5.*" -exec rm -rf {} + && \
    find /install -type d -name "wheel-0.45*" -exec rm -rf {} + && \
    find /install -path "*/setuptools/_vendor/jaraco.context*" -exec rm -rf {} + && \
    find /install -path "*/setuptools/_vendor/wheel*" -exec rm -rf {} +

# ── Stage 2: Runtime ───────────────────────────────────────────────────────────
FROM python:3.11-slim

WORKDIR /app

# Upgrade system packages in the runtime image too (OpenSSL CVEs)
RUN apt-get update -o Acquire::Retries=5 && \
    apt-get upgrade -y -o Acquire::Retries=5 --no-install-recommends && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy installed site-packages from builder
COPY --from=builder /install /usr/local

# Copy application source (excluding files in .dockerignore)
COPY . .

# Run as non-root user for container security
RUN useradd --no-create-home --shell /bin/false appuser && \
    chown -R appuser:appuser /app
USER appuser

EXPOSE 8088

# Use JSON form for CMD (best practice)
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8088"]
