## Projeto: **Plataforma de Agendamento e Gestão de Serviços**

Um sistema onde **clientes** agendam horários com **profissionais** (barbeiro, estética, personal trainer, consultor, etc.), com regras de disponibilidade, pagamentos opcionais, notificações e painel administrativo.

### Objetivo do sistema

* Cliente cria conta, escolhe um profissional e **agenda um horário**.
* Profissional configura **agenda, serviços, duração, folgas e exceções**.
* Sistema evita conflitos (overbooking), aplica regras (cancelamento, tolerância, etc).
* Notificações (email/whatsapp/sms simulável) para confirmações e lembretes.
* Painel admin para auditoria e controle.

---

## Stack sugerida (você pode escolher o que aplicar)

* **Java 17+**
* **Spring Boot**: Web, Validation, Data JPA, Security
* **PostgreSQL**
* **Flyway** (migração)
* **Docker + docker-compose**
* **OpenAPI/Swagger**
* **JUnit 5 + Mockito**
* **Testcontainers** (opcional, mas muito forte)
* **Redis** (opcional: cache + rate limit)
* **Kafka/RabbitMQ** (opcional: eventos de agendamento/notificação)

---

## Domínio (modelagem)

### Entidades principais

* **User**: id, nome, email, senhaHash, role (CLIENT/PROVIDER/ADMIN), ativo
* **ProviderProfile**: userId, bio, localização, regras (ex: antecedência mínima)
* **ServiceOffering**: id, providerId, nome, descrição, duraçãoMin, preçoCents, ativo
* **ProviderAvailability**: id, providerId, diaSemana, horaInicio, horaFim (janelas fixas)
* **ProviderTimeOff**: id, providerId, dataInicio, dataFim, motivo (folgas/exceções)
* **Appointment**: id, clientId, providerId, serviceId, startAt, endAt, status (SCHEDULED/CANCELED/COMPLETED), cancelReason, createdAt
* **Payment** (opcional): id, appointmentId, status, method, amountCents, externalId
* **NotificationLog**: id, userId, type, channel, payload, sentAt, status

### Regras de negócio que deixam o projeto “real”

* Não pode marcar fora da disponibilidade do profissional.
* Não pode marcar em folga/timeOff.
* Não pode marcar em horário já ocupado.
* Pode exigir **antecedência mínima** (ex: 2h antes).
* Cancelamento permitido até X horas antes.
* Status transitions controladas (não pode “COMPLETED” direto de “CANCELED”).

---

## Endpoints (REST) — sugestão organizada

### Auth

* `POST /auth/register`
* `POST /auth/login`
* `POST /auth/refresh` (opcional)
* `POST /auth/logout` (opcional)

### Providers (catálogo)

* `GET /providers` (filtro por cidade, serviço, rating futuramente)
* `GET /providers/{id}`
* `GET /providers/{id}/services`
* `GET /providers/{id}/availability?date=YYYY-MM-DD` (gera horários livres)

### Serviços do profissional

* `POST /me/services`
* `PUT /me/services/{id}`
* `PATCH /me/services/{id}/active`
* `GET /me/services`

### Agenda do profissional

* `POST /me/availability`
* `PUT /me/availability/{id}`
* `DELETE /me/availability/{id}`
* `POST /me/time-off`
* `GET /me/time-off`

### Agendamentos

* `POST /appointments` (cliente cria)
* `GET /appointments` (cliente vê os seus)
* `GET /me/appointments` (profissional vê os dele)
* `PATCH /appointments/{id}/cancel`
* `PATCH /appointments/{id}/complete` (profissional)

### Admin

* `GET /admin/appointments` (filtros + paginação)
* `GET /admin/audit` (opcional: logs/ações)
* `PATCH /admin/users/{id}/disable`

---

## DTOs, validação e resposta padrão

* Use DTOs separados para **request/response**.
* Validações:

  * `@NotBlank` nome/descrição
  * `@Email` email
  * `@Min(1)` duraçãoMin/preçoCents
  * validação custom: `startAt < endAt`, “horário alinhado em blocos de 15 min”, etc.
* Padrão de resposta (exemplo):

  * `{ "data": ..., "meta": { "page":..., "size":... }, "errors": [] }`

---

## Persistência e consultas “de verdade”

* Paginação e filtros:

  * `GET /providers?serviceId=&city=&page=&size=&sort=`
* Busca e filtro avançado com:

  * `Specification` (Spring Data JPA) **ou** QueryDSL (opcional)
* Índices importantes no Postgres:

  * `(provider_id, start_at)` em Appointment
  * `(client_id, created_at)`
* Regra anti-conflito:

  * checar overlap: `startAt < existingEnd && endAt > existingStart`
  * e/ou usar constraint mais avançada (nível extra)

---

## Segurança (Spring Security)

* JWT com roles:

  * CLIENT: cria/visualiza próprios agendamentos
  * PROVIDER: configura agenda + finaliza agendamento
  * ADMIN: acessa tudo
* Method security:

  * `@PreAuthorize("hasRole('PROVIDER')")`
* Proteções extras:

  * rate-limit no login (Redis opcional)
  * lock de conta após tentativas (opcional)

---

## Tratamento global de erros

Crie:

* `@ControllerAdvice` com:

  * validação (400)
  * não encontrado (404)
  * conflito de agenda (409)
  * acesso negado (403)
* Retorno consistente com `code`, `message`, `details`

---

## Testes (bem direcionado)

### Unitários (JUnit + Mockito)

* `AppointmentService`:

  * cria agendamento dentro da disponibilidade
  * rejeita conflito
  * rejeita em timeOff
  * aplica antecedência mínima
* `AvailabilityService`:

  * gera slots do dia (15/30 min) e remove ocupados

### Integração (Testcontainers)

* sobe Postgres e valida:

  * migrations Flyway
  * endpoint de criar agendamento
  * paginação e filtros

---

## Assíncrono e eventos (opcional, mas fica muito bom)

Quando cria `Appointment`:

* publica evento `AppointmentCreated`
* consumer cria `NotificationLog` e “envia” notificação (mock/email fake)
* Se usar Kafka/RabbitMQ: isola bem responsabilidades

---

## Docker

`docker-compose.yml` com:

* app
* postgres
* (opcional) redis
* (opcional) kafka/rabbit

---

## Organização de pacotes (sugestão)

```
com.appointmently
  config/
  security/
  controller/
  dto/
  entity/
  repository/
  service/
  mapper/
  exception/
  event/
  util/
```

---

## Entregáveis que deixam seu projeto “portfolio-ready”

* README com:

  * como rodar (docker-compose)
  * endpoints principais
  * regras de negócio
* Swagger bem descrito
* Seed de dados (dev)
* Coleção Postman/Insomnia
* Testes rodando no CI (GitHub Actions)
