FROM gcr.io/distroless/java17-debian11:nonroot

ADD server/build/libs/chess-server-all.jar chess-server.jar

EXPOSE 5566

USER nonroot
ENTRYPOINT ["java", "-jar", "chess-server.jar"]