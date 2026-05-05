# Guia De Desenvolvimento

Este arquivo resume como codar novas funcionalidades mantendo o padrão simples do projeto.

## Regra Principal

Não esconda comportamento importante atrás de abstrações genéricas.

Use contratos pequenos para padronizar, mas mantenha cada entidade clara:

- controller explícito;
- service concreto;
- repository próprio;
- mapper próprio;
- DTOs próprios.

## Criando Um CRUD

Para uma entidade `Produto`, crie:

```text
model/produto/Produto.java
model/produto/dto/ProdutoRequest.java
model/produto/dto/ProdutoResponse.java
repository/ProdutoRepository.java
mapper/ProdutoMapper.java
service/ProdutoService.java
controller/ProdutoController.java
resources/db/migration/V2__create_produto_table.sql
```

## Entity

A entidade deve herdar de `BaseEntity`.

```java
@Entity
@Table(name = "produto")
public class Produto extends BaseEntity {
    // campos de negócio
}
```

Não duplique campos técnicos na entidade concreta.

Campos técnicos já existem na base:

- `id`
- `version`
- `createdAt`
- `updatedAt`
- `entityStatus`
- `deletedAt`

## DTOs

Use records para DTOs.

O mesmo request pode ser usado em create e update quando as validações forem iguais.

```java
public record ProdutoRequest(
        @NotBlank String nome
) {
}
```

Response deve expor o que a API precisa retornar. Pode incluir campos técnicos quando ajudar no debug e no contrato da API.

## Repository

Repositories devem ignorar soft delete nas buscas públicas.

```java
Optional<Produto> findByIdAndEntityStatusNot(UUID id, EntityStatus entityStatus);

Set<Produto> findAllByEntityStatusNot(EntityStatus entityStatus);
```

## Mapper

Use MapStruct e estenda `BaseMapper`.

Ignore campos técnicos no `toEntity` e no `updateEntityFromRequest`.

```java
@Mapper(componentModel = "spring")
public interface ProdutoMapper extends BaseMapper<Produto, ProdutoRequest, ProdutoResponse> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "entityStatus", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Produto toEntity(ProdutoRequest request);
}
```

## Service

Service implementa `BaseService`.

Ele deve:

- abrir transações;
- buscar entidade ativa;
- lançar `ResourceNotFoundException` quando não encontrar;
- aplicar validações específicas;
- usar mapper;
- executar soft delete.

Controller não deve mapear DTO manualmente.

## Controller

Controller deve:

- declarar endpoints explicitamente;
- usar `@Valid`;
- retornar `ResponseEntity`;
- chamar somente o service;
- documentar endpoints com Swagger/OpenAPI.
- declarar respostas de erro com `ErrorResponseDTO`.
- cobrir `400`, `404` e `500` quando fizer sentido para o endpoint.

Não crie `BaseController`.

Exemplo de resposta de erro no Swagger:

```java
@ApiResponse(
        responseCode = "404",
        description = "Produto não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
)
```

## Flyway

Cada tabela deve incluir os campos técnicos da `BaseEntity`.

Modelo básico:

```sql
CREATE TABLE IF NOT EXISTS produto (
    id UUID PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    entity_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    deleted_at TIMESTAMP,
    nome VARCHAR(255) NOT NULL,
    CONSTRAINT ck_produto_entity_status
        CHECK (entity_status IN ('ACTIVE', 'INACTIVE', 'DELETED'))
);
```

Adicione índices de acordo com as queries do repository.

Use sempre o próximo número de migration:

```text
V1__create_exemplo_table.sql
V2__create_produto_table.sql
V3__add_produto_codigo_column.sql
```

Não altere uma migration que já foi aplicada em outro ambiente. Crie uma nova.

## Testes

Service:

- teste caminhos positivos e negativos de todos os métodos públicos;
- use `assertThat` para retorno e estado;
- use `assertThatThrownBy` para exceptions;
- finalize com `verify`, `verifyNoInteractions` e `verifyNoMoreInteractions`.

Controller:

- teste status de sucesso;
- teste validação `400`;
- teste `ResourceNotFoundException` como `404`;
- teste `BusinessException` como `400`;
- teste erro inesperado como `500` quando o endpoint puder propagar exception.

Repository:

- use Testcontainers para testar PostgreSQL real;
- valide se Flyway cria o schema esperado;
- teste queries derivadas do Spring Data;
- cubra filtros de soft delete com registros `ACTIVE` e `DELETED`.

Nomeie esses testes com sufixo `Test` para rodarem no `./mvnw test`.

Exemplo:

```text
repository/ProdutoRepositoryTest.java
```

## CI

O workflow de CI roda `./mvnw test` no módulo `Descarte-Eletronico`.

Ele deve passar antes de integrar qualquer PR.

Como a suíte usa Testcontainers, o Docker precisa estar disponível no ambiente local ou no runner de CI.

## Formatação

Use Google Java Format para manter o padrão do código.

No IntelliJ IDEA:

1. Instale o plugin `google-java-format`.
2. Habilite em `Settings > google-java-format Settings`.
3. Use `Reformat Code` nos arquivos Java alterados antes do commit.

Não faça commit apenas reformatando arquivos que não têm relação com a tarefa.

## Ambiente Local

Copie o arquivo de exemplo:

```bash
cp .env.example .env
```

Suba o banco:

```bash
docker compose up -d postgres
```

Rode os testes:

```bash
./mvnw test
```

Rode a aplicação:

```bash
./mvnw spring-boot:run
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```
