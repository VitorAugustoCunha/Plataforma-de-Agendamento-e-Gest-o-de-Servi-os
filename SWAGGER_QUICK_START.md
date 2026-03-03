# Testando a API com Swagger UI

## Iniciando a Aplicação

### Com Docker Compose (Recomendado)

```bash
docker-compose up
```

Aguarde até ver a mensagem:
```
app_1 | Application started on http://localhost:8080
```

### Sem Docker (Local)

```bash
# Certifique-se que PostgreSQL está rodando
mvn spring-boot:run
```

---

## Acessando o Swagger UI

Abra seu navegador e acesse:

```
http://localhost:8080/swagger-ui.html
```

Você verá uma interface interativa com:
- ✅ Todos os endpoints documentados
- ✅ Descrição e parâmetros de cada endpoint
- ✅ Formulários para testar os endpoints
- ✅ Exemplos de requisição e resposta

---

## Teste Rápido (5 minutos)

### 1. Autenticar

Clique em **POST /api/auth/login** e preencha:

```json
{
  "email": "cliente@agenda.local",
  "password": "devpass"
}
```

Clique em **Execute** e copie o `token` da resposta.

### 2. Autorizar no Swagger

Clique no botão **Authorize** (cadeado 🔒 no canto superior):

```
Bearer {seu-token-aqui}
```

### 3. Testar Endpoints

**Listar Provedores:**
- Vá para: `GET /api/providers`
- Clique em **Execute**
- Veja a lista de provedores cadastrados

**Listar Serviços:**
- Vá para: `GET /api/services`
- Clique em **Execute**
- Veja os serviços disponíveis

**Listar Agendamentos:**
- Vá para: `GET /api/appointments`
- Clique em **Execute**

---

## Testes Mais Completos

### Criar um Agendamento

1. **GET /api/providers** → Copie um `providerId`
2. **GET /api/services?providerId={providerId}** → Copie um `serviceId`
3. **POST /api/appointments** com body:

```json
{
  "serviceId": "{serviceId}",
  "startAt": "2026-03-15T10:00:00-03:00",
  "endAt": "2026-03-15T10:30:00-03:00"
}
```

**Adicione** o parâmetro query: `?clientId={seu-clientId}`

### Cancelar Agendamento

1. **GET /api/appointments** → Copie um `id`
2. **PATCH /api/appointments/{id}/cancel** com body:

```json
{
  "cancelReason": "Mudança de planos"
}
```

---

## Documentação Swagger

- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **Swagger YAML**: http://localhost:8080/v3/api-docs.yaml

---

## Dicas

### 1. Expandir/Colapsar Seções

Clique nos títulos (Auth, Providers, Services, Appointments) para expandir/colapsar.

### 2. Testar com Valores nos Dados de Seed

Use esses IDs pré-cadastrados:

- **Cliente ID**: `550e8400-e29b-41d4-a716-446655440002`
- **Barbeiro (Provider) ID**: `550e8400-e29b-41d4-a716-446655440011`
- **Personal (Provider) ID**: `550e8400-e29b-41d4-a716-446655440012`
- **Serviço (Corte) ID**: `550e8400-e29b-41d4-a716-446655440021`

### 3. Formato de Data/Hora

Use ISO 8601 com timezone:
```
2026-03-15T10:30:00-03:00
  │       │ │ │  │ └─ Timezone (UTC-3)
  │       │ │ │  └─ Minutos
  │       │ │ └─ Horas
  │       │ └─ Dia
  │       └─ Mês-Dia
  └─ Ano
```

### 4. Troubleshooting

**Erro 401 (Unauthorized):**
- Faça login novamente
- Copie o novo token
- Clique em Authorize e adicione de novo

**Erro 409 (Conflict):**
- Tentou agendar em horário ocupado
- Escolha outro horário

**Erro 404 (Not Found):**
- ID não existe
- Verifique se o ID está correto

---

## Próximos Passos

Agora que tem Swagger rodando, implemente:

1. **Testes** (JUnit + Testcontainers)
2. **Postman Collection** (exportar endpoints)
3. **GitHub Actions** (CI/CD)

---

## Links Úteis

- 📖 [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)
- 📋 [OpenAPI 3.0 Spec](https://spec.openapis.org/oas/v3.0.3)
- 🔐 [JWT.io - Decodificar Tokens](https://jwt.io/)
- 🧪 [Como Testar REST APIs](https://restful-api.dev/testing)
