FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline
# Node e cache do npm numa camada própria, refeita só quando as dependências do frontend mudam
COPY package.json package-lock.json .npmrc ./
RUN mvn -B frontend:install-node-and-npm@install-node-and-npm frontend:npm@npm-ci
COPY vite.config.js .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
