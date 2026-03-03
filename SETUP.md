# Setup e Execução do Projeto

## Pré-requisitos

- Docker e Docker Compose instalados
- Java 25+ (para desenvolvimento local sem Docker)
- Maven 3.9+ (para desenvolvimento local sem Docker)
- PostgreSQL 16+ (se não usar Docker)

## Execução com Docker Compose (Recomendado)

### 1. Clone o repositório
```bash
git clone <seu-repo>
cd projeto
```

### 2. Inicie os serviços
```bash
docker-compose up -d
```

Isso vai:
- Subir um container PostgreSQL 16
- Compilar e subir o aplicativo Spring Boot
- Aplicar automaticamente as migrations Flyway

### 3. Verificar logs
```bash
docker-compose logs -f app
```

### 4. Parar os serviços
```bash
docker-compose down
```

---

## Execução Local (sem Docker)

### 1. Instale e configure PostgreSQL
```bash
# Linux/Mac
brew install postgresql
# ou Windows: baixe do postgresql.org

# Inicie o serviço
pg_ctl start -D /usr/local/var/postgres
```

### 2. Crie o banco de dados
```bash
createdb -U devuser -W agenda_db
# Será solicitada a senha: devpass
```

Ou use psql:
```bash
psql -U postgres
CREATE DATABASE agenda_db;
CREATE USER devuser WITH PASSWORD 'devpass';
GRANT ALL PRIVILEGES ON DATABASE agenda_db TO devuser;
```

### 3. Configure variáveis de ambiente (opcional)
```bash
cp .env.example .env
# Edite .env com suas configurações
```

### 4. Execute o aplicativo
```bash
# Compile e rode
mvn clean spring-boot:run

# Ou compile e execute o JAR
mvn clean package
java -jar target/plataform-0.0.1-SNAPSHOT.jar
```

O aplicativo vai:
- Conectar ao PostgreSQL
- Executar as migrations Flyway automaticamente
- Inciar em http://localhost:8080

---

## Verificação da Saúde

### Health Check
```bash
curl http://localhost:8080/health
```

Resposta esperada:
```json
{
  "status": "UP"
}
```

---

## Dados de Teste

As migrations incluem dados de seed:
- **Admin**: admin@agenda.local / password
- **Cliente**: cliente@agenda.local / password
- **Barbeiro**: barbeiro@agenda.local / password
- **Personal**: personal@agenda.local / password

Todas com senha: (use a do migration ou configura a sua)

---

## Estrutura de Migrations

```
src/main/resources/db/migration/
├── V1__init.sql           # Schema inicial com todas as tabelas
└── V2__seed_data.sql      # Dados de exemplo para desenvolvimento
```

### Versioning
- Seguir padrão Flyway: `V{VERSION}__description.sql`
- Versões devem ser sequenciais: V1__, V2__, V3__...
- Nunca alterar migrations já aplicadas!

---

## Troubleshooting

### Erro: "Network 'agenda_default' not found"
```bash
docker-compose down
docker network prune
docker-compose up
```

### Erro: "Connection refused" (PostgreSQL)
```bash
# Verifique se o container está rodando
docker-compose ps

# Reinicie
docker-compose restart postgres
```

### Erro: Flyway validation failed
- Uma migration foi alterada após ser executada
- Solução: resetar o banco (DEV only)
  ```bash
  docker-compose down -v  # -v remove volumes
  docker-compose up
  ```

### Porta 5432 já em uso
```bash
# Mude no docker-compose.yml ou mude a porta do PostgreSQL local
docker ps | grep 5432
kill <process>
```

---

## Endpoints Principais (após startup)

### Auth
- `POST /auth/register` - Registrar novo usuário
- `POST /auth/login` - Fazer login

### Providers
- `GET /providers` - Listar provedores
- `GET /providers/{id}` - Detalhes do provedor
- `GET /providers/{id}/availability?date=2026-03-10` - Horários livres

### Agendamentos
- `POST /appointments` - Agendar
- `GET /appointments` - Listar meus agendamentos
- `PATCH /appointments/{id}/cancel` - Cancelar

Consulte a documentação Swagger em: `http://localhost:8080/swagger-ui.html` (após adicionar springdoc-openapi)

---

## Desenvolvimento

### Adicionar nova migration
```bash
# 1. Crie o arquivo
touch src/main/resources/db/migration/V3__add_new_table.sql

# 2. Escreva o SQL

# 3. Restart da aplicação (Flyway aplica automaticamente)
```

### Profile específico para testes
```bash
# Rodar com profile de testes
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test"
```

---

## Links Úteis

- [Flyway Documentation](https://flywaydb.org/documentation/database/postgresql)
- [Spring Boot + Flyway](https://spring.io/guides/gs/database-migration-flyway/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)
