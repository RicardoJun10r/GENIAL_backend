# TODO.md - Plano de Refatoracao da Camada de Dominio (/model)

Este documento contem o passo a passo exato que deve ser executado para refatorar as classes de entidade (`Product.java`, `Storage.java`, `User.java`), garantindo que o debito tecnico listado no `CODEX.md` seja zerado e as regras de arquitetura sejam cumpridas.

A LLM deve processar este arquivo marcando mentalmente cada etapa como concluida ao gerar o codigo.

---

## Passo 1: Preparacao e Imports Gerais
- [ ] Garantir que a aplicacao possui a dependencia do Jakarta Validation (`spring-boot-starter-validation`).
- [ ] Importar nas classes as anotacoes do Hibernate (`@SQLDelete`, `@Where`) e do Jakarta Validation (`@NotBlank`, `@NotNull`, `@Min`).
- [ ] Importar anotacoes de auditoria do Spring Data (`@CreatedDate`, `@LastModifiedDate`, `@EntityListeners`).
- [ ] Remover a anotacao `@EqualsAndHashCode` do Lombok, caso exista. Manter apenas `@Getter`, `@Setter`, `@NoArgsConstructor` e `@AllArgsConstructor`.
- [ ] Adicionar a anotacao `@EntityListeners(AuditingEntityListener.class)` no topo de todas as classes de entidade para habilitar o rastreamento automatico de datas.

---

## Passo 2: Refatoracao da Entidade `Product.java`
- [ ] **Nomenclatura:** Alterar `@Table(name = "PRODUTOS")` para `@Table(name = "products")`.
- [ ] **Soft Delete:** - Adicionar o atributo `private boolean active = true;`.
  - Adicionar as anotacoes no topo da classe: `@SQLDelete(sql = "UPDATE products SET active = false WHERE id=?")` e `@Where(clause = "active = true")`.
- [ ] **Integridade Financeira:** - Mudar o atributo `Double value` para `BigDecimal value`.
  - Adicionar a anotacao `@Column(precision = 19, scale = 4)` no campo `value`.
  - Importar `java.math.BigDecimal`.
- [ ] **Auditoria Continua:** - Remover o campo `date` legado e o metodo `@PrePersist void onCreate()`.
  - Adicionar `private LocalDateTime createdAt;` com as anotacoes `@CreatedDate` e `@Column(updatable = false)`.
  - Adicionar `private LocalDateTime updatedAt;` com a anotacao `@LastModifiedDate`.
- [ ] **Validacoes (Constraints):**
  - No atributo `name`: Adicionar `@NotBlank` e `@Column(nullable = false)`.
  - No atributo `quantidade`: Adicionar `@NotNull`, `@Min(0)` e `@Column(nullable = false)`.
- [ ] **Identidade:** Implementar manualmente os metodos `equals()` e `hashCode()` comparando estritamente o atributo `id`.
- [ ] **Relacionamentos:** Garantir que o `@ManyToOne` com `Storage` possua `fetch = FetchType.LAZY`.

---

## Passo 3: Refatoracao da Entidade `Storage.java`
- [ ] **Nomenclatura:** Alterar `@Table(name = "STORAGE")` para `@Table(name = "storages")`.
- [ ] **Soft Delete:** - Adicionar o atributo `private boolean active = true;`.
  - Adicionar as anotacoes no topo da classe: `@SQLDelete(sql = "UPDATE storages SET active = false WHERE id=?")` e `@Where(clause = "active = true")`.
- [ ] **Auditoria Continua:** - Adicionar `private LocalDateTime createdAt;` com as anotacoes `@CreatedDate` e `@Column(updatable = false)`.
  - Adicionar `private LocalDateTime updatedAt;` com a anotacao `@LastModifiedDate`.
- [ ] **Validacoes (Constraints):**
  - No atributo `name`: Adicionar `@NotBlank` e `@Column(nullable = false)`.
- [ ] **Identidade:** Implementar manualmente os metodos `equals()` e `hashCode()` comparando estritamente o atributo `id`.
- [ ] **Relacionamentos:** - Garantir que o `@ManyToOne` com `User` possua `fetch = FetchType.LAZY`.
  - Garantir que o `@OneToMany` com `Product` possua `fetch = FetchType.LAZY`.

---

## Passo 4: Refatoracao da Entidade `User.java`
- [ ] **Nomenclatura:** Alterar `@Table(name = "USUARIOS")` para `@Table(name = "users")`.
- [ ] **Soft Delete:** - Adicionar o atributo `private boolean active = true;`.
  - Adicionar as anotacoes no topo da classe: `@SQLDelete(sql = "UPDATE users SET active = false WHERE id=?")` e `@Where(clause = "active = true")`.
- [ ] **Auditoria Continua:** - Adicionar `private LocalDateTime createdAt;` com as anotacoes `@CreatedDate` e `@Column(updatable = false)`.
  - Adicionar `private LocalDateTime updatedAt;` com a anotacao `@LastModifiedDate`.
- [ ] **Validacoes (Constraints):**
  - No atributo `name`: Adicionar `@NotBlank` e `@Column(nullable = false)`.
  - No atributo `email`: Adicionar `@NotBlank`, `@Email` e garantir que o `@Column(unique = true, nullable = false)` seja mantido.
  - No atributo `password`: Adicionar `@NotBlank` e `@Column(nullable = false)`.
- [ ] **Identidade:** Implementar manualmente os metodos `equals()` e `hashCode()` comparando estritamente o atributo `id`.
- [ ] **Relacionamentos:** Garantir que o `@OneToMany` com `Storage` possua `fetch = FetchType.LAZY`.

---

## Passo 5: Inspecao Final de Qualidade (Sanity Check)
- [ ] Nenhuma das tres classes possui logica de negocio em seus metodos.
- [ ] Nenhuma classe esta utilizando `Double` ou `Float` para representar valores monetarios.
- [ ] Todas as listas/colecoes inicializadas nos construtores ou em `@PrePersist` foram mantidas adequadamente.
- [ ] O padrao de nomenclatura dos campos internos (`camelCase`) e das tabelas (`snake_case`) foi respeitado rigorosamente.
- [ ] Todas as entidades possuem `@EntityListeners(AuditingEntityListener.class)` e seus respectivos campos de data de criacao e atualizacao.