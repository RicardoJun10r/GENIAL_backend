# TESTING.md - Estratégia e Validação de Qualidade

Este documento define os padrões, ferramentas e cenários obrigatórios para a criação de testes no projeto **Genial Inventory SaaS**. Nenhum módulo ou refatoração é considerado "concluído" sem que estas diretrizes sejam atendidas.

## Stack de Testes

- **Framework Base:** JUnit 5 (Jupiter). Proibido o uso de JUnit 4.
- **Mocks:** Mockito (uso de `@Mock`, `@InjectMocks` e `BDDMockito`).
- **Asserções:** AssertJ (uso fluente com `assertThat()`). Proibido o uso de `Assertions` nativo do JUnit (para manter consistência e legibilidade).
- **Testes Web:** `@WebMvcTest` para Controllers (isolamento do contexto do Spring).
- **Testes de Banco:** `@DataJpaTest` e Testcontainers (para validação de queries customizadas).

---

## Anatomia e Nomenclatura do Teste

Todo teste deve seguir o padrão comportamental **BDD (Behavior Driven Development)** estruturado em 3 blocos bem definidos por comentários:

1. `// Given` (Dado que): Configuração do cenário, mocks e DTOs de entrada.
2. `// When` (Quando): A execução da ação principal (a chamada do método).
3. `// Then` (Então): As asserções (`assertThat`) e verificações de interação (`verify`).

**Padrão de Nomenclatura dos Métodos:**
Deve seguir o formato `should_[Comportamento Esperado]_When_[Cenário/Condição]`.
*Exemplo:* `should_ThrowBusinessException_When_UserTriesToAccessOthersProduct()`

---

## Cobertura e Validação Obrigatória por Camadas

A validação de software neste projeto não é opcional. **TODAS as funções importantes e fluxos de negócio devem possuir testes automatizados.** Para que um módulo seja considerado validado, os testes devem cobrir as seguintes camadas isoladamente:

### 1. Camada de Controller (`@WebMvcTest`)
O objetivo aqui não é testar regra de negócio, mas sim o contrato da API, a interface HTTP e a segurança das rotas. O `Service` deve ser sempre "mockado" (`@MockBean`).
- **Blindagem de Entrada (Validação de DTOs):** Enviar JSONs malformados ou inválidos (ex: preço negativo) e validar se a API retorna `400 Bad Request` com o padrão RFC 7807, sem que a requisição alcance o Service.
- **Semântica de Status HTTP:** Garantir o retorno exato para cada ação: `201 Created` (POST), `204 No Content` (DELETE) e `200 OK` (GET/PUT).
- **Isolamento de Dados (Anti-Leak):** Validar o JSON de resposta (`jsonPath`) para atestar que apenas os campos do DTO estão visíveis. Entidades JPA nunca devem ser serializadas na resposta.
- **Segurança de Rota (RBAC):** Em endpoints protegidos (`@PreAuthorize`), simular requisições com perfis não autorizados para validar o bloqueio via `403 Forbidden`.

### 2. Camada de Service (Unitário com Mockito)
É o coração da aplicação. Requer a maior cobertura. O Spring Context **não** deve ser carregado aqui (`@ExtendWith(MockitoExtension.class)`).
- **Filosofia Fail-Fast (Caminho Triste):** Testar se o serviço lança exceções imediatas (ex: `IllegalArgumentException`) ao receber parâmetros inválidos, usando o sufixo `_When_InvalidInput`.
- **Caminho Feliz (Happy Path):** Validar se o fluxo completo ocorre corretamente e se o Repository é chamado com os dados corretos (`verify(repository).save(any())`).
- **Multitenancy e IDOR (Crítico):** Simular tentativas de acesso cruzado onde o `userId` logado não bate com o dono do recurso (`Storage`/`Product`). Garantir que o sistema lança a `BusinessException` de "Acesso Negado".
- **Regras Financeiras:** Submeter cálculos de `BigDecimal` e saldos de estoque a testes com valores de borda (zeros, negativos) para evitar erros de lógica ou arredondamento.

### 3. Camada de Repository (`@DataJpaTest`)
Não se testa métodos padrão do Spring Data (como `save()` ou `findById()`). Testa-se apenas as consultas customizadas.
- **Filtros de Posse:** Inserir dados de múltiplos usuários no banco em memória e validar se métodos como `findByIdAndUserId()` retornam exclusivamente os dados do usuário correto.
- **Soft Delete:** Garantir que buscas normais não retornem registros inativados (validação da cláusula `@Where(clause = "active=true")`).

---

## Checklist de Cenários Críticos (Módulo de Inventário)

Qualquer nova funcionalidade ou refatoração nas entidades `Product` e `Storage` deve obrigatoriamente incluir testes para os seguintes cenários:

### Módulo: Produto (`Product`)
- [ ] O sistema **impede** a criação de um produto com estoque negativo (`quantidade < 0`).
- [ ] O sistema **impede** a associação de um Produto a um `Storage` que pertence a outro Usuário.
- [ ] O campo `date` (Data de entrada) permanece intacto durante uma operação de atualização (PUT).
- [ ] O valor do produto (`value`) é salvo mantendo a escala correta (duas casas decimais no `BigDecimal`).

### Módulo: Armazenamento (`Storage`)
- [ ] O sistema retorna lista vazia caso o Usuário solicite seus `Storages` mas ainda não tenha criado nenhum (não deve retornar erro 500 ou NullPointer).
- [ ] A exclusão de um `Storage` (DELETE) realiza apenas um *Soft Delete* (marca como inativo) ou impede a exclusão se houver Produtos ativos atrelados a ele.

### Módulo: Segurança / Tenant
- [ ] Nenhum método de busca, alteração ou deleção é executado sem que o `SecurityContextHolder` (Mock) forneça um `userId` válido.
