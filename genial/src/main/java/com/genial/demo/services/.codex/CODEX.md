# Camada de Servicos (/services)

## Visao Geral do Modulo

A pasta `/services` e o coracao do Genial. Ela contem a lógica de negócio pura, a orquestração de transações e a aplicação rigorosa das regras de domínio.

Nesta arquitetura, os servicos atuam como a ponte entre os controladores (interface Web) e os repositorios (acesso a dados). Eles devem ser completamente agnosticos em relacao a infraestrutura web (nunca devem conhecer classes como `HttpServletRequest` ou `ResponseEntity`) e operam unicamente transacionando DTOs e Entidades.

## Regras de Ouro da Camada (Invariaveis)

1. **Multitenancy Estrito (Isolamento de Dados):** Nenhum metodo de busca, atualizacao ou exclusao em `StorageService` ou `ProductService` deve aceitar apenas o `id` do recurso. Eles devem obrigatoriamente receber o `userId` (extraido do contexto de seguranca) e validar a posse antes de qualquer acao.
2. **Excecoes de Negocio:** E terminantemente proibido lancar excecoes genericas (`RuntimeException`, `Exception`). O sistema deve utilizar excecoes nao-checadas customizadas (ex: `ResourceNotFoundException`, `BusinessException`, `UnauthorizedAccessException`) para que o `GlobalExceptionHandler` possa formata-las corretamente.
3. **Fail-Fast e Early Returns:** Valide os parametros de entrada nas primeiras linhas do metodo. Evite blocos `if (Optional.isPresent())` aninhados. Utilize `.orElseThrow()`.
4. **Seguranca e Privacidade:** Logicas de autenticacao e senhas devem utilizar Hash (ex: BCrypt). O uso de `System.out.println` ou o log de dados indevidos (PII) e uma quebra grave de compliance.
5. **Transacionabilidade:** Metodos de leitura devem usar `@Transactional(readOnly = true)` para otimizacao de performance no Hibernate.

---

## Pontos de Melhoria e Debito Tecnico (Roadmap de Refatoracao)

Com base na auditoria do codigo atual (`UserService.java`, `StorageService.java`, `ProductService.java`), os seguintes pontos criticos de debito tecnico foram identificados e devem ser resolvidos imediatamente:

### 1. Tratamento de Erros Generico e Ocultacao de Falhas

- **Problema:** Todos os servicos estao repletos de `throw new RuntimeException("Erro");` ou `throw new RuntimeException("Erro: Nao achado");`. Isso impede o rastreamento adequado e o tratamento global de erros.
- **Acao:** Substituir todos os `Optional.isPresent()` seguidos de `throw` ao final do metodo por `repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Mensagem clara de erro"));`.

### 2. Vulnerabilidade Critica de IDOR (Quebra de Multitenancy)

- **Problema:** Metodos como `ProductService.delete(String id)` e `StorageService.update(StorageUpdate dto)` operam apenas com o ID do recurso. Qualquer usuario autenticado pode passar o ID do produto de outro usuario e deleta-lo ou altera-lo.
- **Acao:** Alterar as assinaturas para receber o `userId` de quem esta executando a acao e delegar a exclusao/atualizacao para metodos no repositorio que validem ambas as chaves (ex: `deleteByIdAndStorageUserId`).

### 3. Falha de Seguranca em Senhas e Vazamento em Console (`UserService.java`)

- **Problema:** O metodo `login` compara senhas em texto plano (`password.equals(dto.password())`). O metodo `findByEmail` possui um `System.out.println(s.getName() + " " + s.getDescription());` para debugar dados associados, o que causa vazamento de memoria (I/O bloqueante) e violacao de LGPD em producao.
- **Acao:** Integrar `PasswordEncoder` (Spring Security) para comparar hashes no login e na atualizacao de senhas. Remover qualquer `System.out.println`. Utilizar a anotacao `@Slf4j` do Lombok e registrar eventos de erro ou negocio nos niveis `log.info` ou `log.warn`.

### 4. Violacao de Sintaxe e Clean Code

- **Problema:** Variaveis locais foram declaradas usando snake_case, ferindo a convencao Java (ex: `novo_produto`, `id_storage`, `novo_usuario`). O metodo `ProductService.update` esta longo e repetitivo com multiplos `if`.
- **Acao:** Padronizar nomes para `camelCase` (ex: `newProduct`, `storageId`). 

### 5. Baixa Performance em Buscas (`StorageService.java`)

- **Problema:** O metodo `findByName` carrega a entidade `User`, extrai todas as listas de `Storage` para a memoria do servidor e faz um `.stream().filter(...).findFirst().get()`. Isso causara estouro de memoria (`OutOfMemoryError`) se o usuario tiver milhares de storages.
- **Acao:** Criar uma consulta direta no `StorageRepository` (ex: `findByNameAndUserEmail(String name, String email)`) para que o banco de dados faca o filtro usando SQL, sem carregar listas para a memoria.

### 6. Atualizacoes e Persistencia Redundante

- **Problema:** Em metodos como `addProductOnStorage`, o codigo salva o produto, adiciona na lista do `storage`, e depois chama `storageRepository.save(storage.get())`. Com o JPA bem configurado, isso e redundante e gera queries desnecessarias de UPDATE.
- **Acao:** Apenas instanciar o produto, setar a relacao `product.setStorage(storage)` e salvar o produto. A associacao bidirecional e o gerenciamento de persistencia cuidam do resto sozinhos.