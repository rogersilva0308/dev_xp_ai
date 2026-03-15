# dev-xp-ai

Aplicação exemplo em Spring Boot para gerenciamento simples de desenvolvedores.

Principais pontos:

- Projeto Java 17 usando Spring Boot
- Banco em memória H2 (já incluido nas dependências)
- Endpoints REST para criar, listar, consultar e atualizar experiência

Requisitos

- JDK 17
- Maven (ou usar o wrapper included: `mvnw` / `mvnw.cmd`)

Executando

1. Pelo wrapper (recomendado):

```powershell
.\mvnw.cmd clean package; .\mvnw.cmd spring-boot:run
```

2. Ou com um Maven instalado:

```powershell
mvn clean package; mvn spring-boot:run
```

Ao iniciar, a aplicação expõe a API em http://localhost:8080 por padrão.

Endpoints

A API principal está em `/developers` e oferece os seguintes recursos (baseado em `DevController`):

- POST /developers
  - Cria um desenvolvedor.
  - Cabeçalho obrigatório: `correlationId` com um UUID.
  - Corpo JSON (exemplo):

```json
{
  "fullName": "João da Silva",
  "email": "joao@example.com",
  "nickname": "joaos",
  "uf": "SP",
  "yearsOfExperience": 5,
  "primaryLanguage": "Java",
  "interestedInAi": true,
  "skills": ["Java", "Spring Boot", "SQL"]
}
```

  - Respostas de erro retornam JSON com `message` e `details`.

- GET /developers
  - Lista todos os desenvolvedores.
  - Query params opcionais:
    - `uf` — filtra por sigla do estado (ex.: SP)
    - `language` — filtra por linguagem primaria (case-insensitive)

- GET /developers/{id}
  - Retorna o desenvolvedor pelo `id`.

- PUT /developers/{id}/experience
  - Atualiza apenas `yearsOfExperience`.
  - Corpo JSON: `{ "yearsOfExperience": 6 }`

Validações importantes

- `fullName`: 5 a 120 caracteres
- `email`: formato simples validado por regex e é único
- `nickname`: 3 a 30 caracteres (alfanum + . _ -) e é único
- `uf`: uma das siglas brasileiras (ex.: SP, RJ, MG, ...)
- `yearsOfExperience`: 0 a 60
- `primaryLanguage`: obrigatório, até 50 caracteres
- `skills`: entre 1 e 10 itens, cada skill 2 a 30 caracteres

H2 Console

O projeto inclui a dependência do H2; caso queira abrir o console web, habilite no `application.properties` e configure as propriedades necessárias. Atualmente o arquivo `src/main/resources/application.properties` define apenas o nome da aplicação.

Observações

- Todos os endpoints esperam o header `correlationId` com um UUID (ex.: `3fa85f64-5717-4562-b3fc-2c963f66afa6`).
- O projeto já contém código para criar a tabela `tb_developer` automaticamente na inicialização.

Exemplos de curl (PowerShell)

Criar desenvolvedor:

```powershell
$body = @'{
  "fullName": "João da Silva",
  "email": "joao@example.com",
  "nickname": "joaos",
  "uf": "SP",
  "yearsOfExperience": 5,
  "primaryLanguage": "Java",
  "interestedInAi": true,
  "skills": ["Java", "Spring Boot", "SQL"]
}'@

curl -Uri http://localhost:8080/developers -Method POST -Headers @{"correlationId" = "$(New-Guid)"} -Body $body -ContentType "application/json"
```

Listar todos:

```powershell
curl -Uri "http://localhost:8080/developers" -Headers @{"correlationId" = "$(New-Guid)"}
```

Consultar por id:

```powershell
curl -Uri "http://localhost:8080/developers/1" -Headers @{"correlationId" = "$(New-Guid)"}
```

Atualizar experiência:

```powershell
$body = '{"yearsOfExperience": 6}'
curl -Uri http://localhost:8080/developers/1/experience -Method PUT -Headers @{"correlationId" = "$(New-Guid)"} -Body $body -ContentType "application/json"
```

Contribuindo

Abra um pull request com melhoria de validações, testes ou novas features.

Licença

Verifique com os mantenedores do projeto — não há uma licença definida no pom.xml.

