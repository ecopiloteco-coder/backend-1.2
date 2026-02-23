# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./

# Use npm install with network resilience to ensure dependencies match pinned package.json
# Security: Purge any accidental Python artifacts (Trivy requirement for jaraco.context/wheel)
RUN (apk update || true) && (apk upgrade || true) && \
    npm config set fetch-retries 10 && \
    npm config set fetch-retry-mintimeout 60000 && \
    npm config set fetch-retry-maxtimeout 300000 && \
    npm install --no-audit --no-fund && \
    find /usr/lib/python* -name "jaraco.context*" -exec rm -rf {} + || true && \
    find /usr/lib/python* -name "wheel*" -exec rm -rf {} + || true && \
    find /app/node_modules -name "jaraco.context*" -exec rm -rf {} + || true && \
    find /app/node_modules -name "wheel*" -exec rm -rf {} + || true

COPY . .

# Stage 2: Runtime
FROM node:20-alpine

WORKDIR /app

# Set production environment
ENV NODE_ENV=production

# Security: Purge any accidental Python artifacts from base image (Trivy requirement)
RUN (apk update || true) && (apk upgrade || true) && \
    find /usr/lib/python* -name "jaraco.context*" -exec rm -rf {} + || true && \
    find /usr/lib/python* -name "wheel*" -exec rm -rf {} + || true && \
    rm -rf /var/cache/apk/* && \
    useradd --no-create-home --shell /bin/false appuser || \
    addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy only necessary files from builder
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/server.js ./
COPY --from=builder /app/config ./config
COPY --from=builder /app/controllers ./controllers
COPY --from=builder /app/models ./models
COPY --from=builder /app/routes ./routes
COPY --from=builder /app/services ./services

# Security: Run as non-root user
USER appuser

EXPOSE 8084

CMD ["node", "server.js"]
