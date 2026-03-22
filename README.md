# Parking Garage

Backend para o desafio técnico da Estapar — gerenciamento de garagem com pricing dinâmico, controle de vagas e receita.

> **Arquitetura e decisões de design** — fluxo de eventos, modelo de dados, pricing dinâmico e índices — estão documentados em [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

---

## Stack

| Tecnologia | Versão / Uso |
|---|---|
| Java | 21 |
| Spring Boot | 3.4.0 |
| Spring Data JPA | Persistência |
| Flyway | Migrations de schema |
| MySQL | 9.6 (via Docker) |
| H2 | Testes |
| Bean Validation | Validação de entrada |

---

## Pré-requisitos

- Docker e Docker Compose

---

## Como executar

```bash
docker compose up -d
```

Esse comando sobe o MySQL, o simulador e a aplicação. A API fica disponível na **porta 3003**.

No startup, ela carrega a topologia da garagem do simulador (`GET http://localhost:3000/garage`) e persiste no banco. O retry automático aguarda o simulador ficar pronto antes de prosseguir.

---

## Swagger / OpenAPI

Com a aplicação rodando, acesse:

- [http://localhost:3003/swagger-ui.html](http://localhost:3003/swagger-ui.html)

---

## Endpoints

### `POST /webhook`

Recebe eventos do simulador. Tipos suportados: `ENTRY`, `PARKED`, `EXIT`.

<details>
<summary>Exemplos de payload</summary>

**ENTRY** — veículo entra na garagem:
```json
{
  "license_plate": "ZUL0001",
  "entry_time": "2025-01-01T12:00:00.000Z",
  "event_type": "ENTRY"
}
```

**PARKED** — veículo estaciona em uma vaga:
```json
{
  "license_plate": "ZUL0001",
  "lat": -23.561684,
  "lng": -46.655981,
  "event_type": "PARKED"
}
```

**EXIT** — veículo sai da garagem:
```json
{
  "license_plate": "ZUL0001",
  "exit_time": "2025-01-01T14:10:00.000Z",
  "event_type": "EXIT"
}
```

</details>

### `GET /revenue`

Consulta a receita de um setor em uma data específica. Utiliza query parameters em vez de request body, pois RFC 9110 §9.3.1 desencoraja body em requisições GET e muitos clientes HTTP e proxies o ignoram.

| Parâmetro | Tipo | Exemplo |
|---|---|---|
| `date` | ISO date | `2025-01-01` |
| `sector` | string | `A` |

```bash
curl "http://localhost:3003/revenue?date=2025-01-01&sector=A"
```

Resposta:
```json
{
  "amount": 20.00,
  "currency": "BRL",
  "timestamp": "2025-01-01T12:00:00Z"
}
```

---

## Testes

```bash
mvn test
```

Os testes unitários cobrem as regras de pricing (`PricingPolicyServiceTest`), o fluxo completo do webhook (`WebhookServiceTest`), a lógica de receita (`RevenueServiceTest`), a carga inicial da garagem (`GarageBootstrapServiceTest`) e a camada REST de receita (`RevenueControllerTest`). O banco em testes é H2 (in-memory) com schema gerenciado pelo Flyway.

---

## Estrutura do projeto

A descrição de cada pacote, o modelo de dados e os diagramas de fluxo estão em [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

```text
parking-garage/
├── docker-compose.yml   # MySQL 9.6 + simulador + aplicação
├── Dockerfile           # Build multi-stage da aplicação
├── docs/ARCHITECTURE.md # Arquitetura, fluxos e decisões de design
├── pom.xml
└── src/
    ├── main/java/com/example/parking/   # Código-fonte principal
    └── test/                            # Testes unitários (H2)
```

---

## Configuração

Todas as propriedades são configuráveis via variáveis de ambiente no `docker-compose.yml`.

| Propriedade | Padrão | Descrição |
|---|---|---|
| `server.port` | `3003` | Porta da API |
| `simulator.base-url` | `http://127.0.0.1:3000` | URL do simulador |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/parking_garage` | Conexão MySQL |
| `spring.datasource.username` | `parking` | Usuário do banco |
| `spring.datasource.password` | `parking` | Senha do banco |
