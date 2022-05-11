FROM --platform=amd64 gradle:jdk17 AS builder

WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . /home/gradle/src

RUN gradle server:build --no-daemon

FROM gcr.io/distroless/java17-debian11:nonroot

COPY --from=builder /home/gradle/src/server/build/libs/chess-server-all.jar chess-server.jar

USER nonroot
ENTRYPOINT ["java", "-jar", "chess-server.jar"]