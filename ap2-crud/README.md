# SGFH - Sistema de Gestão de Farmácia Hospitalar

Projeto da disciplina **Tópicos Especiais em Programação III - Desenvolvimento Web (2026/2)**
do Bacharelado em Informática Biomédica da UFCSPA.

Esta entrega corresponde à **Atividade Prática 2**: um CRUD completo com interface gráfica.
O módulo implementado é o de **usuários e papéis de acesso**, que serve de base para o
controle de acesso do sistema de farmácia hospitalar. O login em si entra em etapa futura.

## Funcionalidades

- CRUD completo de **papéis** (criar, listar, editar, excluir)
- CRUD completo de **usuários** (criar, listar, editar, excluir)
- Um usuário sempre pertence a um papel
- Um papel em uso por algum usuário não pode ser excluído
- E-mail de usuário e nome de papel são únicos
- Senha nunca é gravada em texto puro: apenas o hash BCrypt é persistido
- Quatro papéis padrão são criados automaticamente na primeira execução

## Tecnologias

| Camada | Tecnologia |
| --- | --- |
| Frontend | Thymeleaf + htmx |
| Backend | Java 17 + Spring Boot 4.1.1 |
| Banco de dados | MongoDB 7 |
| Build | Maven (wrapper incluído) |
| Execução | Docker Compose |

## Como executar

### Opção 1: Docker Compose (recomendado)

Sobe a aplicação e o banco juntos. Requer apenas Docker instalado.

```bash
docker compose up --build
```

Acesse <http://localhost:8080>

Para parar e apagar os dados:

```bash
docker compose down -v
```

### Opção 2: Rodar local com Mongo em container

```bash
docker run -d -p 27017:27017 --name sgfh-mongo mongo:7
./mvnw spring-boot:run
```

Acesse <http://localhost:8080>

### Variáveis de ambiente

| Variável | Padrão | Descrição |
| --- | --- | --- |
| `MONGODB_URI` | `mongodb://localhost:27017/sgfh` | String de conexão do MongoDB |
| `PORT` | `8080` | Porta HTTP da aplicação |

## Banco de dados

O MongoDB cria o banco `sgfh` automaticamente na primeira execução, com duas coleções:

- `roles` - papéis de acesso
- `users` - usuários

Os quatro papéis iniciais (`ADMINISTRADOR`, `FARMACEUTICO`, `TECNICO`, `AUDITOR`) são
inseridos pela classe `SeedData` quando a coleção está vazia, então não é necessário
importar nenhum dump para começar a usar.

Um dump de exemplo está em `database/` e pode ser restaurado com:

```bash
docker exec -i sgfh-mongo mongorestore --drop --archive < database/sgfh-dump.archive
```

## Estrutura do projeto

O código segue uma separação em camadas no estilo Clean Architecture:

```
br.edu.ufcspa.sgfh
├── domain               regras e modelos puros, sem dependência de framework
│   ├── model            Role, User (records imutáveis)
│   └── exception        BusinessException, NotFoundException
├── application          casos de uso
│   ├── port.out         interfaces de saída (repositórios, hash de senha)
│   └── service          RoleService, UserService
├── infrastructure       detalhes técnicos
│   ├── persistence      documents, repositories, adapters e mappers do MongoDB
│   ├── security         implementação BCrypt do PasswordHasher
│   └── config           carga inicial de dados
└── web                  entrada HTTP
    ├── controller       RoleController, UserController
    ├── dto              forms de entrada e views de saída
    └── mapper           conversão entre DTO e domínio
```

A regra de dependência aponta sempre para dentro: `web` e `infrastructure` conhecem
`application` e `domain`, mas `domain` não conhece ninguém. A troca do MongoDB por outro
banco exigiria mudanças apenas em `infrastructure/persistence`.

## Rotas

| Método | Rota | Descrição |
| --- | --- | --- |
| GET | `/` | Redireciona para `/users` |
| GET | `/users` | Lista usuários e exibe o formulário |
| GET | `/users/{id}/edit` | Carrega um usuário no formulário |
| POST | `/users` | Cria ou atualiza um usuário |
| DELETE | `/users/{id}` | Exclui um usuário (htmx) |
| GET | `/roles` | Lista papéis e exibe o formulário |
| GET | `/roles/{id}/edit` | Carrega um papel no formulário |
| POST | `/roles` | Cria ou atualiza um papel |
| DELETE | `/roles/{id}` | Exclui um papel (htmx) |

## Autor

Cícero Luiz Wendorff
