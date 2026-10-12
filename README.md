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

## Como executar a AP2

```bash
cd ap2-crud
docker compose up --build
```

Acesse <http://localhost:8080>

As instruções completas, incluindo a execução sem Docker e a restauração do dump do banco,
estão em [`ap2-crud/README.md`](ap2-crud/README.md).
