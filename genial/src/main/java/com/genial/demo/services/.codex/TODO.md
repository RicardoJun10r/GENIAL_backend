# Plano de Refatoracao da Camada de Servicos (/services)

Este documento contem o roteiro passo a passo exato que deve ser executado para refatorar os servicos (`UserService.java`, `StorageService.java`, `ProductService.java`), garantindo que todo o debito tecnico listado no `CODEX.md` da camada seja resolvido e as premissas arquiteturais de seguranca e performance sejam cumpridas.

A LLM devera processar este arquivo atuando sequencialmente e marcando cada etapa como concluida.

---

## Passo 1: Preparacao e Padronizacao Global
- [ ] Criar ou certificar-se de que existem as excecoes nao-checadas customizadas: `ResourceNotFoundException`, `BusinessException` e `UnauthorizedAccessException`.
- [ ] Adicionar a anotacao `@Slf4j` (do Lombok) no topo de TODAS as classes de servico para habilitar logs estruturados.
- [ ] Certificar-se de que a interface `PasswordEncoder` (do Spring Security) sera injetada onde houver manipulacao de senhas.

---

## Passo 2: Refatoracao do `UserService.java`
- [ ] **Seguranca de Senhas:** No metodo `login`, substituir a comparacao em texto plano (`password.equals()`) pelo uso de `passwordEncoder.matches()`. Na criacao/atualizacao de usuario, utilizar `passwordEncoder.encode()`.
- [ ] **Vazamento de PII:** Remover qualquer ocorrencia de `System.out.println` no metodo `findByEmail` e nos demais. Se o log for realmente necessario para debugar, utilizar `log.info()` ou `log.warn()` com mascaramento adequado.
- [ ] **Tratamento de Erros (Fail-Fast):** Substituir as validacoes `if (Optional.isPresent())` e `throw new RuntimeException(...)` por `userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("..."))`.
- [ ] **Convencao Clean Code:** Renomear variaveis em `snake_case` (como `novo_usuario`) para `camelCase` (`newUsuario` ou `newUser`).
- [ ] **Performance:** Garantir que todos os metodos que realizam apenas leitura (`findByEmail`, `findByUuid`) possuam a anotacao `@Transactional(readOnly = true)`.

---

## Passo 3: Refatoracao do `StorageService.java`
- [ ] **Vulnerabilidade IDOR (Isolamento de Dados):** Atualizar as assinaturas dos metodos `update` e `delete` para receberem obrigatoriamente o identificador do usuario (ex: `userId`). A validacao de existencia/exclusao deve ocorrer utilizando metodos vinculados ao usuario no repositorio (ex: `deleteByIdAndUserId`).
- [ ] **Otimizacao de Performance (Gargalo de Memoria):** No metodo `findByName`, remover o carregamento da entidade `User` com filtragem em memoria via `.stream().filter(...)`. Substituir por uma chamada direta ao banco: `storageRepository.findByNameAndUserEmail(name, email)`.
- [ ] **Tratamento de Erros (Fail-Fast):** Erradicar os lancamentos de `RuntimeException`. Usar variaveis locais com `.orElseThrow()` para lancar `ResourceNotFoundException` ou `UnauthorizedAccessException`.
- [ ] **Redundancia de Salvar:** Verificar os metodos de adicao de relacionamentos e garantir que o `.save()` so seja chamado para a entidade principal correta, sem updates redundantes na lista do usuario.
- [ ] **Performance:** Adicionar `@Transactional(readOnly = true)` em consultas como `findById` e `findByName`.

---

## Passo 4: Refatoracao do `ProductService.java`
- [ ] **Vulnerabilidade IDOR (Isolamento de Dados):** Assim como no `Storage`, blindar os metodos `update` e `delete` modificando a assinatura para requerer o `userId`. Utilizar metodos no repositorio que validem se o `Product` pertence a um `Storage` daquele usuario (ex: `deleteByIdAndStorageUserId`).
- [ ] **Persistencia Redundante:** No metodo `addProductOnStorage`, instanciar o produto, setar o relacionamento (`product.setStorage(storage)`) e chamar apenas `productRepository.save(product)`. Remover qualquer adicao manual em `storage.get().getProducts().add()` seguida de `storageRepository.save()`.
- [ ] **Clean Code e Sintaxe:** Refatorar a logica de multiplos `if` no metodo `update` para uma abordagem mais limpa. Renomear variaveis em `snake_case` como `id_storage` e `novo_produto` para `storageId` e `newProduct`.
- [ ] **Tratamento de Erros (Fail-Fast):** Substituir os blocos de if com `RuntimeException` por fluxos diretos com `.orElseThrow(() -> new ResourceNotFoundException("..."))`.
- [ ] **Performance:** Adicionar a anotacao `@Transactional(readOnly = true)` no metodo `findById`.

---

## Passo 5: Inspecao Final de Qualidade (Sanity Check)
- [ ] Nenhuma classe de servico no projeto lanca `RuntimeException` ou `Exception` de forma crua.
- [ ] Nenhum metodo de mutacao ou exclusao (`update`, `delete`) de `Product` ou `Storage` ignora a verificacao do dono do recurso (Tenant ID).
- [ ] Nenhum console (`System.out.println`) foi deixado para tras.
- [ ] Todas as transacoes exclusivas de leitura estao marcadas com `@Transactional(readOnly = true)`.