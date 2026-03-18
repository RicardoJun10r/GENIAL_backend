# Camada de Dominio e Persistencia (/model)

## Visao Geral do Modulo

A pasta `/model` (ou camada de Dominio/Entidade) e o nucleo estrutural do **Genial Inventory SaaS**. Ela contem exclusivamente as classes anotadas com `@Entity` que mapeiam o modelo orientado a objetos para o banco de dados relacional PostgreSQL via JPA/Hibernate.

Nesta arquitetura, as entidades sao a representacao do estado da aplicacao e do modelo relacional, nao devendo conter regras de negocio complexas ou manipulacao de infraestrutura.

## Regras de Ouro da Camada (Invariaveis)

1. **Zero Logica de Negocio:** Entidades nao fazem calculos financeiros complexos, nao chamam repositorios e nao disparam eventos externos. Elas apenas representam o estado e garantem a consistencia interna dos seus dados (ex: `@PrePersist`).
2. **Isolamento Absoluto da API:** Nenhuma classe desta pasta pode ser serializada ou retornada diretamente em um `Controller`. Elas devem ser obrigatoriamente mapeadas para DTOs via MapStruct na camada de `Service`.
3. **Identidade e Igualdade:** Os metodos `equals()` e `hashCode()` devem ser sobrescritos manualmente utilizando exclusivamente o campo `id` (UUID). O uso de `@EqualsAndHashCode` do Lombok e estritamente proibido para entidades JPA, pois causa problemas de performance e recursividade em colecoes Lazy.
4. **Relacionamentos Lazy:** Todo mapeamento `@OneToMany` ou `@ManyToOne` deve utilizar `fetch = FetchType.LAZY`.
5. **Auditoria Integrada:** Campos que representam datas de criacao (como o `date` no Produto) devem ser blindados contra atualizacoes pela API.

## Hierarquia de Dominio (Multitenancy)

O dominio atual segue uma hierarquia de posse estrita (1:N):
**User (Proprietario) -> Storage (Local) -> Product (Item)**

Qualquer operacao transversal de busca deve respeitar essa cadeia, garantindo que a raiz de isolamento seja o `User`.

---

## Pontos de Melhoria e Debito Tecnico (Roadmap de Refatoracao)

Com base na analise das classes legadas (`User.java`, `Storage.java`, `Product.java`), os seguintes pontos de débito técnico foram identificados e devem ser resolvidos na proxima refatoracao:

### 1. Padronizacao de Nomenclatura (Naming Conventions)
- **Problema:** As anotacoes `@Table` misturam idiomas e padroes (ex: `"PRODUTOS"` em portugues, `"STORAGE"` em ingles, `"USUARIOS"` em portugues).
- **Acao:** Refatorar todas as nomenclaturas de tabelas para o padrao ingles, plural e minuculo (padrão *snake_case* do PostgreSQL).
  - Em `Product`: Mudar para `@Table(name = "products")`
  - Em `Storage`: Mudar para `@Table(name = "storages")`
  - Em `User`: Mudar para `@Table(name = "users")`

### 2. Integridade de Dados Financeiros (`Product.java`)
- **Problema:** A classe utiliza `private Double value;`. O uso de ponto flutuante em campos financeiros causa erros de arredondamento criticos em softwares de contabilidade.
- **Acao:** Alterar a tipagem para `java.math.BigDecimal` e adicionar a anotacao de precisao (ex: `@Column(precision = 19, scale = 4)`).

### 3. Blindagem de Historico e Auditoria (`Product.java`)
- **Problema:** O campo `date` preenchido no `@PrePersist` nao possui protecao contra updates.
- **Acao:** Adicionar a anotacao `@Column(updatable = false)` no campo `date` para garantir a imutabilidade do registro de entrada.

### 4. Implementacao de Identidade JPA (`User`, `Storage`, `Product`)
- **Problema:** Nenhuma entidade possui `equals()` e `hashCode()`. O uso padrao do Java ira comparar os espacos de memoria, causando falhas em validacoes de colecoes (`Set` e `List`).
- **Acao:** Implementar manualmente `equals()` e `hashCode()` comparando unicamente o `id`.

### 5. Validacoes de Banco e Constraints
- **Problema:** Os atributos (como `name` em todas as classes, `quantidade` no Produto, etc) permitem nulos e valores irreais em nivel de banco de dados.
- **Acao:** Adicionar anotacoes de validacao do Jakarta Bean Validation (`@NotBlank`, `@NotNull`, `@Min(0)` para quantidade) e refletir essas obrigatoriedades na definicao da coluna (ex: `@Column(nullable = false)`).

### 6. Preparacao para Soft Delete (LGPD)
- **Problema:** O modelo atual permite a exclusao fisica (Hard Delete), o que quebra o historico contabil de inventario e viola premissas da LGPD.
- **Acao:** Adicionar um campo `private boolean active = true;` nas classes, em conjunto com as anotacoes `@SQLDelete` e `@Where(clause = "active = true")` a nivel de classe.