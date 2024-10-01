FROM cgr.dev/chainguard/git:latest AS git-clone

WORKDIR /sqlmap

RUN git clone --depth 1 https://github.com/sqlmapproject/sqlmap.git .

FROM cgr.dev/chainguard/python:latest

COPY --from=git-clone /sqlmap .

ENTRYPOINT ["python3", "/sqlmap.py"]