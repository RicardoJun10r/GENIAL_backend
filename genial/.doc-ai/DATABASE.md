# DATABASE.md - Estrutura de Dados e Persistência

Este documento descreve o modelo de dados do projeto **Genial Inventory SaaS**, mapeado via JPA/Hibernate para um banco de dados **PostgreSQL**. Ele detalha o estado legado atual e o roteiro arquitetural para a refatoração.

## Diagrama de Relacionamentos (ERD) e Hierarquia

O modelo baseia-se em uma arquitetura Multitenant isolada por aplicação, com uma hierarquia estrita de 1:N (Um para Muitos) de propriedade em cascata.

**`User` (Proprietário) -> `Storage` (Local) -> `Product` (Item)**

> **Regra de Isolamento (Multitenancy):** A raiz de toda consulta de domínio começa no `User`. Nenhum `Product` ou `Storage` pode ser acessado, modificado ou listado sem que o `id` do `User` autenticado faça parte da cláusula restritiva da consulta.

---

## Dicionário de Dados (Legado vs. Refatoração)

Abaixo estão as definições atuais das tabelas e as marcações de evolução arquitetural.

### 1. Entidade: `User` (Tabela Legada: `USUARIOS`)

Representa o locatário (Tenant) do SaaS. Detém as credenciais de acesso e a posse de todos os dados subsequentes.

| Coluna Atual | Tipo (JPA) | Restrições Legadas | Target da Refatoração (Compliance) |
| :--- | :--- | :--- | :--- |
| `id` | `String (UUID)` | PK, Generated | Manter UUID. Garantir geração nativa (`uuid2`). |
| `email` | `String` | Unique, Not Null | PII Sensível. Usar como chave natural de login. |
| `name` | `String` | - | PII Sensível. Ocultar de logs. |
| `password` | `String` | - | Garantir persistência via Hash (BCrypt/Argon2). |
| *(Novo)* | `Boolean` | - | Adicionar `active` para suporte a Soft Delete. |
| *(Novo)* | `String` | - | Adicionar `role` para adequação de RBAC (Security). |

* **Relacionamento:** 1:N com `Storage`. Mapeamento deve ser obrigatoriamente `FetchType.LAZY` para evitar N+1 queries.

### 2. Entidade: `Storage` (Tabela Legada: `STORAGE`)

Representa uma unidade de armazenamento lógica ou física pertencente a um usuário específico.

| Coluna Atual | Tipo (JPA) | Restrições Legadas | Target da Refatoração (Compliance) |
| :--- | :--- | :--- | :--- |
| `id` | `String (UUID)` | PK, Generated | Manter UUID. |
| `id_user` | `String (UUID)` | FK (`USUARIOS`) | Chave de Isolamento Multitenant (Obrigatória nas buscas). |
| `name` | `String` | - | Validar preenchimento (`@NotBlank`). |
| `description` | `String` | - | - |
| *(Novo)* | `LocalDateTime` | - | Campos de Auditoria (CreatedDate, LastModifiedDate). |

* **Relacionamento:** N:1 com `User`. 1:N com `Product`. 

### 3. Entidade: `Product` (Tabela Legada: `PRODUTOS`)

Representa os ativos financeiros e de inventário controlados pelo sistema.

| Coluna Atual | Tipo (JPA) | Restrições Legadas | Target da Refatoração (Compliance) |
| :--- | :--- | :--- | :--- |
| `id` | `String (UUID)` | PK, Generated | Manter UUID. |
| `id_storage` | `String (UUID)` | FK (`STORAGE`) | Pertencimento indireto ao `User`. |
| `name` | `String` | - | Validar tamanho e nulidade no DB (`nullable = false`). |
| `description` | `String` | - | - |
| `sector` | `String` | - | Refatorar para `Enum` no Java, gravando String no DB. |
| `value` | `Double` | - | **CRÍTICO:** Mudar para `BigDecimal` com precision/scale. |
| `quantidade` | `Integer` | - | Validar `Min(0)` no DB para evitar estoque negativo. |
| `date` | `LocalDate` | `@PrePersist` | Adicionar `@Column(updatable = false)`. |

---

## Roteiro de Refatoração e Compliance de Dados

Para alinhar o banco de dados legado aos padrões de governança exigidos no `CODEX.md`, as seguintes correções deverão ser aplicadas via migrações (ex: Flyway/Liquibase):

### 1. Padronização de Nomenclatura (Naming Conventions)

* **Problema:** Mistura de idiomas e pluralidade (`USUARIOS`, `STORAGE`, `PRODUTOS`).
* **Ação Exigida:** Migrar todas as tabelas para inglês no formato plural ou singular estrito, utilizando *snake_case* (padrão PostgreSQL). 
    * *Target:* `users`, `storages`, `products`.

### 2. Integridade Financeira

* **Problema:** O uso de `Double` para o campo `value` compromete a precisão de cálculos contábeis.
* **Ação Exigida:** Refatorar a coluna para `DECIMAL(19, 4)` no PostgreSQL e mapear como `java.math.BigDecimal` no JPA.

### 3. Otimização de JPA e Memória (Evitar *Memory Leaks*)

* **Problema:** Anotações Lombok `@Data`, `@ToString`, `@EqualsAndHashCode` ou `@AllArgsConstructor` em entidades causam loops infinitos em coleções Lazy e carregamento excessivo de memória.
* **Ação Exigida:** * Utilizar apenas `@Getter` e `@Setter`.
    * Implementar `equals()` e `hashCode()` baseados **exclusivamente no campo `id`**.
    * Garantir que todas as associações (como `@OneToMany` e `@ManyToOne`) estejam configuradas como `FetchType.LAZY`.

### 4. Governança, Auditoria e LGPD

* **Audit Trail:** Habilitar o `@EnableJpaAuditing` no Spring. Adicionar campos de controle de criação e modificação em todas as entidades cruciais. A data de criação (`date` no Produto) deve ser estritamente protegida com `updatable = false`.
* **Soft Delete:** Para conformidade com histórico de contabilidade, implementar a anotação `@SQLDelete(sql = "UPDATE tabela SET active = false WHERE id=?")` e `@Where(clause = "active=true")` para evitar a deleção física de dados que impactem relatórios passados.
