# API de Tarefas — Spring Boot

API REST desenvolvida em Java com Spring Boot para gerenciamento de tarefas.

Este projeto foi criado para praticar conceitos de desenvolvimento Backend, arquitetura em camadas, persistência de dados, testes automatizados e versionamento com Git.

## Tecnologias utilizadas

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database
- Maven
- Lombok
- JUnit e Mockito
- Git e GitHub

## Funcionalidades

- Cadastrar tarefas
- Buscar tarefa por ID
- Atualizar tarefas
- Excluir tarefas
- Buscar tarefas por prioridade
- Validar dados de entrada
- Tratar exceções com respostas HTTP adequadas

## Arquitetura

O projeto utiliza uma organização em camadas:

- **Controller:** recebe as requisições HTTP.
- **Service:** implementa as regras de negócio.
- **Repository:** realiza o acesso aos dados.
- **DTO:** define os dados de entrada e saída da API.
- **Exception Handler:** centraliza o tratamento de erros.

## Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/tarefas` | Cadastrar uma tarefa |
| GET | `/tarefas/{id}` | Buscar tarefa por ID |
| PUT | `/tarefas/{id}` | Atualizar uma tarefa |
| DELETE | `/tarefas/{id}` | Excluir uma tarefa |
| GET | `/tarefas/prioridade/{prioridade}` | Buscar tarefas por prioridade |

## Como executar o projeto

### Pré-requisitos

- JDK compatível com a versão configurada no `pom.xml`
- IntelliJ IDEA ou outra IDE Java
- Maven configurado na IDE ou instalado no computador

### Executando a aplicação

1. Clone o repositório:

   ```bash
   git clone https://github.com/DevVitorhf/api-tarefas-spring-boot.git
   ```

2. Abra o projeto na IDE.

3. Aguarde o Maven carregar as dependências.

4. Execute a classe `AprendendospringApplication`.

5. A aplicação ficará disponível em `http://localhost:8080`, caso a porta padrão não tenha sido alterada.

### Exemplo de requisição

Para cadastrar uma tarefa, envie uma requisição:

**POST** `/tarefas`

```json
{
  "titulo": "Estudar Java",
  "prioridade": "ALTA"
}
```

Para buscar tarefas por prioridade:

**GET** `/tarefas/prioridade/ALTA`

### Banco de dados

O projeto utiliza H2 em memória para desenvolvimento e testes. Os dados armazenados nesse banco não persistem após o encerramento da aplicação.

## Testes

O projeto possui testes automatizados utilizando JUnit, Mockito e MockMvc.

Os testes verificam regras de negócio, respostas HTTP e tratamento de exceções.

## Autor

Desenvolvido por [DevVitorhf](https://github.com/DevVitorhf).