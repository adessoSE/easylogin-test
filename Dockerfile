# Build frontend
FROM node:20 AS frontend-build
WORKDIR /frontend
COPY vdg-ticket-frontend/package*.json ./
RUN npm ci
COPY vdg-ticket-frontend/ .
RUN npm run build

# Copy backend
FROM registry.access.redhat.com/ubi8/openjdk-21:1.20 AS backend-build

USER root
RUN microdnf install -y maven && microdnf clean all

WORKDIR /backend
COPY vdg-ticket-backend/pom.xml ./
COPY vdg-ticket-backend/src ./src

# Copy frontend/dist to quarkus backend
# The Frontend is deployed via the Quarkus backend
COPY --from=frontend-build /frontend/dist/ /backend/src/main/resources/META-INF/resources/

# Build Quarkus backend
RUN mvn package -DskipTests

FROM registry.access.redhat.com/ubi8/openjdk-21:1.20

WORKDIR /app

# Nur das gebaute JAR kopieren
COPY --from=backend-build /backend/target/quarkus-app/ /app/

EXPOSE 8080
USER 185

CMD ["java", "-jar", "/app/quarkus-run.jar"]