FROM node:18-alpine

RUN npm install -g live-server

WORKDIR /app

COPY ./wasmJsDist ./wasmJsDist

EXPOSE 8080

CMD ["live-server", "--cors", "--mount=/:./wasmJsDist"]