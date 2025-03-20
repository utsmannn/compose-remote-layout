FROM node:18-alpine

# Install http-server globally (more reliable than live-server)
RUN npm install -g http-server

# Install bash for debugging
RUN apk add --no-cache bash

# Set the working directory
WORKDIR /app

# Copy application files
COPY /wasmJsDist /app

# Create a startup script with debugging
RUN echo '#!/bin/bash' > /app/start.sh && \
    echo 'echo "Container starting..."' >> /app/start.sh && \
    echo 'echo "Current directory: $(pwd)"' >> /app/start.sh && \
    echo 'echo "Directory contents:"' >> /app/start.sh && \
    echo 'ls -la /app' >> /app/start.sh && \
    echo 'echo "Starting HTTP server on port 8080..."' >> /app/start.sh && \
    echo 'exec http-server /app -p 8080 --cors -c-1 -a 0.0.0.0 -e js=application/javascript -e wasm=application/wasm' >> /app/start.sh && \
    chmod +x /app/start.sh

# Expose the server port
EXPOSE 8080

# Run the server with the startup script
CMD ["/app/start.sh"]