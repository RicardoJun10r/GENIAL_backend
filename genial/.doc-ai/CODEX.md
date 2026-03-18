# Contexto

Este é o backend, uma API RESTFul, de um projeto antigo da faculdades, na qual a ideia principal é ser um programa de gerenciador de estoque genérico.

Com isso, clientes de quaisquer vertentes podem utilizar do sistema para fazer a contabilidade dos produtos de suas determinadas empresas.

Em contra partida, esse sistema foi feito quando tinhamos menos experiência, portanto existe diversos erros no código, com falhas na arquitetura e principalmente na segurança.

**Objetivo:** Refatorar o código legado, corrigindo dívidas técnicas, falhas de arquitetura e vulnerabilidades de segurança.

### Workflow de Build e Validação (Maven)

O projeto gerencia dependências e o ciclo de vida de build exclusivamente via **Maven**. A IA deve assumir os seguintes comandos como padrão para testar, validar e executar o código:

**PARA RODAR OS COMANDOS DO MAVEN CERTIFIQUE-SE QUE ESTÁ DENTRO DA PASTA `genial`**

1. **Validação Estrita (O Padrão Ouro):**
   - Comando: `mvn clean verify`
   - *Quando usar:* Sempre que finalizar uma refatoração ou antes de aprovar um código. Este comando garante que o código compila, roda os testes unitários (JUnit) e passa por todas as verificações de análise estática (Checkstyle, PMD e SpotBugs).

2. **Testes Isolados (TDD):**
   - Comando: `mvn clean test`
   - *Quando usar:* Durante o ciclo de desenvolvimento contínuo (Test-Driven Development) para validar rapidamente a lógica de negócio recém-criada sem rodar a análise estática completa.

3. **Execução Local:**
   - Comando: `mvn spring-boot:run`
   - *Quando usar:* Para subir a aplicação localmente. A IA deve assumir que o banco PostgreSQL local já está rodando via Docker (ou nativo no Arch Linux) na porta 5432 antes de sugerir este comando.

4. **Gestão de Dependências:**
   - Comando: `mvn dependency:tree`
   - *Quando usar:* Se houver problemas de conflito de versões (ex: classes duplicadas ou `NoSuchMethodError`), sugira este comando para debugar a árvore de dependências.
   - *Regra de Inclusão:* Ao sugerir uma nova biblioteca, forneça sempre o bloco `<dependency>` exato e atualizado para o `pom.xml`, priorizando as versões gerenciadas pelo `spring-boot-dependencies`.

5. **Empacotamento (Deploy):**
   - Comando: `mvn clean package -DskipTests`
   - *Quando usar:* Apenas para gerar o `.jar` final executável na pasta `/target` caso o objetivo seja testar o artefato de produção (pulando testes apenas se já tiverem sido validados no `verify`).

## Tecnológias

- Maven;
- Java Spring Boot;
  - **Lombok:** Use para reduzir boilerplate (Getter/Setter/Builder).
  - **MapStruct:** Obrigatório para conversão Entidade <-> DTO.
  - **Análise Estática:** Siga rigorosamente as regras do Checkstyle, PMD e SpotBugs.
- Banco de dados Postgresql;

## Padrões de API REST e Paginação

- **Maturidade REST:** Respeite a semântica dos verbos HTTP (GET, POST, PUT, PATCH, DELETE) e retorne os Status Codes corretos (ex: `201 Created` para POST, `204 No Content` para DELETE).
- **Paginação Obrigatória:** Qualquer endpoint que retorne uma lista de recursos deve utilizar a interface `Pageable` do Spring Data. Nunca retorne `List<T>` diretamente no Controller para evitar estouro de memória (Out Of Memory).
- **Versionamento:** (Se aplicável) Assuma que os endpoints devem estar sob um path de versão, como `/api/v1/...`.

## Estratégia de Testes (Testing Stack)

- **Frameworks:** Use exclusivamente **JUnit 5 (Jupiter)**, **Mockito** e **AssertJ** para asserções fluentes.
- **Isolamento de Camadas:** - Para `Services`: Testes unitários puros com `@ExtendWith(MockitoExtension.class)`. Mocke os Repositories.
  - Para `Controllers`: Use `@WebMvcTest` para validar rotas, status HTTP e validações de DTO (`@Valid`), sem carregar o contexto inteiro do Spring.
- **Padrão de Nomenclatura:** Use o padrão `should_DoSomething_When_SomeCondition` para o nome dos métodos de teste, ou o padrão `Given_When_Then` (BDD) internamente no código do teste.

## Diretrizes de Refatoração

1. **Segurança:** Valide permissões em nível de método. Proteja contra SQL Injection e Insecure Direct Object References (IDOR).
2. **Arquitetura:** Desacople a lógica de negócio dos Controllers. Mova validações para a camada de Service ou Domain.
3. **DTOs:** Nunca exponha a estrutura do banco de dados na API.
4. **Lógica Legada:** Ao sugerir mudanças, explique o risco de quebra e sugira um teste unitário para validar o comportamento atual antes da alteração.

## Diretrizes de Codificação

*Siga* os seguintes príncipios na geração de código:

- **Clean Code:** Nomes semânticos, funções pequenas e ausência de 'Magic Numbers'.
- **SOLID:** - (S) Uma classe, uma responsabilidade. 
    - (O) Aberto para extensão, fechado para modificação.
    - (D) Injeção de dependência via construtor (Spring best practices).
- **Lei de Demeter:** Evite 'chains' de getters. O código deve interagir apenas com dependências diretas.
- **DRY (Don't Repeat Yourself):** Abstraia lógica repetitiva em componentes reutilizáveis ou métodos privados.

*Siga* as seguintes preferências de Sintaxe:

- **Estrutura:** Use **Early Returns** e evite o uso de `else` após um `return` ou `throw`.
- **Naming:** - Classes: `PascalCase`.
    - Métodos e Variáveis: `camelCase`.
    - Booleans: Prefixos semânticos (`is`, `has`, `can`, `should`).
- **Java Moderno:** - Use **Streams API** para manipulação de coleções.
    - Use **Records** para transportadores de dados (DTOs).
    - Prefira **Optional** para retornos que podem ser vazios.
- **Spring/DI:** Injeção de dependência sempre via **construtor**.
- **Imutabilidade:** Parâmetros de métodos e variáveis locais devem ser `final` por padrão.

*Siga* as seguintes preferências de tratamento de erros:

- **Fail-Fast:** Valide argumentos e estado do objeto no início de cada método de serviço.
- **Exceptions:** Use exclusivamente `Unchecked Exceptions` customizadas (ex: `BusinessException`).
- **Global Handler:** Centralize o tratamento em um `@ControllerAdvice`.
- **Concurrency:** Trate `OptimisticLockException` retornando HTTP 409.
- **Payload:** Siga o padrão RFC 7807 para respostas de erro.
- **Segurança:** Nunca exponha Stack Traces em ambiente de produção.

## Fluxo de Trabalho e Resolução de Problemas

1. **Diagnóstico:** Antes de codar, explique brevemente o "Code Smell" detectado no trecho legado.
2. **Alternativas:** Proponha a solução ideal (SOLID/Clean Code Architecture) e a solução mínima necessária.
3. **Segurança:** Filtre toda sugestão pelo checklist OWASP. Garanta que IDs de clientes sejam validados contra o contexto do usuário autenticado.
4. **Testabilidade:** Código sem teste não é código refatorado. Sempre forneça o esqueleto do teste unitário correspondente.
5. **Ferramentas:** O código deve passar "liso" pelo PMD e SpotBugs. Evite variáveis não utilizadas ou retornos nulos sem Optional.
6. **Foco em Performance:** Como é um sistema de inventário/contabilidade, evite operações $O(n^2)$ em coleções e prefira consultas paginadas no banco de dados.

## Conhecimento Específico (Projetos Atuais)

*Projeto:* Genial Inventory SaaS (Legacy Refactor)

*Descrição:* Backend de um sistema de gerenciamento de estoque genérico e multitenant, focado na transição de um código acadêmico para uma arquitetura robusta e segura.

*Objetivo Atual:* Refatorar as entidades User, Storage e Product, implementando DTOs com MapStruct e garantindo que um usuário só consiga visualizar/editar seus próprios Storages e Products.

## Regras de Negócio:

*Hierarquia de Posse:* Um Product pertence obrigatoriamente a um Storage, e um Storage pertence obrigatoriamente a um User. Nenhuma operação deve permitir que um Product "troque" de dono (User) indiretamente.

*Imutabilidade de Histórico:* O campo date em Product (preenchido via @PrePersist) nunca deve ser alterado após a criação, pois representa a data de entrada no estoque.

*Unicidade de Identificação:* Embora o banco use UUID como String, o email do User é a chave natural única para autenticação.

*Cálculos Financeiros:* O campo value em Product representa o custo unitário. Cálculos de valor total de estoque devem ser feitos via BigDecimal no Service, nunca no Controller ou via Double (evitar erros de precisão).

*Soft Delete (Futuro):* Planejamos não deletar registros fisicamente. Considere sempre a inclusão de um campo active ou deleted_at em refatorações futuras.

## Padrões de Arquitetura e Camadas

O projeto adota uma arquitetura em camadas estrita. A comunicação entre camadas deve ser unidirecional (Controller $\rightarrow$ Service $\rightarrow$ Repository).

- `config`: Apenas configurações de infraestrutura (Beans, Security, Swagger, etc.).
- `controller`: Exclusivo para orquestração HTTP (Entrada/Saída) e validação de DTOs (@Valid). *Regra:* Nunca injete Repository aqui.
- `services`: O coração do sistema. Onde reside a lógica de negócio pura, validações de domínio e cálculos.
- `repositories`: Apenas acesso a dados via Spring Data JPA. Nenhuma regra de negócio deve vazar para cá.
- `model`: Classes de entidade JPA (@Entity). *Regra:* Entidades são estritamente privadas à camada de serviço e banco de dados.
- `shared`: Recursos transversais, com foco na conversão exclusiva entre Entity e DTO.
  - Mapeamento: O `MapStruct` deve ser configurado na interface com `@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)` para garantir que nenhum campo seja esquecido na conversão.

## Segurança Multitenant e Proteção IDOR

Como o sistema é um SaaS, o isolamento de dados entre clientes é crítico. Confiança zero nos inputs do usuário.

- Ownership Validation (Posse): Toda operação de leitura, atualização ou deleção de um recurso (Storage ou Product) deve obrigatoriamente validar a posse na consulta ao banco.
  - Exemplo: Extraia o userId do SecurityContext e use findByIdAndUserId(id, authenticatedUserId) no Repository. Nunca use apenas findById(id).

- IDOR Protection: O sistema nunca deve confirmar a existência de um recurso de terceiros. Se a busca por ID + userId falhar, retorne imediatamente 404 Not Found ou 403 Forbidden.

## Integridade de Dados e Financeira

- Precisão Contábil: Substitua qualquer uso de `Double` ou `Float` por `BigDecimal` para o campo value (e afins). Isso evita falhas de arredondamento inerentes ao ponto flutuante (ex: $0.1 + 0.2 \neq 0.3$).
- Imutabilidade de Auditoria: O campo `date` em `Product` (e similares) marca a criação do registro. Ele deve ser anotado com `@Column(updatable = false)` e preenchido via `@PrePersist` para garantir que nem a API consiga adulterar o histórico.

## Observabilidade, Logs e Qualidade Estática

1. Rastreabilidade (Audit Trail)
Erros silenciosos ou genéricos são proibidos. Use exclusivamente `@Slf4j` do Lombok.

- Nível WARN: Para violações de regra de negócio (ex: estoque insuficiente, BusinessException).

- Nível ERROR: Para exceções inesperadas do sistema (ex: banco fora, erro de MapStruct). Inclua o Stack Trace.

- *Formato Obrigatório:* O log deve fornecer contexto completo.
  - Padrão: "Falha na operação [AÇÃO] para o Usuário [ID]. Recurso: [TIPO] [ID]. Motivo: [MENSAGEM]"

2. Política de Zero Warnings (PMD / SpotBugs)
A IA deve atuar como uma ferramenta de análise estática prévia.

- Priorize a conformidade com as regras de Checkstyle, PMD e SpotBugs acima da brevidade do código.

- *Autocorreção:* Se a sugestão inicial violar regras conhecidas (ex: `AvoidFieldInjection` exigindo construtores, `UnusedPrivateField`, variáveis locais não-finais), a IA deve corrigir o código internamente antes de apresentá-lo.

## Governança de TI e Compliance (LGPD e Auditoria)

O código gerado deve estar em conformidade com as práticas de Governança Corporativa de TI e leis de proteção de dados (LGPD/GDPR), seguindo o princípio de *Privacy by Design*.

1. **Privacidade e Proteção de Dados (PII):**
   - **Mascaramento:** Dados sensíveis (como CPF, telefone ou e-mails pessoais) não devem ser expostos em logs de erro em nenhuma hipótese.
   - **Exposição de DTOs:** Ao retornar dados de usuários, utilize anotações como `@JsonIgnore` ou crie DTOs específicos de visualização para garantir que informações sensíveis nunca "vazem" acidentalmente nas respostas da API.

2. **Auditoria Contínua (Audit Trail Corporativo):**
   - **Spring Data Auditing:** Sempre que criar ou refatorar entidades centrais, implemente a auditoria nativa do Spring. 
   - Exija o uso de `@EntityListeners(AuditingEntityListener.class)` nas entidades e mapeie os campos de rastreabilidade com `@CreatedBy`, `@CreatedDate`, `@LastModifiedBy` e `@LastModifiedDate`. O sistema deve saber exatamente *quem* alterou um registro e *quando*.

3. **Controle de Acesso Baseado em Funções (RBAC - Principle of Least Privilege):**
   - Assuma que o sistema possui perfis (ex: `ADMIN`, `MANAGER`, `OPERATOR`).
   - A IA deve sempre sugerir a proteção de endpoints sensíveis (como deleção ou relatórios financeiros) usando as anotações do Spring Security, como `@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")`. Nenhuma rota destrutiva deve ficar sem validação de role.

4. **Direito ao Esquecimento e Retenção (Soft Delete):**
   - Em conformidade com as leis de proteção de dados, registros atrelados a usuários não devem ser apagados fisicamente de imediato para manter a integridade contábil, mas devem ser anonimizados ou inativados.
   - Aplique o padrão de Soft Delete utilizando a anotação `@SQLDelete` e `@Where(clause = "deleted = false")` (ou `@SQLRestriction` nas versões mais novas do Hibernate) nas entidades que exigem conformidade histórica.
