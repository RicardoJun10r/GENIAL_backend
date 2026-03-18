# Camada de Repositorios (/repositories)

## Visao Geral do Modulo

A pasta `/repositories` representa a camada de Acesso a Dados (Data Access Layer) do **Genial Inventory SaaS**. Ela contem exclusivamente interfaces que estendem `JpaRepository` (ou similares do Spring Data).

O objetivo desta camada e isolar a interacao com o banco de dados PostgreSQL. Nenhuma regra de negocio, validacao de dominio ou tratamento de excecao HTTP deve existir aqui. O repositorio apenas recebe comandos de leitura ou escrita do `Service` e os executa no banco.

## Regras de Ouro da Camada (Invariaveis)

1. **Multitenancy Estrito nas Consultas:** Entidades filhas (`Storage` e `Product`) nunca devem ser consultadas apenas por seus IDs. Todas as buscas, atualizacoes e exclusoes devem incluir a chave do usuario proprietario (Tenant) para evitar vulnerabilidades de IDOR.
2. **Proibicao de Hard Deletes Customizados:** Como o sistema adota a premissa de Soft Delete (para compliance com LGPD e auditoria), metodos derivados de delecao direta (ex: `deleteBy...`) devem ser evitados. A exclusao deve ser gerenciada pelas anotacoes `@SQLDelete` na entidade e acionada via `repository.delete()`.
3. **Paginacao Obrigatoria:** Qualquer consulta que possa retornar mais de um registro deve obrigatoriamente receber um parametro `Pageable` e retornar um objeto `Page<T>`, evitando estouro de memoria.
4. **Otimizacao de N+1 (Fetch Joins):** Consultas que precisem carregar relacionamentos Lazy (como buscar um Storage e seus Products) devem usar `@Query` com `JOIN FETCH` ou `@EntityGraph`.

---

## Pontos de Melhoria e Debito Tecnico (Roadmap de Refatoracao)

Com base na analise das interfaces legadas (`UserRepository.java`, `StorageRepository.java`, `ProductRepository.java`), os seguintes pontos criticos de debito tecnico foram identificados e devem ser resolvidos:

### 1. Ausencia Absoluta de Isolamento Multitenant (`ProductRepository` e `StorageRepository`)
- **Problema:** As interfaces estao vazias ou possuem metodos genericos. Isso significa que o `Service` esta usando o `findById(id)` padrao do Spring Data, permitindo que qualquer usuario consulte o ID de um storage ou produto que nao lhe pertence.
- **Acao:** - Em `StorageRepository`: Criar `Optional<Storage> findByIdAndUserId(String id, String userId);`.
  - Em `ProductRepository`: Criar `Optional<Product> findByIdAndStorageUserId(String id, String userId);`.
  - Em `ProductRepository`: Criar metodos para listagem paginada por usuario: `Page<Product> findAllByStorageUserId(String userId, Pageable pageable);`.

### 2. Risco Critico de Perda de Dados em Massa (`ProductRepository.java`)
- **Problema:** O metodo `void deleteByName(String name);` permite deletar produtos pelo nome. Em um sistema SaaS Multitenant, usuarios diferentes podem ter produtos com o mesmo nome (ex: "Caneta Azul"). Este metodo, se chamado, deletara as canetas de TODOS os clientes do banco de dados simultaneamente.
- **Acao:** Remover este metodo imediatamente. Exclusoes devem ser feitas unicamente por `ID` e validadas contra o `userId` dono do recurso.

### 3. Violacao da Regra de Soft Delete e Auditoria (`UserRepository.java`)
- **Problema:** O metodo `void deleteByEmail(String email);` forca uma exclusao fisica via query derivada do Spring Data. Isso viola o padrao de Soft Delete estabelecido para conformidade com a LGPD e quebra a integridade referencial se o usuario ja possuir `Storages`.
- **Acao:** Remover o metodo `deleteByEmail`. A inativacao do usuario devera ser feita buscando a entidade no `Service`, alterando a flag (ex: `user.setActive(false)`) e salvando novamente, ou utilizando o metodo `delete()` padrao caso o `@SQLDelete` esteja devidamente configurado na entidade `User`.

### 4. Consultas de Alta Carga Sem Paginacao
- **Problema:** Atualmente, para buscar todos os `Storages` de um usuario, o `Service` esta puxando o `User` inteiro e usando `user.getStorages()`, o que traz todos os registros para a memoria de uma vez.
- **Acao:** Adicionar no `StorageRepository` o metodo `Page<Storage> findByUserId(String userId, Pageable pageable);` para transferir a responsabilidade de filtragem e limitacao de dados para o banco de dados (PostgreSQL).