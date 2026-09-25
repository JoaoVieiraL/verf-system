# Roadmap — VERF

Este documento registra a evolução planejada do VERF e os principais pontos técnicos que ainda precisam ser implementados ou refinados.

O roadmap não representa que todas as funcionalidades listadas serão implementadas imediatamente. Ele funciona como um registro de evolução técnica e ajuda a manter o desenvolvimento organizado.

## Estado atual

O projeto já possui a estrutura básica de uma API REST com:

- Controllers
- Services
- Repositories
- Entities
- DTOs
- Spring Data JPA
- PostgreSQL
- Spring Security
- operações CRUD em desenvolvimento

A prioridade atual é consolidar as regras de negócio e melhorar a qualidade da API antes de adicionar funcionalidades mais avançadas.

---

# 1. Validação e tratamento de erros

**Prioridade: alta**

### Objetivo

Fazer com que a API diferencie corretamente erros de entrada, recursos inexistentes, conflitos de negócio e falhas internas.

### Tarefas

- ✅ Adicionar `@Valid` aos DTOs recebidos pelos Controllers.
- ✅ Adicionar validações como `@NotBlank`, `@Size`, `@Positive` e outras adequadas a cada DTO.
- ✅ Criar exceções específicas para recursos não encontrados.
- ✅ Criar exceções para regras de negócio.
- ✅ Criar `@RestControllerAdvice`.
- [ ] Padronizar respostas de erro.
- [ ] Utilizar `ProblemDetail` quando fizer sentido.
- [ ] Evitar retornar stacktrace ou detalhes internos para o cliente.
- [ ] Tratar conflitos de integridade do banco, como valores únicos duplicados.

### Resultado esperado

Exemplo:

```json
{
  "title": "Tinta não encontrada",
  "status": 404,
  "detail": "Nenhuma tinta encontrada com o id 42"
}
```

---

# 2. DTOs e contrato da API

**Prioridade: alta**

### Objetivo

Evitar que as entidades JPA sejam utilizadas diretamente como contrato de resposta da API.

### Tarefas

- [ ] Criar Response DTOs para os recursos relevantes.
- [ ] Criar `FuncionarioResponseDto` sem `senha_hash`.
- [ ] Separar DTOs de entrada e saída quando necessário.
- [ ] Revisar quais campos realmente devem ser aceitos pelo cliente.
- [ ] Evitar que campos controlados pelo servidor sejam enviados livremente pelo cliente.
- [ ] Revisar relacionamentos para evitar recursão na serialização JSON.
- [ ] Avaliar MapStruct depois que o mapeamento manual estiver bem compreendido.

### Princípio

```text
Request DTO → Service → Entity → Banco

Banco → Entity → Response DTO → Cliente
```

---

# 3. Padronização REST

**Prioridade: alta**

### Objetivo

Tornar os endpoints previsíveis e consistentes.

### Tarefas

- [ ] Padronizar prefixo `/api/v1`.
- [ ] Usar nomes de recursos no plural.
- [ ] Utilizar letras minúsculas nas rotas.
- ✅ Remover padrões como `ID/{id}`.
- [ ] Revisar o uso atual de `/v1` e `/v2`.
- [ ] Definir corretamente `POST`, `GET`, `PUT`, `PATCH` e `DELETE`.
- [ ] Utilizar status HTTP apropriados.
- [ ] Adicionar paginação aos endpoints de listagem quando necessário.
- [ ] Adicionar filtros de consulta relevantes.

### Exemplo desejado

```text
GET    /api/v1/tintas
GET    /api/v1/tintas/{id}
POST   /api/v1/tintas
PUT    /api/v1/tintas/{id}
PATCH  /api/v1/tintas/{id}/inativar
```

---

# 4. Regras de negócio e estoque

**Prioridade: muito alta**

Esta é uma das partes mais importantes do VERF.

### Objetivo

Garantir que o estoque seja consequência das operações realizadas e que as movimentações sejam rastreáveis.

### Tarefas

- ✅ Definir claramente os tipos de movimentação.
- ✅ Validar quantidade maior que zero.
- ✅ Impedir saída maior que o estoque disponível.
- ✅ Registrar cada movimentação.
- [ ] Atualizar o estoque a partir da operação de negócio.
- [ ] Evitar alteração direta e arbitrária do saldo do estoque.
- [ ] Definir como ajustes manuais serão registrados.
- [ ] Definir o fluxo de produção de tinta.
- [ ] Validar disponibilidade das matérias-primas antes da produção.
- [ ] Registrar consumo das matérias-primas.
- [ ] Registrar entrada do produto produzido.
- [ ] Garantir consistência entre produção, movimentação e estoque.
- [ ] Utilizar `@Transactional` nos fluxos que alteram múltiplos registros.

### Princípio

```text
Entrada
   ↓
Movimentação
   ↓
Regra de negócio
   ↓
Estoque

Produção
   ↓
Consumo das matérias-primas
   ↓
Movimentações
   ↓
Entrada da tinta produzida
   ↓
Estoque
```

O objetivo é evitar um cenário em que o saldo seja alterado sem existir uma operação que explique essa alteração.

---

# 5. Datas de auditoria

**Prioridade: média**

### Objetivo

Manter `criadoEm` e `atualizadoEm` sob responsabilidade do backend.

### Tarefas

- ✅ Utilizar `@PrePersist` para preencher `criadoEm` e `atualizadoEm`.
- [ ] Utilizar `@PreUpdate` para atualizar `atualizadoEm`.
- [ ] Evitar receber essas datas diretamente em DTOs de criação.
- [ ] Revisar se as datas de movimentação e auditoria representam corretamente o momento da operação.

Exemplo:

```java
@PrePersist
public void prePersist() {
    LocalDateTime agora = LocalDateTime.now();
    criadoEm = agora;
    atualizadoEm = agora;
}

@PreUpdate
public void preUpdate() {
    atualizadoEm = LocalDateTime.now();
}
```

---

# 6. Segurança

**Prioridade: alta**

### Estado atual

O Spring Security está presente, mas as rotas ainda estão temporariamente liberadas para facilitar o desenvolvimento:

```java
.anyRequest().permitAll()
```

Isso é aceitável durante o desenvolvimento local, mas não deve permanecer em uma versão de produção.

### Tarefas

- [ ] Criar `PasswordEncoder`.
- [ ] Armazenar senhas somente em formato de hash.
- [ ] Nunca retornar `senha_hash` em Response DTO.
- [ ] Criar autenticação.
- [ ] Implementar JWT quando a base de autenticação estiver compreendida.
- [ ] Criar endpoint de login.
- [ ] Configurar expiração do token.
- [ ] Implementar autorização por nível de acesso.
- [ ] Utilizar `@PreAuthorize` nos endpoints sensíveis.
- [ ] Configurar CORS de forma explícita quando houver frontend.
- [ ] Revisar CSRF de acordo com a estratégia de autenticação adotada.

### Observação

A implementação de JWT não deve ser feita apenas para "marcar como concluído". Primeiro é importante entender:

```text
Authentication
Authorization
Password hashing
SecurityContext
JWT
Roles/Authorities
```

---

# 7. Persistência e banco de dados

**Prioridade: média/alta**

### Tarefas

- [ ] Adicionar Flyway.
- [ ] Criar migration inicial do banco.
- [ ] Trocar `ddl-auto: update` por uma estratégia adequada ao ambiente.
- [ ] Utilizar `validate` quando o schema passar a ser controlado pelas migrations.
- [ ] Criar índices relevantes.
- [ ] Revisar constraints e chaves estrangeiras.
- [ ] Revisar `FetchType`.
- [ ] Identificar possíveis problemas de N+1 queries.
- [ ] Padronizar tipos de ID (`Integer`/`Long`) com um critério definido.
- [ ] Revisar nomes e tipos das colunas.

### Desenvolvimento x produção

Durante o aprendizado, `ddl-auto: update` pode facilitar os testes locais.

Quando o projeto passar a ter migrations:

```text
Hibernate → valida o schema
Flyway    → controla as alterações do schema
```

---

# 8. Testes automatizados

**Prioridade: alta**

### Tarefas

- [ ] Substituir o teste inicial `contextLoads()` por testes reais.
- [ ] Criar testes unitários dos Services.
- [ ] Testar casos de sucesso.
- [ ] Testar recursos inexistentes.
- [ ] Testar regras de negócio.
- [ ] Testar inativação.
- [ ] Testar movimentações de estoque.
- [ ] Testar produção e consumo de matéria-prima.
- [ ] Criar testes dos Controllers com `MockMvc`.
- [ ] Criar testes de Repository quando necessário.
- [ ] Avaliar Testcontainers para testes que dependem de PostgreSQL.
- [ ] Avaliar JaCoCo posteriormente para acompanhar cobertura.

### Primeiros testes recomendados

```text
FornecedorService
 ├── deveBuscarFornecedorExistente
 ├── deveLancarExcecaoQuandoNaoEncontrar
 └── deveInativarFornecedor

TintaService
 ├── deveBuscarTintaExistente
 ├── deveLancarExcecaoQuandoNaoEncontrar
 └── deveInativarTinta

MovimentacaoEstoqueService
 ├── deveRegistrarEntrada
 ├── deveRegistrarSaida
 └── naoDevePermitirSaidaMaiorQueEstoque
```

---

# 9. Observabilidade e operação

**Prioridade: média**

### Tarefas

- [ ] Adicionar Spring Boot Actuator.
- [ ] Criar endpoint de health check.
- [ ] Revisar logging.
- [ ] Separar configurações por profile.
- [ ] Criar configuração de desenvolvimento.
- [ ] Criar configuração de produção.
- [ ] Evitar `show-sql` em produção.
- [ ] Avaliar logs estruturados.

---

# 10. Docker

**Prioridade: média**

### Tarefas

- [ ] Criar `Dockerfile`.
- [ ] Criar `docker-compose.yml` para desenvolvimento.
- [ ] Subir PostgreSQL por container.
- [ ] Configurar variáveis de ambiente.
- [ ] Documentar como iniciar o ambiente.
- [ ] Testar a aplicação em ambiente limpo.

Objetivo:

```text
docker compose up
        ↓
PostgreSQL
        +
VERF API
```

---

# 11. CI/CD

**Prioridade: baixa no momento**

### Tarefas

- [ ] Criar GitHub Actions.
- [ ] Executar build automaticamente.
- [ ] Executar testes a cada push.
- [ ] Executar testes em Pull Requests.
- [ ] Impedir merge quando os testes falharem.
- [ ] Avaliar geração automática do artefato da aplicação.

A prioridade é baixa porque CI/CD será mais útil depois que existirem testes relevantes.

---

# 12. Documentação

**Prioridade: contínua**

### Tarefas

- [x] README inicial.
- [x] Roadmap técnico.
- [ ] Documentar regras de negócio importantes.
- [ ] Documentar fluxo de movimentação.
- [ ] Documentar fluxo de produção.
- [ ] Documentar autenticação quando implementada.
- [ ] Melhorar documentação OpenAPI.
- [ ] Adicionar exemplos de requests e responses.
- [ ] Adicionar diagrama ER atualizado.
- [ ] Documentar decisões arquiteturais importantes.

---

# Ordem recomendada de implementação

A ordem abaixo prioriza aquilo que melhora a qualidade do projeto sem tentar implementar tudo ao mesmo tempo.

```text
1. Regras de negócio do estoque
        ↓
2. Validação + exceções
        ↓
3. DTOs de resposta
        ↓
4. Padronização REST
        ↓
5. Testes
        ↓
6. Segurança
        ↓
7. Flyway / banco
        ↓
8. Actuator + profiles
        ↓
9. Docker
        ↓
10. CI/CD
```

## O que NÃO precisa ser feito agora

Não é necessário implementar imediatamente:

- JWT completo;
- refresh token;
- Docker;
- CI/CD;
- JaCoCo;
- MapStruct;
- observabilidade avançada;
- arquitetura complexa;
- microserviços.

Primeiro consolide:

```text
Java
 ↓
Spring Boot
 ↓
Controller
 ↓
Service
 ↓
Repository
 ↓
JPA
 ↓
PostgreSQL
 ↓
Regras de negócio
 ↓
Testes
```

Depois avance para segurança, infraestrutura e otimizações.

---

# Critério para considerar uma etapa concluída

Uma tarefa não deve ser considerada concluída apenas porque o código "funciona".

Sempre que possível, verificar:

1. A regra de negócio está correta?
2. O erro esperado retorna o status HTTP correto?
3. O banco fica consistente?
4. Existe teste para o comportamento?
5. O endpoint possui contrato claro?
6. Dados sensíveis estão protegidos?
7. A solução é compreensível para outro desenvolvedor?

Esse critério evita que o roadmap vire apenas uma lista de tecnologias adicionadas ao projeto.
