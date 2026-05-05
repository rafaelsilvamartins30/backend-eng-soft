# Descarte Eletronico

Backend Spring Boot com Java 21 para o projeto de descarte eletrônico.

O projeto usa uma base simples para criação de CRUDs, sem controllers genéricos e sem implementação automática de CRUD. A regra é manter cada entidade explícita, mas reaproveitar contratos pequenos para evitar repetição desnecessária.

## Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- Spring Security
- SpringDoc OpenAPI / Swagger
- MapStruct
- PostgreSQL
- Docker Compose
- Flyway
- Testcontainers

## Como Rodar

Entre no módulo da aplicação:

```bash
cd Descarte-Eletronico
```

Crie o arquivo `.env` a partir do exemplo:

```bash
cp .env.example .env
```

O `.env.example` já contém valores funcionais para desenvolvimento local. O `.env` real é ignorado pelo Git e pode ser ajustado por máquina.

Suba o banco:

```bash
docker compose up -d postgres
```

Rode os testes:

```bash
./mvnw test
```

Os testes de repository usam Testcontainers. Por isso, o Docker precisa estar rodando para a suíte completa passar.

Rode a aplicação:

```bash
./mvnw spring-boot:run
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

## Ambiente Local Com Docker Compose

O Compose usa as variáveis do `.env` para criar o Postgres local.

Arquivo mínimo funcional:

```env
POSTGRES_IMAGE=postgres:16-alpine
POSTGRES_CONTAINER_NAME=descarte-eletronico-postgres
POSTGRES_DB=descarte_eletronico
POSTGRES_USER=descarte
POSTGRES_PASSWORD=descarte
POSTGRES_PORT=5432

DB_HOST=localhost
DB_PORT=5432
DB_NAME=descarte_eletronico
DB_USERNAME=descarte
DB_PASSWORD=descarte
DB_URL=jdbc:postgresql://localhost:5432/descarte_eletronico
```

Comandos úteis:

```bash
docker compose up -d postgres
docker compose ps
docker compose logs -f postgres
docker compose down
```

Se mudar `POSTGRES_PORT`, ajuste também `DB_PORT` e `DB_URL`.

## Variáveis De Ambiente

O arquivo real `.env` não deve ser versionado. Use `.env.example` como referência.

O `compose.yaml` segue a Compose Specification atual. Por isso ele não usa a chave `version`, que é obsoleta no Docker Compose v2. A versão do serviço fica explícita na imagem `postgres:16-alpine`.

Principais variáveis:

- `POSTGRES_IMAGE`: imagem Docker do Postgres.
- `POSTGRES_CONTAINER_NAME`: nome do container local.
- `POSTGRES_DB`: nome do banco criado pelo container.
- `POSTGRES_USER`: usuário do banco.
- `POSTGRES_PASSWORD`: senha do banco.
- `POSTGRES_PORT`: porta exposta no host.
- `DB_HOST`: host usado pela aplicação.
- `DB_PORT`: porta usada pela aplicação.
- `DB_NAME`: banco usado pela aplicação.
- `DB_USERNAME`: usuário usado pela aplicação.
- `DB_PASSWORD`: senha usada pela aplicação.
- `DB_URL`: URL JDBC completa. Quando definida, tem prioridade sobre `DB_HOST`, `DB_PORT` e `DB_NAME`.

## Estrutura Do CRUD

Fluxo esperado:

```text
Controller -> Service -> Repository
                 |
               Mapper
```

Responsabilidades:

- Controller: expõe endpoints HTTP, usa `@Valid`, monta `ResponseEntity` e chama o service.
- Service: concentra regra de negócio, transações, soft delete, validações específicas e uso do mapper.
- Repository: acesso ao banco via Spring Data JPA.
- Mapper: conversão entre DTO e entidade usando MapStruct.
- DTOs: entrada e saída da API.
- Entity: modelo JPA, herdando campos técnicos de `BaseEntity`.

## BaseEntity

Toda entidade deve herdar de `BaseEntity`.

Campos técnicos:

- `id`: UUID.
- `version`: controle de optimistic locking.
- `createdAt`: data de criação.
- `updatedAt`: data da última atualização.
- `entityStatus`: ciclo de vida técnico.
- `deletedAt`: data do soft delete.

`entityStatus` usa o enum `EntityStatus`:

- `ACTIVE`
- `INACTIVE`
- `DELETED`

Não use `entityStatus` para estado de negócio. Estados como `PENDING`, `APPROVED`, `FINISHED` ou `CANCELED` devem ficar em enums próprios da entidade.

## Soft Delete

Remoção deve ser lógica.

No service concreto:

- `delete` deve alterar `entityStatus` para `DELETED`;
- `delete` deve preencher `deletedAt`;
- `findById` deve ignorar registros `DELETED`;
- `findAll` deve ignorar registros `DELETED`.

## BaseMapper

Cada entidade deve ter um mapper concreto estendendo `BaseMapper`.

Métodos do contrato:

- `toEntity`
- `toResponse`
- `toResponseSet`
- `updateEntityFromRequest`

No método de update, ignore campos técnicos:

- `id`
- `version`
- `createdAt`
- `updatedAt`
- `entityStatus`
- `deletedAt`

O mapper deve ficar no service. Controller não deve fazer conversão manual.

## BaseService

`BaseService` é só um contrato mínimo:

- `create`
- `findById`
- `findAll`
- `update`
- `delete`

Não existe `AbstractCrudService`. Cada service concreto implementa sua lógica explicitamente.

Listagens usam `Set` por padrão para evitar duplicidade.

## Como Criar Uma Nova Entidade

Para criar uma entidade nova, siga o padrão do exemplo.

Arquivos esperados:

- `model/<entidade>/<Entidade>.java`
- `model/<entidade>/dto/<Entidade>Request.java`
- `model/<entidade>/dto/<Entidade>Response.java`
- `repository/<Entidade>Repository.java`
- `mapper/<Entidade>Mapper.java`
- `service/<Entidade>Service.java`
- `controller/<Entidade>Controller.java`
- migration Flyway em `src/main/resources/db/migration`

Regras:

- Controller deve ser explícito por entidade.
- Não criar `BaseController`.
- Não criar CRUD genérico automático.
- Service deve implementar `BaseService`.
- Mapper deve estender `BaseMapper`.
- Repository deve expor buscas ignorando `EntityStatus.DELETED`.
- DTO de request pode ser o mesmo para create e update quando fizer sentido.
- SQL deve conter campos técnicos herdados da `BaseEntity`.
- Swagger deve documentar respostas de sucesso e erro usando `ErrorResponseDTO`.
- Testes devem cobrir caminhos positivos e negativos do service e do controller.

Checklist recomendado:

1. Criar entity, DTOs, repository, mapper, service e controller.
2. Criar migration Flyway com o próximo número, por exemplo `V2__create_produto_table.sql`.
3. No repository, criar métodos que ignorem `EntityStatus.DELETED`.
4. No service, implementar soft delete e lançar `ResourceNotFoundException` quando necessário.
5. No controller, usar `@Valid`, `ResponseEntity`, `@Operation` e `@ApiResponses`.
6. Criar testes unitários do service com `assertThat`, `assertThatThrownBy`, `verify` e `verifyNoMoreInteractions`.
7. Criar testes do controller cobrindo status `2xx`, `400`, `404` e, quando fizer sentido, `500`.
8. Criar teste de repository com Testcontainers quando houver query, migration ou comportamento JPA relevante.
9. Rodar `./mvnw test` antes de abrir PR.

## Endpoints Do Exemplo

Base path:

```text
/api/exemplos
```

Endpoints:

- `POST /api/exemplos`
- `GET /api/exemplos`
- `GET /api/exemplos/{id}`
- `PUT /api/exemplos/{id}`
- `DELETE /api/exemplos/{id}`

Exemplo de body:

```json
{
  "nome": "Coleta de notebook",
  "descricao": "Exemplo de cadastro para referência do CRUD"
}
```

## Tratamento De Erros

O projeto possui:

- `ResourceNotFoundException`
- `BusinessException`
- `GlobalExceptionHandler`
- `ErrorResponseDTO`

Validações de entrada retornam `400` com detalhes em `Set<String>`.

Contrato padrão de erro:

```json
{
  "timestamp": "2026-05-04T21:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Dados de entrada inválidos",
  "path": "/api/exemplos",
  "details": ["nome: O nome é obrigatório"]
}
```

Mapeamento esperado:

- `MethodArgumentNotValidException`: `400`.
- `BusinessException`: `400`.
- `ResourceNotFoundException`: `404`.
- `Exception`: `500`.

## Swagger / OpenAPI

Controllers devem declarar:

- `@Tag` na classe.
- `@Operation` por endpoint.
- `@ApiResponses` com todos os status relevantes.
- `ErrorResponseDTO` como schema das respostas de erro.
- DTOs com `@Schema` e exemplos nos campos principais.

O Swagger local fica em:

```text
http://localhost:8080/swagger-ui.html
```

## CI

O repositório possui workflow em `.github/workflows/ci.yml`.

Ele roda:

```bash
./mvnw test
```

O workflow executa em `push` para `main`/`master` e em todo `pull_request`. Isso bloqueia regressões de compilação e testes antes de integrar mudanças.

## Testes Com Testcontainers

Além dos testes unitários, o projeto usa Testcontainers para validar integração real com PostgreSQL.

O teste de repository:

- sobe um container temporário de PostgreSQL;
- aponta o Spring para esse banco;
- executa as migrations Flyway;
- valida JPA, schema, queries e soft delete contra banco real.

Isso evita que a aplicação quebre só em runtime por erro de migration, nome de coluna, enum ou query derivada.

Requisito local:

```bash
docker info
```

Se esse comando falhar, inicie o Docker antes de rodar:

```bash
./mvnw test
```

## Formatação

O padrão de formatação do projeto é Google Java Format.

No IntelliJ IDEA:

1. Instale o plugin `google-java-format`.
2. Abra `Settings > google-java-format Settings`.
3. Marque `Enable google-java-format`.
4. Use o estilo padrão do Google Java Format.
5. Antes de commitar, rode `Reformat Code` nos arquivos Java alterados.

Evite misturar refatoração com reformat geral do projeto inteiro no mesmo commit. Formate os arquivos tocados pela mudança.

## Próximas Etapas

1. Configurar segurança local/dev para facilitar teste dos endpoints.
2. Criar a primeira entidade real do domínio de descarte eletrônico.
3. Evoluir as migrations Flyway conforme novas entidades forem criadas.
4. Evoluir o README conforme entidades reais substituírem o CRUD de exemplo.
