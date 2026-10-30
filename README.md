# Tópicos Especiais em Programação III - Desenvolvimento Web (2026/2)

Repositório com as entregas da disciplina.
Bacharelado em Informática Biomédica - UFCSPA
Prof. Dr. Muriel Figueredo Franco

**Aluno:** Cícero Luiz Wendorff

## Projeto da disciplina

**SGFH - Sistema de Gestão de Farmácia Hospitalar.** Sistema web para controle de estoque e
dispensação de medicamentos em farmácia hospitalar, com controle de acesso por papel,
trilha de auditoria e observabilidade.

## Entregas

| Pasta | Entrega | Conteúdo |
| --- | --- | --- |
| [`ap1/`](ap1/) | Atividade Prática 1 | Definição inicial do projeto (PDF com as respostas a, b e c) |
| [`ap2-crud/`](ap2-crud/) | Atividade Prática 2 | CRUD de usuários e papéis com interface gráfica |
| [`ap3-plataforma/`](ap3-plataforma/) | Atividade Prática 3 | Versão inicial da plataforma: login, controle de acesso por papel, usuários e papéis, integrações externas; PDF com os avanços e desafios |

## Como executar a versão atual (AP3)

```bash
cd ap3-plataforma
docker compose up --build
```

Acesse <http://localhost:8080> e entre com `admin@sgfh.local` / `admin123`.

As instruções completas, incluindo os demais usuários de demonstração, a execução sem Docker
e a restauração do dump do banco, estão em [`ap3-plataforma/README.md`](ap3-plataforma/README.md).
A AP2 continua disponível em [`ap2-crud/`](ap2-crud/), do jeito que foi entregue.
