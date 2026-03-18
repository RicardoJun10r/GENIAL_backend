# Camada de Controladores (/controller)

## Visao Geral do Modulo

A pasta `/controller` (ou camada Web/API) atua como o portao de entrada do **Genial Inventory SaaS**. Ela contem exclusivamente as classes anotadas com `@RestController` que lidam com a orquestracao de requisicoes e respostas HTTP.

Nesta arquitetura, os controladores sao "burros". Eles tem apenas tres responsabilidades:
1. Receber a requisicao HTTP e mapear os parametros (Path, Query, Body).
2. Validar a integridade basica dos DTOs de entrada (sintaxe, preenchimento).
3. Delegar a execucao para a camada de `Service` e empacotar o resultado no `ResponseEntity` com o Status Code adequado.

## Regras de Ouro da Camada (Invariaveis)

1. **Zero Logica de Negocio:** E estritamente proibido instanciar entidades, acessar repositorios diretamente ou realizar calculos condicionais complexos dentro dos controladores.
2. **Maturidade REST (Nivel 2+):** URLs devem conter apenas substantivos no plural, nunca verbos. A acao e definida pelo verbo HTTP (GET, POST, PUT, DELETE).
3. **Validacao de Borda:** Todo `@RequestBody` deve ser acompanhado da anotacao `@Valid` para garantir que o framework rejeite payloads malformados antes de chegarem ao `Service`.
4. **Isolamento de Entidades:** Controladores so conhecem DTOs (Records ou Classes). Nenhuma Entidade JPA deve trafegar nesta camada.

---

## Pontos de Melhoria e Debito Tecnico (Roadmap de Refatoracao)

Com base na auditoria do codigo atual (`UserController.java`, `StorageController.java`, `ProductController.java`), os seguintes pontos criticos de debito tecnico violam os padroes RESTFul e de seguranca, devendo ser refatorados imediatamente:

### 1. Violacao de Semantica REST (Verbos nas URLs e Idioma)
- **Problema:** As rotas utilizam verbos explicitos e estao em portugues (ex: `/produtos/buscar`, `/estoque/{email}/buscar`, `/usuarios/atualizar`, `/usuarios/registrar`).
- **Acao:** Padronizar os *base paths* para ingles plural e utilizar versionamento. Remover todos os verbos das rotas.
  - `ProductController`: `@RequestMapping("/api/v1/products")`
  - `StorageController`: `@RequestMapping("/api/v1/storages")`
  - `UserController`: `@RequestMapping("/api/v1/users")`
  - Uma busca por ID deve ser apenas um `GET /{id}` e uma atualizacao um `PUT /{id}`.

### 2. Uso Incorreto de HTTP Status Codes
- **Problema:** Todos os metodos (incluindo criacao e delecao) estao retornando `200 OK` (`ResponseEntity.ok()`). Retornar texto puro como "Deletado!" em um DELETE quebra contratos de API de frontend.
- **Acao:** - Metodos `@PostMapping` devem retornar `ResponseEntity.status(HttpStatus.CREATED).body(...)` (201 Created).
  - Metodos `@DeleteMapping` devem retornar `ResponseEntity.noContent().build()` (204 No Content).

### 3. Falta de Validacao de DTOs (Vulnerabilidade de Entrada)
- **Problema:** Nenhuma das operacoes que recebe um `@RequestBody` (como `ProductCreate`, `StorageUpdate`, `CreateUserRequest`) possui validacao ativa. O sistema atualmente aceita nomes vazios, precos negativos e emails invalidos.
- **Acao:** Adicionar a anotacao `@Valid` (do `jakarta.validation`) antes de cada `@RequestBody`. Exemplo: `public ResponseEntity<ProductResponse> update(@Valid @RequestBody ProductUpdate dto)`.

### 4. Exposicao de PII em URLs (Furo de Privacidade)
- **Problema:** O `UserController` e o `StorageController` aceitam e-mails diretamente nos *Path Variables* (ex: `/usuarios/{email}` e `/estoque/{email}/buscar`). E-mails sao Identificadores Pessoais (PII). Trafega-los na URL expoe o dado em logs de servidores proxy, roteadores e historico de navegadores.
- **Acao:** Substituir identificadores de URL por `ID` (UUID). Em operacoes que alteram ou buscam o proprio usuario logado, o ID sequer deve vir da URL, mas sim ser extraido diretamente do Token JWT no `SecurityContext`.

### 5. Inconsistencia no Retorno de Respostas HTTP
- **Problema:** No `UserController`, o metodo `getByEmail` retorna o `UserDto` diretamente, pulando o empacotamento do `ResponseEntity`, o que impede a manipulacao de cabecalhos (headers) e padronizacao do framework.
- **Acao:** Padronizar todos os endpoints para retornarem obrigatoriamente um objeto `ResponseEntity<T>`.