# Documentação da API - Swagger/OpenAPI

## Como Acessar

Após iniciar a aplicação, acesse:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Endpoints Principais

### Autenticação

#### 1. Registrar Novo Usuário

```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123",
  "role": "CLIENT"
}
```

**Respostas:**
- `201 Created`: Usuário criado com sucesso
- `400 Bad Request`: Validação falhou

#### 2. Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "password": "senha123"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": "550e8400-e29b-41d4-a716-446655440002",
  "email": "joao@example.com",
  "name": "João Silva",
  "role": "CLIENT"
}
```

---

### Provedores

#### 1. Criar Perfil de Provedor

```http
POST /api/providers
Content-Type: application/json
Authorization: Bearer {token}

{
  "userId": "550e8400-e29b-41d4-a716-446655440003",
  "bio": "Barbeiro experiente",
  "locationText": "Centro, São Paulo",
  "minAdvanceMinutes": 30,
  "cancelWindowMinutes": 120,
  "slotStepMinutes": 30
}
```

#### 2. Listar Provedores

```http
GET /api/providers?page=0&size=10
Authorization: Bearer {token}
```

#### 3. Obter Detalhes do Provedor

```http
GET /api/providers/{providerId}
Authorization: Bearer {token}
```

---

### Serviços

#### 1. Criar Serviço

```http
POST /api/services
Content-Type: application/json
Authorization: Bearer {token}

{
  "providerId": "550e8400-e29b-41d4-a716-446655440011",
  "name": "Corte de Cabelo",
  "description": "Corte padrão com secagem",
  "durationMinutes": 30,
  "priceCents": 5000,
  "active": true
}
```

#### 2. Listar Serviços

```http
GET /api/services?providerId={providerId}&active=true&page=0&size=10
Authorization: Bearer {token}
```

#### 3. Listar Serviços de um Provedor

```http
GET /api/services/provider/{providerId}?page=0&size=10
Authorization: Bearer {token}
```

---

### Agendamentos

#### 1. Agendar Serviço

```http
POST /api/appointments?clientId={clientId}
Content-Type: application/json
Authorization: Bearer {token}

{
  "serviceId": "550e8400-e29b-41d4-a716-446655440021",
  "startAt": "2026-03-10T10:00:00-03:00",
  "endAt": "2026-03-10T10:30:00-03:00"
}
```

**Respostas:**
- `201 Created`: Agendamento criado
- `409 Conflict`: Conflito de horário

#### 2. Listar Meus Agendamentos

```http
GET /api/appointments/client/{clientId}?page=0&size=10
Authorization: Bearer {token}
```

#### 3. Listar Agendamentos de um Provedor

```http
GET /api/appointments/provider/{providerId}?page=0&size=10
Authorization: Bearer {token}
```

#### 4. Obter Detalhes do Agendamento

```http
GET /api/appointments/{appointmentId}
Authorization: Bearer {token}
```

#### 5. Cancelar Agendamento

```http
PATCH /api/appointments/{appointmentId}/cancel
Content-Type: application/json
Authorization: Bearer {token}

{
  "cancelReason": "Motivo do cancelamento"
}
```

**Restrições:**
- Cancelamento válido apenas se feito com antecedência mínima

#### 6. Completar Agendamento

```http
PATCH /api/appointments/{appointmentId}/complete
Authorization: Bearer {token}
```

**Permissão:**
- Apenas o provedor pode completar

---

## Autenticação

### Usando JWT Token

Todos os endpoints (exceto `/auth/register` e `/auth/login`) requerem:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Roles Disponíveis

- **CLIENT**: Pode criar e visualizar próprios agendamentos
- **PROVIDER**: Pode gerenciar serviços, disponibilidades e completar agendamentos
- **ADMIN**: Acesso total

---

## Filtros e Paginação

### Paginação

Todos os endpoints GET com listagem suportam:

```http
GET /api/endpoint?page=0&size=20&sort=createdAt,desc
```

- `page`: Número da página (começa em 0)
- `size`: Quantidade de itens por página
- `sort`: Ordenação (ex: `createdAt,desc`)

### Filtros

Exemplos:

```http
GET /api/appointments?clientId={id}&status=SCHEDULED&providerId={id}
GET /api/services?providerId={id}&active=true&name=Corte
```

---

## Códigos de Resposta HTTP

| Código | Significado |
|--------|------------|
| 200 | OK - Sucesso |
| 201 | Created - Recurso criado |
| 400 | Bad Request - Validação falhou |
| 401 | Unauthorized - Sem autenticação |
| 403 | Forbidden - Sem permissão |
| 404 | Not Found - Recurso não encontrado |
| 409 | Conflict - Conflito (ex: horário ocupado) |
| 500 | Internal Server Error |

---

## Exemplos de Teste com cURL

### 1. Registrar

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@example.com",
    "password": "senha123",
    "role": "CLIENT"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "password": "senha123"
  }'
```

### 3. Listar Provedores (com token)

```bash
curl -X GET http://localhost:8080/api/providers \
  -H "Authorization: Bearer {seu-token-aqui}"
```

---

## Formato de Datas

Todas as datas usam ISO 8601 com timezone:

```
2026-03-10T10:30:00-03:00
```

---

## Tratamento de Erros

As respostas de erro seguem o padrão:

```json
{
  "timestamp": "2026-03-10T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validação falhou",
  "details": {
    "email": "Email já está registrado",
    "password": "Deve ter no mínimo 6 caracteres"
  }
}
```

---

## Dados de Teste (Seed Data)

O banco de dados já vem preenchido com dados de teste:

**Usuários:**

| Email | Senha | Role |
|-------|-------|------|
| admin@agenda.local | password | ADMIN |
| cliente@agenda.local | password | CLIENT |
| barbeiro@agenda.local | password | PROVIDER |
| personal@agenda.local | password | PROVIDER |

**Serviços:**

- Corte Simples (30 min, R$ 50)
- Barba Completa (30 min, R$ 30)
- Sessão Personal 1h (60 min, R$ 150)
- Avaliação Física (30 min, R$ 50)

---

## Health Check

```http
GET /health
```

Resposta:
```
Application is running
```

---

## Links Úteis

- [Swagger/OpenAPI Specification](https://swagger.io/specification/)
- [springdoc-openapi Documentation](https://springdoc.org/)
- [JWT Authentication](https://jwt.io/)
- [ISO 8601 Dates](https://en.wikipedia.org/wiki/ISO_8601)
