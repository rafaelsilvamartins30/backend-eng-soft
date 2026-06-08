# Descarte Eletrônico - Backend

API REST desenvolvida com Spring Boot 4 e Java 21 para o gerenciamento de pontos de coleta de resíduos eletrônicos.

O projeto foca em fornecer uma plataforma para que administradores gerenciem locais de descarte e usuários possam localizar pontos próximos e reportar problemas.

## 🚀 Tecnologias

- **Java 21** & **Spring Boot 4**
- **Spring Security** com JWT (OAuth2 Resource Server)
- **Spring Data JPA** com PostgreSQL
- **Flyway** para migrações de banco de dados
- **MapStruct** para mapeamento de DTOs
- **SpringDoc OpenAPI** (Swagger) para documentação
- **Testcontainers** para testes de integração reais
- **Docker Compose** para ambiente de desenvolvimento

## 🛠️ Como Rodar

### Pré-requisitos
- Java 21+
- Docker & Docker Compose
- Maven (ou use o `./mvnw` incluso)

### Passos
1. Entre no diretório do projeto:
   ```bash
   cd Descarte-Eletronico
   ```
2. Prepare o ambiente:
   ```bash
   cp .env.example .env
   ```
3. Suba o banco de dados:
   ```bash
   docker compose up -d postgres
   ```
4. Execute os testes:
   ```bash
   ./mvnw test
   ```
5. Inicie a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```

A documentação interativa estará disponível em: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas clara:
- **Controller:** Exposição de endpoints e validação de entrada (`@Valid`).
- **Service:** Regras de negócio, transações e orquestração.
- **Repository:** Interface de comunicação com o banco de dados.
- **Mapper:** Conversão eficiente entre Entidades e DTOs usando MapStruct.

### Entidades Principais
- **Ponto de Coleta:** Locais onde o descarte pode ser realizado.
- **Tipo de Produto:** Categorias de eletrônicos aceitos (ex: Baterias, Monitores).
- **Relato de Problema:** Feedbacks ou avisos de "ponto cheio" enviados por usuários.
- **Notificação:** Alertas administrativos gerados a partir de novos relatos.
- **Usuário Admin:** Gestor único do sistema.

## 📡 Endpoints Principais

### Autenticação & Usuário
- `POST /api/v1/auth/login`: Autentica e retorna o JWT.
- `GET /api/v1/usuarios/me`: Detalhes do administrador logado.
- `PATCH /api/v1/usuarios/me`: Atualiza dados do administrador.

### Tipos de Produto
- `GET /api/v1/tipos-produto`: Lista tipos ativos (Público).
- `GET /api/v1/tipos-produto/{id}`: Busca por ID (Público).
- `POST/PUT/DELETE /api/v1/tipos-produto`: Gestão (Admin).

### Pontos de Coleta
- `GET /api/v1/pontos-coleta`: Lista pontos disponíveis (Público).
- `GET /api/v1/pontos-coleta/{id}`: Detalhes do ponto (Público).
- `POST /api/v1/pontos-coleta/{id}/relatos-problema`: Envia feedback/aviso (Público).
- `POST/PUT/DELETE /api/v1/pontos-coleta`: Gestão (Admin).

### Administrativo (Relatos e Notificações)
- `GET /api/v1/relatos-problema`: Lista relatos recebidos.
- `GET /api/v1/notificacoes`: Lista alertas do sistema.
- `PATCH /api/v1/notificacoes/{id}/visualizar`: Marca alerta como lido.

## 🧪 Testes

### Unitários
Executados para Services e Controllers, garantindo o isolamento da lógica:
```bash
./mvnw test
```

### Integração
Utilizam **Testcontainers** para subir um banco PostgreSQL real durante os testes, garantindo que as migrations e queries JPA estejam corretas. Requer o Docker rodando localmente.

## 🧹 Formatação
O projeto utiliza o **Google Java Format**. Certifique-se de formatar seu código antes de enviar contribuições.
