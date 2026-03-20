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
| MySQL | 8.4 (via Docker) |
| H2 | Testes |
| Bean Validation | Validação de entrada |

---

## Pré-requisitos

- Java 21+
- Docker e Docker Compose
- Maven (ou usar o wrapper `./mvnw`)

---

## Como executar

### 1. Subir o MySQL

```bash
docker compose up -d
```

### 2. Subir o simulador

```bash
docker run -d -p 3000:3000 --name garage-sim cfontes0estapar/garage-sim:1.0.0
```

### 3. Iniciar a aplicação

```bash
./mvnw spring-boot:run
```

Ou com Maven global:

```bash
mvn spring-boot:run
```

A API sobe na **porta 3003**. No startup, ela carrega a topologia da garagem do simulador (`http://localhost:3000/garage`) e persiste no banco.

> Se o simulador usar outra porta, altere `simulator.base-url` em `src/main/resources/application.yml`.

---

## Swagger / OpenAPI

A documentação da API está disponível via Swagger UI. Com a aplicação rodando, acesse:

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

Consulta a receita de um setor em uma data específica.

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
./mvnw test
```

Os testes unitários validam as regras de pricing (multiplicador dinâmico, período gratuito e arredondamento de horas).

---

## Estrutura do projeto

A descrição de cada pacote, o modelo de dados e os diagramas de fluxo estão em [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

```text
parking-garage/
├── docker-compose.yml   # MySQL 8.4
├── docs/ARCHITECTURE.md # Arquitetura, fluxos e decisões de design
├── pom.xml
└── src/
    ├── main/java/com/example/parking/   # Código-fonte principal
    └── test/                            # Testes unitários (H2)
```

---

## Configuração

| Propriedade | Padrão | Descrição |
|---|---|---|
| `server.port` | `3003` | Porta da API |
| `simulator.base-url` | `http://localhost:3000` | URL do simulador |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/parking_garage` | Conexão MySQL |
| `spring.datasource.username` | `parking` | Usuário do banco |
| `spring.datasource.password` | `parking` | Senha do banco |
