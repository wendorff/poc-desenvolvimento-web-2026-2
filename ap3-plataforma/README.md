# SGFH - Sistema de Gestão de Farmácia Hospitalar

Projeto da disciplina **Tópicos Especiais em Programação III - Desenvolvimento Web (2026/2)**
do Bacharelado em Informática Biomédica da UFCSPA.

Esta pasta corresponde à **Atividade Prática 3**: a versão inicial da plataforma. Ela parte do
CRUD de usuários e papéis da AP2 e acrescenta login com controle de acesso por papel, os
clientes das APIs externas previstas na AP1 e as telas de cada módulo implementado.

## Funcionalidades

- **Login** com sessão (Spring Security), logout e bloqueio de usuário inativo
- **Controle de acesso por papel (RBAC)** aplicado no servidor; o menu mostra apenas o que o
  papel do usuário pode acessar
- CRUD completo de **usuários** e **papéis** (área administrativa)
- **Integrações externas** com tela de teste:
  - **ANVISA, Bulário Eletrônico**: registro do medicamento e links das bulas
  - **RxNav (RxNorm)**: normaliza o nome digitado para o RxCUI e a grafia canônica
  - **BrasilAPI**: valida o CNPJ e traz razão social e situação cadastral do fornecedor
- Página inicial com os módulos liberados e as próximas etapas do projeto
- **Interface sem recarregar a página** (htmx): navegação, salvamento, busca de usuários enquanto
  se digita e exclusão com diálogo de confirmação, com avisos de sucesso e de erro

### Regras de negócio

- Um usuário sempre pertence a um papel; e-mail de usuário e nome de papel são únicos
- Senha nunca é gravada em texto puro: apenas o hash BCrypt é persistido
- Um papel em uso por algum usuário não pode ser excluído
- O papel `ADMINISTRADOR` não pode ser renomeado, pois é usado nas regras de acesso
- O administrador não pode excluir o próprio usuário
- CNPJ é validado pelos dígitos verificadores antes da consulta, incluindo o formato
  alfanumérico da Receita Federal
- Fornecedor só é considerado apto quando a situação cadastral é `ATIVA`

## Usuários de demonstração

Criados automaticamente na primeira execução, quando a coleção de usuários está vazia.

| E-mail | Senha | Papel |
| --- | --- | --- |
| `admin@sgfh.local` | `admin123` | ADMINISTRADOR |
| `farmaceutico@sgfh.local` | `farma123` | FARMACEUTICO |
| `tecnico@sgfh.local` | `tecnico123` | TECNICO |
| `auditor@sgfh.local` | `auditor123` | AUDITOR |

## Permissões

| Área | ADMINISTRADOR | FARMACEUTICO | TECNICO | AUDITOR |
| --- | :---: | :---: | :---: | :---: |
| Início | sim | sim | sim | sim |
| Usuários e papéis | sim | - | - | - |
| Integrações | sim | sim | - | - |

As regras ficam em `web/security/SecurityConfig`. O menu é montado consultando essas mesmas
regras, então não há duplicação entre o que a tela mostra e o que o servidor permite.

## Tecnologias

| Camada | Tecnologia |
| --- | --- |
| Frontend | Thymeleaf + htmx 2, empacotados com Vite 8 |
| Interface | CSS próprio com tokens de design; fontes IBM Plex servidas pela aplicação |
| Backend | Java 25 + Spring Boot 4.1.1 |
| Segurança | Spring Security (sessão, BCrypt, CSRF) |
| Integrações | Spring `RestClient` |
| Banco de dados | MongoDB 7 |
| Build | Maven (wrapper incluído), que também baixa o Node e roda o build do Vite |
| Execução | Docker Compose |

## Como executar

### Opção 1: Docker Compose (recomendado)

Sobe a aplicação e o banco juntos. Requer apenas Docker instalado.

```bash
docker compose up --build
```

Acesse <http://localhost:8080> e entre com um dos usuários de demonstração.

Para parar e apagar os dados:

```bash
docker compose down -v
```

### Opção 2: Rodar local com Mongo em container

Requer JDK 25. Não é preciso ter Node instalado: na primeira execução o Maven baixa um Node
próprio do projeto (pasta `node/`) e compila o frontend antes de subir a aplicação.

```bash
docker run -d -p 27017:27017 --name sgfh-mongo mongo:7
./mvnw spring-boot:run
```

### Testes

```bash
./mvnw test
```

Os testes não precisam de banco nem de internet: os serviços usam repositórios em memória e
os clientes das APIs externas são testados contra um servidor HTTP simulado. Só a primeira
execução do build usa a rede, para baixar o Node e as dependências do frontend.

### Variáveis de ambiente

| Variável | Padrão | Descrição |
| --- | --- | --- |
| `MONGODB_URI` | `mongodb://localhost:27017/sgfh` | String de conexão do MongoDB |
| `PORT` | `8080` | Porta HTTP da aplicação |

Endereços e timeouts das APIs externas ficam em `application.properties`, com o prefixo
`sgfh.integrations`.

## Banco de dados

O MongoDB cria o banco `sgfh` automaticamente na primeira execução, com duas coleções:

- `roles` - papéis de acesso
- `users` - usuários

A classe `SeedData` insere os quatro papéis iniciais e os usuários de demonstração quando as
coleções estão vazias, então não é necessário importar nenhum dump para começar a usar.

Um dump com esses dados está em `database/` e pode ser restaurado com:

```bash
docker exec -i sgfh-mongo mongorestore --drop --archive < database/sgfh-dump.archive
```

## Integrações externas

Cada API fica atrás de uma porta (interface) da camada de aplicação, e o cliente HTTP fica em
`infrastructure/integration`. Nesta versão os clientes são exercitados pela tela
**Integrações**; nas próximas etapas eles passam a preencher os cadastros de medicamentos e
fornecedores.

| API | Porta | Cliente | Uso previsto |
| --- | --- | --- | --- |
| ANVISA Bulário | `MedicineLeafletCatalog` | `AnvisaBularioClient` | Dados técnicos e bula no cadastro de medicamento |
| RxNav | `DrugTerminology` | `RxNavClient` | RxCUI para padronizar o nome do medicamento |
| BrasilAPI | `CompanyRegistry` | `BrasilApiCnpjClient` | Dados e situação cadastral do fornecedor |

Falhas de rede, timeout ou resposta de erro viram uma mensagem amigável na tela, sem derrubar
a aplicação.

**Limitação conhecida da ANVISA:** a API do Bulário fica atrás de um firewall que recusa
chamadas feitas por servidores (HTTP 403), aceitando apenas o navegador do site oficial. O
cliente foi implementado e testado contra o formato documentado da resposta, e a tela informa
o bloqueio quando ele acontece. RxNav e BrasilAPI respondem normalmente.

## Frontend

As telas são renderizadas no servidor (Thymeleaf) e o htmx troca só os pedaços que mudam, então
não há estado de tela duplicado no navegador. O código do frontend fica em `src/main/frontend` e o
Vite gera o bundle em `target/classes/static/assets`, que o Spring serve em `/assets` com o hash do
conteúdo na URL (cache longo no navegador, sem risco de arquivo desatualizado).

| Caminho | Conteúdo |
| --- | --- |
| `main.js` | Ponto de entrada: importa CSS, htmx e módulos, e liga o modo estrito do htmx |
| `js/` | Token CSRF nas requisições, avisos, diálogo de confirmação, máscara de CNPJ e dicas flutuantes |
| `css/` | Tokens de design (cores, espaços, tipografia), base, layout, componentes e páginas |
| `public/` | Arquivos copiados sem alteração (favicon) |
| `templates/fragments/` | Layout da aplicação, ícones (Lucide, SVG inline) e componentes de aviso |

Decisões de segurança e robustez da interface:

- **Content-Security-Policy** sem `unsafe-inline`: script, estilo e fonte só do próprio servidor
- htmx em modo estrito: sem `eval` e sem executar `<script>` vindo nas respostas
- O histórico não guarda cópia das páginas no navegador, já que elas podem conter dados de
  pacientes; voltar uma página recarrega do servidor
- Sessão expirada durante uma ação do htmx leva ao login pelo cabeçalho `HX-Redirect`, sem
  encaixar a tela de login dentro da página
- Exclusão sempre pede confirmação num diálogo que abre com o foco em **Cancelar**
- O botão de envio fica desabilitado durante a requisição, evitando cadastro em dobro
- A validação é sempre do servidor; o erro aparece no campo e num resumo no topo do formulário
- Contraste de cores no nível AA da WCAG, foco visível em todos os controles e link para pular
  direto ao conteúdo

Para mexer no CSS ou no JavaScript com a aplicação rodando, suba a aplicação sem o cache de
URLs dos arquivos e deixe o Vite recompilando a cada alteração (requer Node 20.19+ ou 22.12+);
basta recarregar a página para ver a mudança:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.web.resources.chain.cache=false
npm run watch   # em outro terminal
```

## Estrutura do projeto

O código segue uma separação em camadas no estilo Clean Architecture:

```
br.edu.ufcspa.sgfh
├── domain               regras e modelos puros, sem dependência de framework
│   ├── model            Role, User, Cnpj, MedicineLeaflet, DrugConcept, CompanyRegistration
│   └── exception        BusinessException, NotFoundException, ExternalServiceException
├── application          casos de uso
│   ├── port.out         interfaces de saída (repositórios, hash de senha, APIs externas)
│   └── service          RoleService, UserService, ReferenceDataService
├── infrastructure       detalhes técnicos
│   ├── persistence      documents, repositories, adapters e mappers do MongoDB
│   ├── integration      clientes HTTP da ANVISA, RxNav e BrasilAPI
│   ├── security         implementação BCrypt do PasswordHasher
│   └── config           carga inicial de dados e criação dos clientes HTTP
└── web                  entrada HTTP
    ├── controller       Home, Login, User, Role e Integration controllers
    ├── security         configuração do Spring Security e usuário autenticado
    ├── advice           menu montado conforme as permissões
    ├── dto              forms de entrada e views de saída
    └── mapper           conversão entre DTO e domínio
```

A regra de dependência aponta sempre para dentro: `web` e `infrastructure` conhecem
`application` e `domain`, mas `domain` não conhece ninguém. O Spring Security usa o
`PasswordHasher` da aplicação, então o algoritmo de hash continua isolado na infraestrutura.

## Rotas

| Método | Rota | Acesso | Descrição |
| --- | --- | --- | --- |
| GET | `/login` | público | Tela de login |
| POST | `/login` | público | Autentica (Spring Security) |
| POST | `/logout` | autenticado | Encerra a sessão |
| GET | `/` | autenticado | Página inicial |
| GET | `/users?q=` | ADMINISTRADOR | Lista usuários (com busca opcional) e exibe o formulário |
| GET | `/users/{id}/edit` | ADMINISTRADOR | Carrega um usuário no formulário |
| POST | `/users` | ADMINISTRADOR | Cria ou atualiza um usuário |
| DELETE | `/users/{id}` | ADMINISTRADOR | Exclui um usuário (htmx) |
| GET | `/roles` | ADMINISTRADOR | Lista papéis e exibe o formulário |
| GET | `/roles/{id}/edit` | ADMINISTRADOR | Carrega um papel no formulário |
| POST | `/roles` | ADMINISTRADOR | Cria ou atualiza um papel |
| DELETE | `/roles/{id}` | ADMINISTRADOR | Exclui um papel (htmx) |
| GET | `/integrations` | ADMINISTRADOR, FARMACEUTICO | Tela de integrações |
| GET | `/integrations/anvisa?productName=` | ADMINISTRADOR, FARMACEUTICO | Busca no Bulário (htmx) |
| GET | `/integrations/rxnav?drugName=` | ADMINISTRADOR, FARMACEUTICO | Normaliza no RxNorm (htmx) |
| GET | `/integrations/cnpj?cnpj=` | ADMINISTRADOR, FARMACEUTICO | Consulta CNPJ (htmx) |

## Autor

Cícero Luiz Wendorff
