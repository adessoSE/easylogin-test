# Build frontend
FROM node:18 AS frontend-build
WORKDIR /frontend
COPY vdg-ticket-frontend/package*.json ./
RUN npm install
COPY vdg-ticket-frontend/ .
RUN npm run build

# Copy backend
FROM registry.access.redhat.com/ubi8/openjdk-21:1.20 AS backend-build
WORKDIR /backend
COPY vdg-ticket-backend/mvnw ./
COPY vdg-ticket-backend/.mvn .mvn
COPY vdg-ticket-backend/pom.xml ./
COPY vdg-ticket-backend/src ./src

# Copy frontend/dist to quarkus backend
# The Frontend is deployed via the Quarkus backend
COPY --from=frontend-build /frontend/dist/ /backend/src/main/resources/META-INF/resources/

# Build Quarkus backend
RUN ./mvnw package -DskipTests

EXPOSE 8080
USER 185

CMD ["java", "-jar", "/backend/target/quarkus-app/quarkus-run.jar"]