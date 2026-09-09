# VERF — Sistema de Gestão de Estoque e Produção de Tintas

## Contexto do projeto

O VERF é um projeto acadêmico desenvolvido em grupo como parte da formação em Análise e Desenvolvimento de Sistemas.

O sistema tem como objetivo auxiliar no controle de estoque, fornecedores, tintas, receitas, produções e movimentações de estoque, buscando melhorar a rastreabilidade das operações e reduzir perdas relacionadas ao controle manual ou inadequado dos materiais.

O projeto está sendo desenvolvido de forma incremental. Nesta etapa, o foco principal está na construção e organização da API REST do backend. Posteriormente, será desenvolvido o frontend, que consumirá a API por meio de requisições HTTP e JSON.

A arquitetura planejada é:

```text
┌─────────────────────┐
│      Frontend       │
│   Interface Web     │
└──────────┬──────────┘
           │ HTTP / JSON
           ▼
┌─────────────────────┐
│     VERF API        │
│    Spring Boot      │
└──────────┬──────────┘
           │ JPA / Hibernate
           ▼
┌─────────────────────┐
│     PostgreSQL      │
│      Database       │
└─────────────────────┘
```

## Objetivo

O VERF busca centralizar e organizar informações relacionadas ao estoque e à produção de tintas, permitindo registrar as operações realizadas e manter um histórico das movimentações.

Entre os principais objetivos estão:

- Controle de tintas e produtos;
- Cadastro e gerenciamento de fornecedores;
- Controle de estoque;
- Registro de entradas e saídas;
- Controle de receitas e itens utilizados;
- Registro de produções;
- Rastreabilidade das movimentações;
- Registro de funcionários e usuários;
- Controle de informações financeiras;
- Registro de logs de acesso;
- Evolução futura para autenticação e autorização de usuários.

## Módulos planejados

| Módulo | Objetivo |
|---|---|
| Tintas | Cadastro e consulta das tintas |
| Fornecedores | Cadastro, consulta e inativação de fornecedores |
| Estoque | Consulta dos saldos disponíveis |
| Movimentações | Registro de entradas, saídas e ajustes |
| Receitas | Definição dos componentes utilizados na produção |
| Produções | Registro da fabricação de tintas |
| Funcionários | Gerenciamento dos funcionários |
| Usuários | Controle dos usuários do sistema |
| Financeiro | Informações financeiras relacionadas ao sistema |
| Logs de acesso | Registro das operações e acessos |

## Arquitetura do backend

O backend segue uma arquitetura em camadas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

### Controller

Responsável por receber as requisições HTTP, validar entradas simples e encaminhar as operações para a camada de serviço.

### Service

Responsável pelas regras de negócio do sistema.

É nessa camada que devem ficar decisões como:

- Verificar se uma entidade existe;
- Impedir operações inválidas;
- Validar regras relacionadas ao estoque;
- Controlar movimentações;
- Verificar relacionamentos entre entidades;
- Garantir a consistência das operações.

### Repository

Responsável pela comunicação com o banco de dados utilizando Spring Data JPA.

### Banco de dados

O PostgreSQL é utilizado para persistência das informações.

## Regra importante de estoque

O estoque não deve ser tratado simplesmente como uma tabela em que qualquer usuário altera diretamente a quantidade disponível.

A ideia do sistema é manter a rastreabilidade:

```text
Movimentação
      ↓
MovimentacaoService
      ↓
Regras de negócio
      ↓
Atualização do estoque
```

Da mesma forma, uma produção poderá futuramente gerar operações como:

```text
Produção
   ↓
Consumo de matéria-prima
   ↓
Movimentações
   ↓
Entrada do produto produzido
   ↓
Estoque
```

Isso permite identificar de onde veio cada alteração no estoque.

## Backend

O backend está sendo desenvolvido utilizando:

- Java 21;
- Spring Boot;
- Spring Web MVC;
- Spring Data JPA;
- Hibernate;
- Spring Security;
- Spring Batch;
- PostgreSQL;
- Lombok;
- Maven;
- Springdoc OpenAPI / Swagger;
- JPA.

## Frontend

O frontend será desenvolvido posteriormente e terá a responsabilidade de fornecer a interface utilizada pelos usuários do sistema.

A comunicação com o backend será realizada por meio da API REST:

```text
Frontend
   ↓
HTTP Request
   ↓
VERF REST API
   ↓
HTTP Response / JSON
```

Enquanto o frontend ainda está em desenvolvimento, a API pode ser testada utilizando ferramentas como Postman.

## Estrutura planejada do projeto

Com a evolução para uma aplicação full-stack, a organização poderá seguir uma estrutura semelhante a:

```text
VERF/
├── backend/
├── frontend/
├── docs/
├── README.md
├── ROADMAP.md
└── .gitignore
```

Atualmente, o repositório está concentrado principalmente no backend.

## Integrantes

O projeto é desenvolvido em grupo.

| Integrante | Responsabilidade |
|---|---|
| Integrante 1 | Backend / API REST |
| Integrante 2 | Frontend |
| Integrante 3 | Banco de dados / documentação |
| Integrante 4 | Testes / integração |

> Os nomes e responsabilidades podem ser atualizados conforme a divisão real das tarefas do grupo.

## Status atual

O projeto encontra-se em desenvolvimento.

Atualmente, o foco está em:

- Estruturação da API REST;
- Implementação das entidades;
- Relacionamentos JPA;
- Repositories;
- Services;
- Controllers;
- Persistência no PostgreSQL;
- Padronização das respostas da API;
- Tratamento de exceções;
- Validação das regras de negócio.

Alguns recursos, principalmente autenticação, autorização, testes automatizados e observabilidade, ainda estão em evolução.

O projeto não deve ser considerado uma aplicação pronta para produção neste momento.

## Segurança

O Spring Security já está presente no projeto, porém a configuração de autenticação e autorização ainda está em desenvolvimento.

Durante a etapa de desenvolvimento e testes de CRUD, algumas rotas podem permanecer temporariamente liberadas.

A configuração definitiva deverá contemplar:

- Autenticação;
- Autorização;
- Perfis e permissões;
- Senhas armazenadas com hash;
- Proteção dos endpoints;
- Possível utilização de JWT.

## Configuração local

As credenciais do banco de dados devem ser fornecidas por variáveis de ambiente.

Exemplo:

```env
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=sua_senha
```

O arquivo `.env` real não deve ser versionado.

Utilize o `.env.example` apenas como referência.

## Executando o projeto

### Pré-requisitos

- Java 21;
- Maven;
- PostgreSQL;
- Git;
- IDE de sua preferência.

### Banco de dados

Crie um banco PostgreSQL chamado:

```text
dbVerf
```

Configure as variáveis:

```text
DATABASE_USERNAME
DATABASE_PASSWORD
```

Depois execute o projeto pela IDE ou utilizando Maven.

## Testando a API

Durante o desenvolvimento, os endpoints podem ser testados com Postman ou Swagger/OpenAPI.

Exemplo de fluxo:

```text
Postman
   ↓ HTTP
Controller
   ↓
Service
   ↓
Repository
   ↓
PostgreSQL
```

## Documentação

O projeto possui um roadmap de evolução em:

`ROADMAP.md`

A documentação deve acompanhar a evolução do sistema e registrar decisões importantes de arquitetura, regras de negócio e melhorias futuras.

## Objetivos de aprendizado

Além de entregar o sistema acadêmico, o projeto está sendo utilizado como prática de desenvolvimento backend com Java.

Os principais conhecimentos trabalhados incluem:

- Java;
- Orientação a objetos;
- Collections;
- Exceptions;
- Optional;
- SQL;
- JPA;
- Hibernate;
- Spring Boot;
- Spring Data JPA;
- APIs REST;
- HTTP;
- DTOs;
- Validação;
- Tratamento global de exceções;
- Spring Security;
- Testes automatizados;
- Git e GitHub;
- Docker;
- Integração entre frontend e backend.

## Roadmap

A evolução planejada do projeto está documentada em `ROADMAP.md`.

Entre as próximas etapas estão:

1. Tratamento global de exceções;
2. DTOs de entrada e saída;
3. Padronização dos endpoints;
4. Implementação das regras de negócio;
5. Rastreabilidade das movimentações;
6. Auditoria de datas;
7. Segurança e autenticação;
8. Testes automatizados;
9. Documentação da API;
10. Docker;
11. CI/CD;
12. Desenvolvimento e integração do frontend.

## Projeto acadêmico e portfólio

Por ser um projeto acadêmico desenvolvido em grupo, o VERF também tem como objetivo demonstrar a aplicação prática dos conhecimentos adquiridos durante a formação.

A intenção é evoluir o sistema de forma incremental, documentando as decisões e melhorias realizadas ao longo do desenvolvimento.

O projeto também poderá ser utilizado como material de portfólio para demonstrar conhecimentos em desenvolvimento backend, APIs REST, banco de dados, arquitetura em camadas, Git/GitHub e, futuramente, integração full-stack.

## Licença

Projeto desenvolvido para fins acadêmicos e de aprendizado.
