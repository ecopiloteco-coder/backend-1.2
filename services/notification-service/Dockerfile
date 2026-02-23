FROM node:20-alpine

WORKDIR /app

COPY package*.json ./

RUN apk update && apk upgrade && \
    npm config set fetch-retries 5 && \
    npm config set fetch-retry-mintimeout 20000 && \
    npm config set fetch-retry-maxtimeout 120000 && \
    npm install

COPY . .

EXPOSE 8084

CMD ["node", "server.js"]
