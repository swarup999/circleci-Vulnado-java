FROM cgr.dev/chainguard/python:latest-dev
USER root
WORKDIR /nmap
RUN apk add --no-cache git

RUN git clone --depth 1 https://github.com/nmap/nmap.git

RUN adduser -D nmapuser
USER nmapuser
