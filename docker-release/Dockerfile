FROM openjdk:8-stretch

RUN export TERM=dumb ; \
  apt-get update && apt-get install -y \
    curl \
    haproxy supervisor \
  && apt-get clean && rm -rf /var/lib/apt/lists/*

COPY app/ /app

WORKDIR /app
ENTRYPOINT ["/app/bin/foilen-email-server"]
