# Usamos la imagen oficial de OpenJDK 17
FROM eclipse-temurin:17-jdk-jammy

# Definimos el directorio de la app dentro del contenedor
WORKDIR /app

# Copiamos el pom y el código fuente
COPY pom.xml .
COPY src ./src

# Construimos el proyecto con Maven
RUN apt-get update && apt-get install -y maven \
    && mvn clean package -DskipTests \
    && apt-get remove -y maven \
    && rm -rf /var/lib/apt/lists/*

# Exponemos el puerto en el que corre Spring Boot (por defecto 8080)
EXPOSE 8080

# Comando para ejecutar la app
CMD ["java", "-jar", "target/cabapro-0.0.1-SNAPSHOT.jar"]
