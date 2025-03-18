FROM node:18-alpine

# Install live-server globally
RUN npm install -g live-server

# Install bash
RUN apk add --no-cache bash

# Set the working directory
WORKDIR /app

COPY /wasmJsDist /app

# Create middleware directory
RUN mkdir -p /app/.live-server

# Create middleware file for correct MIME types
RUN echo 'module.exports = function(req, res, next) {' > /app/.live-server/middleware.js && \
    echo '  if (req.url.endsWith(".js")) {' >> /app/.live-server/middleware.js && \
    echo '    res.setHeader("Content-Type", "application/javascript");' >> /app/.live-server/middleware.js && \
    echo '  } else if (req.url.endsWith(".wasm")) {' >> /app/.live-server/middleware.js && \
    echo '    res.setHeader("Content-Type", "application/wasm");' >> /app/.live-server/middleware.js && \
    echo '  }' >> /app/.live-server/middleware.js && \
    echo '  next();' >> /app/.live-server/middleware.js && \
    echo '};' >> /app/.live-server/middleware.js

# Expose the default port
EXPOSE 8080

# Run live-server directly
CMD ["live-server", "--cors", "--mount=/:/app/wasmJsDist", "--middleware=/app/.live-server/middleware.js"]