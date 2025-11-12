FROM nginx:alpine

# Copy application files
COPY /wasmJsDist /usr/share/nginx/html/

# Create a custom nginx configuration with CORS headers
RUN echo 'server {' > /etc/nginx/conf.d/default.conf && \
    echo '    listen       80;' >> /etc/nginx/conf.d/default.conf && \
    echo '    server_name  localhost;' >> /etc/nginx/conf.d/default.conf && \
    echo '' >> /etc/nginx/conf.d/default.conf && \
    echo '    # Add CORS headers for all requests' >> /etc/nginx/conf.d/default.conf && \
    echo '    add_header "Access-Control-Allow-Origin" "*" always;' >> /etc/nginx/conf.d/default.conf && \
    echo '    add_header "Access-Control-Allow-Methods" "GET, POST, OPTIONS" always;' >> /etc/nginx/conf.d/default.conf && \
    echo '    add_header "Access-Control-Allow-Headers" "DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range" always;' >> /etc/nginx/conf.d/default.conf && \
    echo '    add_header "Access-Control-Expose-Headers" "Content-Length,Content-Range" always;' >> /etc/nginx/conf.d/default.conf && \
    echo '' >> /etc/nginx/conf.d/default.conf && \
    echo '    # Handle preflight OPTIONS requests' >> /etc/nginx/conf.d/default.conf && \
    echo '    if ($request_method = "OPTIONS") {' >> /etc/nginx/conf.d/default.conf && \
    echo '        add_header "Access-Control-Allow-Origin" "*";' >> /etc/nginx/conf.d/default.conf && \
    echo '        add_header "Access-Control-Allow-Methods" "GET, POST, OPTIONS";' >> /etc/nginx/conf.d/default.conf && \
    echo '        add_header "Access-Control-Allow-Headers" "DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range";' >> /etc/nginx/conf.d/default.conf && \
    echo '        add_header "Access-Control-Max-Age" 1728000;' >> /etc/nginx/conf.d/default.conf && \
    echo '        add_header "Content-Type" "text/plain; charset=utf-8";' >> /etc/nginx/conf.d/default.conf && \
    echo '        add_header "Content-Length" 0;' >> /etc/nginx/conf.d/default.conf && \
    echo '        return 204;' >> /etc/nginx/conf.d/default.conf && \
    echo '    }' >> /etc/nginx/conf.d/default.conf && \
    echo '' >> /etc/nginx/conf.d/default.conf && \
    echo '    # Set proper MIME types' >> /etc/nginx/conf.d/default.conf && \
    echo '    location ~ \\.js$ {' >> /etc/nginx/conf.d/default.conf && \
    echo '        types { application/javascript js; }' >> /etc/nginx/conf.d/default.conf && \
    echo '        default_type application/javascript;' >> /etc/nginx/conf.d/default.conf && \
    echo '    }' >> /etc/nginx/conf.d/default.conf && \
    echo '' >> /etc/nginx/conf.d/default.conf && \
    echo '    location ~ \\.wasm$ {' >> /etc/nginx/conf.d/default.conf && \
    echo '        types { application/wasm wasm; }' >> /etc/nginx/conf.d/default.conf && \
    echo '        default_type application/wasm;' >> /etc/nginx/conf.d/default.conf && \
    echo '    }' >> /etc/nginx/conf.d/default.conf && \
    echo '' >> /etc/nginx/conf.d/default.conf && \
    echo '    location / {' >> /etc/nginx/conf.d/default.conf && \
    echo '        root   /usr/share/nginx/html;' >> /etc/nginx/conf.d/default.conf && \
    echo '        index  index.html index.htm;' >> /etc/nginx/conf.d/default.conf && \
    echo '        try_files $uri $uri/ /index.html;' >> /etc/nginx/conf.d/default.conf && \
    echo '    }' >> /etc/nginx/conf.d/default.conf && \
    echo '' >> /etc/nginx/conf.d/default.conf && \
    echo '    # Error handling' >> /etc/nginx/conf.d/default.conf && \
    echo '    error_page   500 502 503 504  /50x.html;' >> /etc/nginx/conf.d/default.conf && \
    echo '    location = /50x.html {' >> /etc/nginx/conf.d/default.conf && \
    echo '        root   /usr/share/nginx/html;' >> /etc/nginx/conf.d/default.conf && \
    echo '    }' >> /etc/nginx/conf.d/default.conf && \
    echo '}' >> /etc/nginx/conf.d/default.conf

# Add mime.types to recognize WASM files
RUN echo 'types {' > /etc/nginx/mime.types && \
    echo '    text/html                             html htm shtml;' >> /etc/nginx/mime.types && \
    echo '    text/css                              css;' >> /etc/nginx/mime.types && \
    echo '    text/xml                              xml;' >> /etc/nginx/mime.types && \
    echo '    application/javascript                js;' >> /etc/nginx/mime.types && \
    echo '    application/wasm                      wasm;' >> /etc/nginx/mime.types && \
    echo '    application/json                      json;' >> /etc/nginx/mime.types && \
    echo '    image/jpeg                            jpeg jpg;' >> /etc/nginx/mime.types && \
    echo '    image/png                             png;' >> /etc/nginx/mime.types && \
    echo '    image/svg+xml                         svg svgz;' >> /etc/nginx/mime.types && \
    echo '}' >> /etc/nginx/mime.types

# Expose the port
EXPOSE 80

# Nginx starts automatically in this image