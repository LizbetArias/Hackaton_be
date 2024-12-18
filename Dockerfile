# Fase de construcción
FROM maven:3.9.0-eclipse-temurin-17 AS builder
WORKDIR /app

# Copiar el archivo pom.xml para aprovechar el caché en la descarga de dependencias
COPY pom.xml ./

# Descargar dependencias
RUN mvn dependency:go-offline

# Copiar el código fuente del proyecto
COPY src ./src

# Compilar la aplicación y generar el archivo JAR
RUN mvn clean install -DskipTests

# Imagen base para ejecutar la aplicación
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiar el archivo JAR construido en la fase anterior
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto 8080 en el contenedor
EXPOSE 8080

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

# Instrucciones para ejecutar:
# 1. Construir la imagen: docker build -t lizbet/mongodb-app .
# 2. Subir la imagen: docker push lizbet/mongodb-app
# 3. Ejecutar el contenedor: docker run -p 8080:8080 lizbet/mongodb-app
