# Guia de Testes - JUnit 5 + Mockito

## Visão Geral

O projeto possui testes unitários implementados com **JUnit 5** e **Mockito** para validar a lógica de negócio dos Services principais.

## Estrutura de Testes

```
src/test/java/com/agenda/plataform/
├── service/
│   ├── AppointmentServiceTest.java       # Testes do serviço de agendamentos
│   └── ProviderAvailabilityServiceTest.java  # Testes de disponibilidade
└── PlataformApplicationTests.java        # Teste básico de inicialização
```

## Dependências

Adicionadas no `pom.xml`:

```xml
<!-- Testing: JUnit 5, Mockito, Testcontainers -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
```

## Testes Unitários

### 1. AppointmentServiceTest

Testa a lógica principal de agendamentos:

#### ✅ Testes Implementados

| Test | Objetivo |
|------|----------|
| `testCreateAppointmentSuccess()` | Validar criação bem-sucedida de agendamento |
| `testCancelAppointmentSuccess()` | Validar cancelamento de agendamento |
| `testCompleteAppointmentSuccess()` | Validar conclusão de agendamento |
| `testCompleteAlreadyCanceledAppointment()` | Rejeitar conclusão de agendamento cancelado |
| `testFindAppointmentById()` | Validar busca por ID |

#### Técnicas Utilizadas

- **@ExtendWith(MockitoExtension.class)** - Integração JUnit 5 com Mockito
- **@Mock** - Mock de dependências (Repository, Services)
- **@InjectMocks** - Injeção de mocks no Service testado
- **ArgumentMatchers** - Flexibilidade nos assertions
- **Verify** - Validação de chamadas aos mocks

#### Exemplo de Teste

```java
@Test
@DisplayName("Deve criar agendamento com sucesso")
void testCreateAppointmentSuccess() {
    // Arrange - Preparar dados e mocks
    when(appointmentRepository.save(any(AppointmentEntity.class)))
            .thenReturn(appointment);

    // Act - Executar ação
    AppointmentEntity created = appointmentService.create(
            client.getId(),
            service.getId(),
            appointment
    );

    // Assert - Validar resultado
    assertNotNull(created);
    assertEquals(AppointmentStatus.SCHEDULED, created.getStatus());
    verify(appointmentRepository, times(1)).save(any());
}
```

### 2. ProviderAvailabilityServiceTest

Testa a lógica de disponibilidades do provedor:

#### ✅ Testes Implementados

| Test | Objetivo |
|------|----------|
| `testCreateAvailabilitySuccess()` | Validar criação de disponibilidade |
| `testCreateAvailabilityWithInvalidTime()` | Rejeitar horários inválidos |
| `testDeleteAvailabilitySuccess()` | Validar deleção de disponibilidade |
| `testDeleteNonExistentAvailability()` | Rejeitar deleção de ID inexistente |
| `testAvailabilityBelongsToProvider()` | Validar relacionamento |

## Estrutura dos Testes

### Padrão AAA (Arrange-Act-Assert)

Todo teste segue este padrão:

```java
@Test
void testSomething() {
    // ARRANGE - Preparar dados, mocks e estado
    when(repository.save(any())).thenReturn(entity);
    
    // ACT - Executar a ação que está sendo testada
    Entity result = service.create(entity);
    
    // ASSERT - Validar os resultados
    assertEquals(expected, result.getField());
    verify(repository).save(any());
}
```

## Executar Testes

### Compilar Testes

```bash
mvn test-compile
```

### Rodar Todos os Testes

```bash
mvn test
```

### Rodar Teste Específico

```bash
mvn test -Dtest=AppointmentServiceTest
```

### Rodar com Cobertura

```bash
mvn test jacoco:report
# Relatório em: target/site/jacoco/index.html
```

## Validações Testadas

### AppointmentService

✅ **Criação**
- Salva agendamento com sucesso
- Popula campos corretamente
- Chama repository uma vez

✅ **Cancelamento**
- Muda status para CANCELED
- Registra motivo do cancelamento
- Valida permissões

✅ **Conclusão**
- Muda status para COMPLETED
- Rejeita se já cancelado
- Rejeita se não for provedor

✅ **Busca**
- Retorna agendamento por ID
- Valida existência

### ProviderAvailabilityService

✅ **Criação**
- Salva disponibilidade
- Valida horários (início < fim)
- Associa ao provedor

✅ **Validação**
- Rejeita horários inválidos
- Rejeita deleção de ID inexistente
- Valida relacionamentos

✅ **Deleção**
- Remove disponibilidade existente
- Verifica existência antes de deletar

## Mock Objects

### Mocks Utilizados

```java
@Mock
private AppointmentRepository appointmentRepository;

@Mock
private ProviderAvailabilityService providerAvailabilityService;

@Mock
private ProviderTimeOffService providerTimeOffService;

@InjectMocks
private AppointmentService appointmentService;
```

## Best Practices Aplicadas

✅ **Isolamento** - Cada teste testamocka as dependências  
✅ **Independência** - Testes não dependem um do outro  
✅ **Limpeza** - @BeforeEach reseta estado a cada teste  
✅ **Nomenclatura Clara** - `@DisplayName` para descrição humana  
✅ **Verificação** - Uso de `verify()` para garantir chamadas  

## Cobertura de Testes

Atualmente cobertas as principais funcionalidades:

- ✅ **AppointmentService**: 5 testes
- ✅ **ProviderAvailabilityService**: 5 testes  
- 🔲 **Integração Database**: Preparado com Testcontainers
- 🔲 **Controllers**: Preparado com MockMvc

## Próximos Passos

### Expandir Cobertura

1. **Adicionar mais testes de edge cases**
   - Datas no passado
   - Agendamentos com overlap
   - Timezones diferentes

2. **Testes de Integração**
   - Com Testcontainers para banco real
   - Validar migrations Flyway
   - Testes de persistência

3. **Testes de Controllers**
   - MockMvc para testes de API
   - Validar status HTTP
   - Testes de autenticação JWT

## Exemplo de Execução

```bash
$ mvn test

[INFO] Running com.agenda.plataform.service.AppointmentServiceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.256 s
[INFO] 
[INFO] Running com.agenda.plataform.service.ProviderAvailabilityServiceTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.189 s
[INFO] 
[INFO] BUILD SUCCESS
```

## Documentação Útil

- 📖 [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- 📘 [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- 🧪 [Testcontainers](https://www.testcontainers.org/quickstart/junit_5_quickstart/)
- 📊 [JaCoCo Code Coverage](https://www.jacoco.org/jacoco/index.html)

---

**Nota**: Testcontainers e testes de integração estão preparados mas requerem Spring Boot Test que inclui MockMvc. Para ativar testes de integração, adicione `spring-boot-starter-test` ao pom.xml (já adicionado).
