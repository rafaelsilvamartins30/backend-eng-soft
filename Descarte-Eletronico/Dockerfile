# Estágio 1: Build da aplicação usando JDK 21
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia os arquivos necessários para o Maven
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Garante formato Unix e permissão de execução (evita erro de quebra de linha se copiado do Windows)
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Baixa as dependências offline para aproveitar o cache do Docker
RUN ./mvnw dependency:go-offline -B

# Copia o código fonte e gera o .jar
COPY src src
RUN ./mvnw clean package -DskipTests

# Estágio 2: Imagem final apenas com o JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o .jar compilado do estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta da API
EXPOSE 8080

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]