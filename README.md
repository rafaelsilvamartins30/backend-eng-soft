# Descarte Eletrônico - Backend

API REST desenvolvida com Spring Boot para o gerenciamento de pontos de coleta de resíduos eletrônicos.

## 🛠️ Ferramentas Necessárias
Para rodar este projeto, você precisará das seguintes ferramentas instaladas:
- **Java 21+ (JDK)**
- **Docker & Docker Compose**
- **Maven** (opcional, pois o projeto inclui o Maven Wrapper `./mvnw`)

## 🏗️ Estrutura de Pastas
A organização do projeto segue a estrutura abaixo:
```
.
├── api-collections/      # Coleções de API (HTTP YAML) para testes e documentação
├── api-docs.json         # Especificação OpenAPI 3.1.0 completa
├── .github/              # Workflows do GitHub Actions (CI/CD)
└── Descarte-Eletronico/  # Diretório principal da aplicação Spring Boot
    ├── src/
    │   ├── main/
    │   │   ├── java/     # Código-fonte (Controllers, Services, Models, Security, etc.)
    │   │   └── resources/# Arquivos de configuração e migrações do banco (Flyway)
    │   └── test/         # Suíte de testes unitários e de integração (Testcontainers)
    ├── Dockerfile        # Configuração para containerização da aplicação
    ├── compose.yaml      # Orquestração do banco de dados PostgreSQL
    └── pom.xml           # Gerenciador de dependências e build do Maven
```

## 🚀 Comandos para Rodar o Projeto
Siga os passos abaixo para iniciar a aplicação em seu ambiente local:

1. **Entre no diretório do projeto:**
   ```bash
   cd Descarte-Eletronico
   ```

2. **Configure as variáveis de ambiente:**
   Crie um arquivo `.env` baseado no exemplo fornecido:
   ```bash
   cp .env.example .env
   ```

3. **Inicie o banco de dados:**
   Certifique-se de que o Docker está rodando e execute:
   ```bash
   docker compose up -d postgres
   ```

4. **Execute a aplicação:**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Para rodar os testes e ver a cobertura:**
   ```bash
   ./mvnw test
   ```
   Após a execução, o relatório de cobertura (JaCoCo) estará disponível em:
   `Descarte-Eletronico/target/site/jacoco/index.html`

A documentação interativa (Swagger) estará disponível em: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 📡 Endpoints Existentes

### 🔐 Autenticação & Usuário
- `POST /api/v1/auth/login`: Autentica o administrador e retorna um JWT.
- `GET /api/v1/usuarios/me`: Busca dados do administrador logado.
- `PATCH /api/v1/usuarios/me`: Atualiza dados do administrador.

### 📦 Tipos de Produto (Categorias)
- `GET /api/v1/tipos-produto`: Lista todos os tipos ativos (Público).
- `GET /api/v1/tipos-produto/{id}`: Busca um tipo por ID (Público).
- `POST /api/v1/tipos-produto`: Cria novo tipo (Admin).
- `PUT /api/v1/tipos-produto/{id}`: Atualiza um tipo (Admin).
- `DELETE /api/v1/tipos-produto/{id}`: Remove um tipo (Admin).

### 📍 Pontos de Coleta
- `GET /api/v1/pontos-coleta`: Lista pontos ativos (Público).
- `GET /api/v1/pontos-coleta/{id}`: Detalhes de um ponto (Público).
- `POST /api/v1/pontos-coleta`: Cadastra novo ponto (Admin).
- `PUT /api/v1/pontos-coleta/{id}`: Atualiza um ponto (Admin).
- `DELETE /api/v1/pontos-coleta/{id}`: Remove um ponto (Admin).
- `POST /api/v1/pontos-coleta/{id}/relatos-problema`: Envia aviso de "ponto cheio" ou feedback (Público).

### ⚠️ Relatos & Notificações (Admin)
- `GET /api/v1/relatos-problema`: Lista relatos recebidos.
- `DELETE /api/v1/relatos-problema/{id}`: Remove um relato.
- `GET /api/v1/notificacoes`: Lista alertas do sistema.
- `PATCH /api/v1/notificacoes/{id}/visualizar`: Marca alerta como lido.
- `DELETE /api/v1/notificacoes/{id}`: Remove uma notificação.

### 🧪 Exemplos (Referência)
- CRUD completo em `/api/v1/exemplos` para fins de padronização de novas funcionalidades.
