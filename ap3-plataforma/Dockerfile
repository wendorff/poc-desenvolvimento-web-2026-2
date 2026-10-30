FROM eclipse-temurin:25-jdk AS build
WORKDIR /build
# Wrapper, plugins e dependências do Gradle numa camada própria, refeita só quando o build muda
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN ./gradlew --no-daemon downloadDependencies
# Node e cache do npm numa camada própria, refeita só quando as dependências do frontend mudam
COPY package.json package-lock.json .npmrc ./
RUN ./gradlew --no-daemon npmInstall
COPY vite.config.js .
COPY src ./src
RUN ./gradlew --no-daemon bootJar

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /build/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
