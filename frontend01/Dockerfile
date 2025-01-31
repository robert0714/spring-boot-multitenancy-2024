# Define Alpine and NGINX versions to use.
ARG ALPINE_VERSION=3.20.1
ARG NGINX_VERSION=1.25.5
ARG NODE_VERSION=21.7.3-alpine3.19

# Prepare an Alpine-based image with OpenSSL.
FROM alpine:${ALPINE_VERSION} AS alpine
ARG DOMAIN_NAME=localhost
ARG DAYS_VALID=3650

RUN apk add --no-cache openssl
RUN echo "Creating self-signed certificate valid for ${DAYS_VALID} days for domain ${DOMAIN_NAME}" && \
    openssl \
    req -x509 \
    -nodes \
    -subj "/CN=${DOMAIN_NAME}" \
    -addext "subjectAltName=DNS:${DOMAIN_NAME}" \
    -days ${DAYS_VALID} \
    -newkey rsa:2048 -keyout /tmp/self-signed.key \
    -out /tmp/self-signed.crt

FROM node:${NODE_VERSION}  AS build-stage

WORKDIR /app
ADD . ./

RUN npm install -g pnpm && \
    pnpm install && \
    pnpm container

FROM nginx:${NGINX_VERSION} AS production-stage 

COPY --from=alpine /tmp/self-signed.key /etc/ssl/private
COPY --from=alpine /tmp/self-signed.crt /etc/ssl/certs
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY ./nginx.default /etc/nginx/nginx.conf
COPY env.sh /docker-entrypoint.d/env.sh
RUN chmod +x /docker-entrypoint.d/env.sh
# RUN chown -R nginx /etc/nginx  && \
#     chown -R nginx /usr/share/ && \
#     chown -R nginx /var/cache/nginx && \
#     chown -R nginx /var/run && \
#     chown -R nginx /var/log/nginx  && \
#     chown -R nginx /etc/ssl && \
#     chmod -R 755 /etc/ssl    
# USER nginx:nginx 
# ENTRYPOINT ["/entrypoint.sh"]
CMD ["nginx","-g","daemon off;"]
