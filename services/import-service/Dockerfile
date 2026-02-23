# ── Stage 1: deps ──────────────────────────────────────────────────────────────
FROM python:3.11-slim AS builder

WORKDIR /app

# Upgrade system packages to patch OS-level CVEs (openssl: CVE-2025-15467, etc.)
# then install Python dependencies in an isolated prefix for a clean final image
RUN apt-get update && \
    apt-get upgrade -y --no-install-recommends && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

COPY requirements.txt .

RUN pip install --no-cache-dir --upgrade pip wheel==0.46.3 setuptools==82.0.0 && \
    pip install --no-cache-dir --default-timeout=120 --retries=5 \
    --prefix=/install -r requirements.txt

# ── Stage 2: runtime ───────────────────────────────────────────────────────────
FROM python:3.11-slim

WORKDIR /app

# Upgrade system packages in the runtime image too (OpenSSL CVEs)
RUN apt-get update && \
    apt-get upgrade -y --no-install-recommends && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy installed site-packages from builder
COPY --from=builder /install /usr/local

# Copy application source
COPY . .

# Run as non-root user for container security
RUN useradd --no-create-home --shell /bin/false appuser
USER appuser

EXPOSE 8088
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8088"]
