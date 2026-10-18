# syntax=docker/dockerfile:1
FROM gradle:8.10.2-jdk21@sha256:963d59f7f22767da4efbcf46b661361b61af5fb88b0309da1071c4234c647eba AS build
WORKDIR /src
COPY settings.gradle.kts build.gradle.kts gradle.lockfile ./
COPY src ./src
RUN gradle --no-daemon installDist

FROM eclipse-temurin:21-jre@sha256:227118764cccd3c3294617061aaa93a08309c63a395eefa83e6b35032992ea99
WORKDIR /app
COPY --from=build /src/build/install/app ./
RUN useradd -u 10001 appu
USER appu
EXPOSE 8080
ENTRYPOINT ["/app/bin/app"]
