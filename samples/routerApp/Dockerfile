FROM node:18-alpine

# Install live-server globally
RUN npm install -g live-server http-server cors

# Install bash
RUN apk add --no-cache bash

# Set the working directory
WORKDIR /app

COPY /wasmJsDist /app

# Create nginx style cors handling script
RUN echo '#!/bin/sh' > /app/start-server.sh && \
    echo '' >> /app/start-server.sh && \
    echo '# Create advanced middleware for handling MIME types and CORS' >> /app/start-server.sh && \
    echo 'mkdir -p /app/.live-server' >> /app/start-server.sh && \
    echo '' >> /app/start-server.sh && \
    echo 'cat > /app/.live-server/middleware.js << EOL' >> /app/start-server.sh && \
    echo 'const cors = require("cors");' >> /app/start-server.sh && \
    echo 'module.exports = [' >> /app/start-server.sh && \
    echo '  // Enable CORS for all requests' >> /app/start-server.sh && \
    echo '  cors({' >> /app/start-server.sh && \
    echo '    origin: "*",' >> /app/start-server.sh && \
    echo '    methods: ["GET", "POST", "OPTIONS"],' >> /app/start-server.sh && \
    echo '    allowedHeaders: ["Content-Type", "Authorization", "X-Requested-With"],' >> /app/start-server.sh && \
    echo '    credentials: true,' >> /app/start-server.sh && \
    echo '    preflightContinue: false,' >> /app/start-server.sh && \
    echo '    optionsSuccessStatus: 204' >> /app/start-server.sh && \
    echo '  }),' >> /app/start-server.sh && \
    echo '  // Custom middleware for MIME types' >> /app/start-server.sh && \
    echo '  function(req, res, next) {' >> /app/start-server.sh && \
    echo '    // Log request for debugging' >> /app/start-server.sh && \
    echo '    console.log(`${new Date().toISOString()} - ${req.method} ${req.url}`);' >> /app/start-server.sh && \
    echo '' >> /app/start-server.sh && \
    echo '    // Always add CORS headers as backup' >> /app/start-server.sh && \
    echo '    res.setHeader("Access-Control-Allow-Origin", "*");' >> /app/start-server.sh && \
    echo '    res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, PATCH, DELETE");' >> /app/start-server.sh && \
    echo '    res.setHeader("Access-Control-Allow-Headers", "X-Requested-With,content-type,Authorization");' >> /app/start-server.sh && \
    echo '    res.setHeader("Access-Control-Allow-Credentials", "true");' >> /app/start-server.sh && \
    echo '' >> /app/start-server.sh && \
    echo '    // Handle OPTIONS requests immediately' >> /app/start-server.sh && \
    echo '    if (req.method === "OPTIONS") {' >> /app/start-server.sh && \
    echo '      res.writeHead(204);' >> /app/start-server.sh && \
    echo '      res.end();' >> /app/start-server.sh && \
    echo '      return;' >> /app/start-server.sh && \
    echo '    }' >> /app/start-server.sh && \
    echo '' >> /app/start-server.sh && \
    echo '    // Set proper MIME types' >> /app/start-server.sh && \
    echo '    if (req.url.endsWith(".js") || req.url.includes(".js")) {' >> /app/start-server.sh && \
    echo '      res.setHeader("Content-Type", "application/javascript");' >> /app/start-server.sh && \
    echo '    } else if (req.url.endsWith(".wasm") || req.url.includes(".wasm")) {' >> /app/start-server.sh && \
    echo '      res.setHeader("Content-Type", "application/wasm");' >> /app/start-server.sh && \
    echo '    } else if (req.url.endsWith(".json")) {' >> /app/start-server.sh && \
    echo '      res.setHeader("Content-Type", "application/json");' >> /app/start-server.sh && \
    echo '    } else if (req.url.endsWith(".css")) {' >> /app/start-server.sh && \
    echo '      res.setHeader("Content-Type", "text/css");' >> /app/start-server.sh && \
    echo '    } else if (req.url.endsWith(".html") || req.url === "/") {' >> /app/start-server.sh && \
    echo '      res.setHeader("Content-Type", "text/html");' >> /app/start-server.sh && \
    echo '    }' >> /app/start-server.sh && \
    echo '' >> /app/start-server.sh && \
    echo '    next();' >> /app/start-server.sh && \
    echo '  }' >> /app/start-server.sh && \
    echo '];' >> /app/start-server.sh && \
    echo 'EOL' >> /app/start-server.sh && \
    echo '' >> /app/start-server.sh && \
    echo '# Run live-server with comprehensive options' >> /app/start-server.sh && \
    echo 'exec live-server --cors --mount=/:/app --middleware=/app/.live-server/middleware.js --port=8080 --host=0.0.0.0 --no-browser' >> /app/start-server.sh && \
    echo '' >> /app/start-server.sh

# Make start script executable
RUN chmod +x /app/start-server.sh

# Install cors module for node
RUN cd /app && npm init -y && npm install cors

# Expose the default port
EXPOSE 8080

# Run enhanced server
CMD ["/app/start-server.sh"]