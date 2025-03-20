FROM node:18-alpine

# Install live-server globally
RUN npm install -g live-server

# Set the working directory
WORKDIR /app

# Copy application files
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
    echo '  res.setHeader("Access-Control-Allow-Origin", "*");' >> /app/.live-server/middleware.js && \
    echo '  next();' >> /app/.live-server/middleware.js && \
    echo '};' >> /app/.live-server/middleware.js

# Expose the server port
EXPOSE 8080

# Run the server - NOTE: Fixed path issues
CMD ["live-server", "--cors", "--port=8080", "--host=0.0.0.0", "--no-browser", "--middleware=/app/.live-server/middleware.js"]