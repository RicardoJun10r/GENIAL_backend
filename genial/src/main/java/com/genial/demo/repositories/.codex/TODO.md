# Plano de Refatoracao da Camada de Repositorios (/repositories)

Este documento contem o passo a passo exato que deve ser executado para refatorar as interfaces de acesso a dados (`ProductRepository.java`, `StorageRepository.java`, `UserRepository.java`), garantindo que o debito tecnico listado no `CODEX.md` desta camada seja zerado e as regras de arquitetura (Multitenancy, Soft Delete e Paginacao) sejam cumpridas.

A LLM deve processar este arquivo marcando mentalmente cada etapa como concluida ao gerar o codigo.

---

## Passo 1: Preparacao e Imports Gerais
- [ ] Garantir a importacao das classes de paginacao do Spring Data nas interfaces que retornarao listas: `org.springframework.data.domain.Page` e `org.springframework.data.domain.Pageable`.
- [ ] Garantir a importacao do `java.util.Optional` em todas as interfaces.

---

## Passo 2: Refatoracao da Interface `ProductRepository.java`
- [ ] **Remocao de Risco Critico:** Deletar o metodo `void deleteByName(String name);` para evitar perda de dados em massa.
- [ ] **Isolamento Multitenant (Busca Unica):** Adicionar o metodo `Optional<Product> findByIdAndStorageUserId(String id, String userId);` para garantir que um produto so seja retornado se pertencer a um storage do usuario solicitante.
- [ ] **Paginacao e Multitenancy (Listagem):** Adicionar o metodo `Page<Product> findAllByStorageUserId(String userId, Pageable pageable);` para buscar todos os produtos de um usuario sem estourar a memoria.

---

## Passo 3: Refatoracao da Interface `StorageRepository.java`
- [ ] **Isolamento Multitenant (Busca Unica):** Adicionar o metodo `Optional<Storage> findByIdAndUserId(String id, String userId);` para impedir a vulnerabilidade de IDOR ao consultar um local de armazenamento.
- [ ] **Paginacao e Performance (Listagem):** Adicionar o metodo `Page<Storage> findByUserId(String userId, Pageable pageable);` para substituir a busca em memoria do Service por uma consulta otimizada no banco de dados.

---

## Passo 4: Refatoracao da Interface `UserRepository.java`
- [ ] **Conformidade com Soft Delete:** Deletar o metodo `void deleteByEmail(String email);` para evitar exclusoes fisicas via JPA derivado. A inativacao passara a ser gerenciada via anotacoes na entidade e `repository.delete()`.
- [ ] Manter o metodo `Optional<User> findByEmail(String email);`.

---

## Passo 5: Inspecao Final de Qualidade (Sanity Check)
- [ ] Nenhuma das tres interfaces contem metodos customizados de Hard Delete (`deleteBy...`).
- [ ] Todos os metodos de busca de Entidades Filhas (`Product`, `Storage`) exigem a passagem do `userId` na assinatura.
- [ ] Nenhuma consulta que possa retornar multiplos resultados devolve `List<T>`. Todas utilizam `Page<T>` e exigem um parametro `Pageable`.
- [ ] As interfaces continuam limpas, sem regras de negocio ou anotacoes indevidas, contendo apenas as assinaturas dos metodos derivados ou consultas customizadas (`@Query`).