FROM clojure:temurin-21-tools-deps-1.12.0.1530-bookworm-slim@sha256:ad82d552dc9689ca349f5895d87e4ced90beedd4b462ecfed44fd0385b413e7a AS builder
WORKDIR /app

## Cache Clojure Deps
RUN clojure -Sdeps '{:deps {dev.rpub/rpub {:mvn/version "0.2.0-SNAPSHOT"}}}' -P

## Install Babashka
RUN apt-get update && apt-get install -y curl
ADD https://raw.githubusercontent.com/babashka/babashka/master/install /app/install
RUN chmod +x install && ./install

## Cache Babashka Deps
RUN bb -Sdeps '{:deps {dev.rpub/rpub {:mvn/version "0.2.0-SNAPSHOT"}}}' prepare

FROM clojure:temurin-21-tools-deps-1.12.0.1530-bookworm-slim@sha256:ad82d552dc9689ca349f5895d87e4ced90beedd4b462ecfed44fd0385b413e7a
WORKDIR /app

COPY --from=builder /root/.m2 /root/.m2
COPY --from=builder /root/.clojure /root/.clojure
COPY --from=builder /usr/local/bin/bb /usr/local/bin/bb
COPY --from=builder /root/.deps.clj /root/.deps.clj

# Run Uberjar
ENTRYPOINT [ \
  "bb", "-Sdeps", "{:deps {dev.rpub/rpub {:mvn/version \"0.2.0-SNAPSHOT\"}}}", \
  "-m", "rpub.tasks/supervisor", \
  "--mvn/version", "0.2.0-SNAPSHOT" \
]
