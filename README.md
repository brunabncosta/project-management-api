# Project Management API

API REST desenvolvida para gerenciamento de projetos e funcionários, como parte do desafio técnico de Backend Java da Orla Digital.

A aplicação permite cadastrar funcionários, cadastrar projetos associados a um ou mais funcionários e consultar os projetos com seus respectivos funcionários.

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA / Hibernate
- PostgreSQL 17
- Flyway
- Bean Validation
- Maven
- Docker Compose
- JUnit 5
- Mockito
- Testcontainers

## Arquitetura

A aplicação segue uma separação em camadas:

```text
controller
    ↓
service
    ↓
repository
    ↓
database
```

A estrutura principal do projeto está organizada em:

```text
src/main/java/com/brunacosta/projectmanagement
├── controller
├── dto
├── entity
├── exception
├── repository
└── service
```

Os DTOs são separados das entidades de persistência para evitar o acoplamento entre o contrato da API e o modelo do banco de dados.

## Modelo de dados

Um projeto pode possuir vários funcionários e um funcionário pode participar de vários projetos.

A relação N:N é representada no banco por uma tabela associativa:

```text
project
   │
   │ N
   │
project_employee
   │
   │ N
   │
employee
```

Tabelas:

- `project`
- `employee`
- `project_employee`

A tabela `project_employee` utiliza a combinação de `project_id` e `employee_id` como chave primária.

Também foram adicionadas restrições no banco para:

- CPF único;
- e-mail único;
- salário maior ou igual a zero;
- integridade referencial entre projetos e funcionários.

O schema é versionado pelo Flyway e o Hibernate utiliza `ddl-auto: validate`, deixando a responsabilidade pela evolução do banco nas migrations.

## Executando a aplicação

### Pré-requisitos

É necessário possuir:

- Java 21
- Maven
- Docker e Docker Compose

### 1. Configurar as variáveis de ambiente

O projeto possui um arquivo `.env.example`.

Crie uma cópia chamada `.env`:

```powershell
Copy-Item .env.example .env
```

O arquivo deve possuir:

```env
POSTGRES_DB=project_management
POSTGRES_USER=project_user
POSTGRES_PASSWORD=project_password
```

> O arquivo `.env` está ignorado pelo Git e não deve ser versionado.

### 2. Subir o PostgreSQL

Na raiz do projeto:

```bash
docker compose up -d
```

Para verificar o container:

```bash
docker compose ps
```

### 3. Configurar as variáveis para a aplicação

No PowerShell:

```powershell
$env:POSTGRES_DB="project_management"
$env:POSTGRES_USER="project_user"
$env:POSTGRES_PASSWORD="project_password"
```

No Linux/macOS:

```bash
export POSTGRES_DB=project_management
export POSTGRES_USER=project_user
export POSTGRES_PASSWORD=project_password
```

### 4. Executar

```bash
mvn spring-boot:run
```

A aplicação ficará disponível em:

```text
http://localhost:8080
```

## Endpoints

### Cadastrar funcionário

```http
POST /api/v1/employees
```

Exemplo:

```json
{
  "name": "Ana Silva",
  "cpf": "12345678901",
  "email": "ana@example.com",
  "salary": 15000.00
}
```

Resposta esperada:

```http
201 Created
```

### Consultar funcionário

```http
GET /api/v1/employees/{id}
```

### Cadastrar projeto

```http
POST /api/v1/projects
```

Exemplo:

```json
{
  "name": "Project Management API",
  "employeeIds": [1, 2]
}
```

Resposta esperada:

```http
201 Created
```

### Consultar projeto

```http
GET /api/v1/projects/{id}
```

### Listar projetos

```http
GET /api/v1/projects
```

A resposta contém os projetos e seus respectivos funcionários.

## Validações e tratamento de erros

A API utiliza Bean Validation para validar os dados recebidos.

Entre as validações implementadas estão:

- nome obrigatório;
- CPF obrigatório com 11 dígitos;
- e-mail válido;
- salário obrigatório e não negativo;
- funcionários informados no cadastro do projeto devem existir.

A aplicação possui tratamento centralizado de exceções, retornando códigos HTTP adequados, incluindo:

- `400 Bad Request` para dados inválidos;
- `404 Not Found` para recursos inexistentes;
- `409 Conflict` para conflitos de dados.

## Testes

Para executar todos os testes:

```bash
mvn test
```

Os testes são divididos em dois níveis.

### Testes unitários

As regras das camadas de serviço são testadas isoladamente utilizando JUnit 5 e Mockito.

São cobertos, entre outros cenários:

- cadastro de funcionário;
- tentativa de cadastro com CPF duplicado;
- tentativa de cadastro com e-mail duplicado;
- cadastro de projeto com funcionários;
- tentativa de associação com funcionário inexistente.

### Teste de integração

O teste de integração utiliza Testcontainers para iniciar automaticamente uma instância descartável do PostgreSQL 17.

Dessa forma, os testes exercitam em conjunto:

```text
HTTP
 ↓
Controller
 ↓
Service
 ↓
Repository
 ↓
Hibernate
 ↓
Flyway
 ↓
PostgreSQL
```

O banco utilizado pelos testes é independente do banco configurado para execução da aplicação.

Por isso, com o Docker disponível, os testes podem ser executados diretamente:

```bash
mvn test
```

sem a necessidade de configurar previamente as credenciais do PostgreSQL da aplicação.

## Decisões técnicas

### PostgreSQL em vez de banco em memória

Foi utilizado PostgreSQL tanto na aplicação quanto nos testes de integração, evitando diferenças de comportamento que poderiam ocorrer ao utilizar um banco em memória.

### Flyway para versionamento do banco

A criação e evolução do schema são controladas através de migrations.

O Hibernate apenas valida se o modelo das entidades corresponde ao schema existente.

### Relacionamento entre projetos e funcionários

Como o relacionamento atualmente não possui atributos próprios, foi utilizado `@ManyToMany` no modelo JPA, mantendo uma tabela associativa explícita e normalizada no banco.

Caso futuramente a associação possua informações próprias, como função do funcionário no projeto, data de alocação ou carga horária, a tabela associativa poderá evoluir para uma entidade própria.

### BigDecimal para valores monetários

O salário é representado utilizando `BigDecimal`, evitando problemas de precisão associados a tipos de ponto flutuante.

### DTOs separados das entidades

As entidades JPA não são expostas diretamente pela API. DTOs específicos são utilizados para entrada e saída de dados.

### Carregamento dos funcionários dos projetos

As consultas de projetos utilizam `EntityGraph` para carregar os funcionários necessários junto à consulta, evitando consultas adicionais durante a montagem da resposta.


## Documentação da API

Com a aplicação em execução, a documentação interativa da API pode ser acessada através do Swagger UI:

- [Swagger UI](http://localhost:8080/swagger-ui/index.html)
- [OpenAPI JSON](http://localhost:8080/v3/api-docs)

O Swagger UI permite visualizar os contratos da API e executar requisições diretamente pela interface.

## Autor

Bruna Costa