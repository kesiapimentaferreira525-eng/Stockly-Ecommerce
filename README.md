# Stockly Ecommerce API

API REST para operações básicas de produtos e pedidos de um e-commerce, desenvolvida com Java 17, Spring Boot e MySQL.

## Funcionalidades implementadas

- Cadastro e consulta de produtos.
- Cadastro de produtos com escolha entre as categorias disponíveis.
- Exclusão de produto somente quando o estoque está zerado e não existem vendas.
- Criação de pedidos com validação de estoque.
- Baixa do estoque e gravação do pedido na mesma transação.
- Cálculo do valor total do pedido a partir do preço do produto e da quantidade.
- Carga idempotente de produtos de demonstração no MySQL durante a inicialização.
- Tratamento de erros para produto inexistente e estoque insuficiente.
- Persistência com Spring Data JPA e geração de UUIDs para as entidades.
- CORS liberado para o frontend local em `localhost:4200` e `localhost:8080` (também aceita `127.0.0.1`).

## Tecnologias

- Java 17
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA / Hibernate
- MySQL Connector
- Lombok
- Maven

## Configuração e execução

É necessário ter Java 17 e MySQL disponíveis. Crie um banco chamado `Ecommerce` e configure as variáveis de ambiente antes de iniciar a aplicação:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/Ecommerce"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "sua-senha"
$env:STORE_PICKUP_LOCATION = "Endereço completo da loja"
.\mvnw.cmd spring-boot:run
```

Execute os comandos na mesma sessão do PowerShell. `DB_URL` pode ser omitida quando o banco estiver no endereço padrão `localhost:3306/Ecommerce`. Configure `STORE_PICKUP_LOCATION` com o endereço real da loja para mostrá-lo na confirmação de retirada. A configuração de produção está em `src/main/resources/application.properties`; o Hibernate atualiza o esquema existente ao iniciar (`spring.jpa.hibernate.ddl-auto=update`). A API fica disponível na porta `8081`.

Para executar os testes:

```powershell
.\mvnw.cmd test
```

Os testes também usam o MySQL configurado pelas variáveis `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. As operações dos testes são transacionais e revertidas ao final de cada teste; a carga inicial de demonstração continua idempotente.

## Swagger / OpenAPI

Com a API em execução, acesse a interface interativa em `http://localhost:8081/swagger-ui/index.html`. O contrato OpenAPI em JSON fica em `http://localhost:8081/v3/api-docs`.

## Endpoints

### Produtos

| Método   | Caminho          | Descrição                                 | Resposta                           |
| -------- | ---------------- | ----------------------------------------- | ---------------------------------- |
| `GET`    | `/products`      | Lista todos os produtos                   | `200 OK`                           |
| `GET`    | `/products/{id}` | Consulta produto pelo UUID                | `200 OK` ou `404 Not Found`        |
| `POST`   | `/products`      | Cadastra um produto                       | `201 Created`                      |
| `DELETE` | `/products/{id}` | Exclui produto sem estoque e sem vendas   | `204 No Content` ou `409 Conflict` |
| `GET`    | `/categories`    | Lista categorias com produtos cadastrados | `200 OK`                           |

Exemplo de cadastro (use um `categoryId` retornado por `GET /categories`):

```json
{
  "sku": "AUR-NOVO-001",
  "name": "Produto novo",
  "description": "Descrição do produto.",
  "price": 149.9,
  "stock": 20,
  "categoryId": "UUID-DA-CATEGORIA"
}
```

O cadastro valida SKU único, nome, descrição, preço positivo, estoque não negativo e categoria existente. A opção de exclusão aparece no catálogo para produtos sem estoque e sem vendas; a API também aplica essas regras e responde `409 Conflict` quando a exclusão não é permitida.

Na primeira inicialização, a aplicação cria quatro categorias e 18 produtos de demonstração se eles ainda não existirem: `Acessórios`, `Cafés Especiais`, `Cápsulas & Kits` e `Métodos`. A carga preserva os produtos e estoques existentes; produtos legados sem SKU ou descrição recebem valores para aparecer corretamente no catálogo.

### Pedidos

| Método | Caminho   | Descrição                      | Resposta                                            |
| ------ | --------- | ------------------------------ | --------------------------------------------------- |
| `POST` | `/orders` | Finaliza uma compra             | `201 Created`, `400 Bad Request` ou `404 Not Found` |

Corpo da requisição:

```json
{
  "productId": "UUID-DO-PRODUTO",
  "quantity": 2,
  "buyerName": "Maria da Silva",
  "fulfillmentType": "DELIVERY",
  "paymentMethod": "PIX",
  "deliveryAddress": {
    "postalCode": "01001-000",
    "street": "Praça da Sé",
    "number": "100",
    "complement": "Apto 12",
    "neighborhood": "Sé",
    "city": "São Paulo",
    "state": "SP"
  }
}
```

O checkout registra nome do comprador, quantidade, modalidade (`DELIVERY` ou `STORE_PICKUP`) e forma de pagamento (`PIX`, `CREDIT_CARD`, `DEBIT_CARD` ou `CASH`). Para entrega, CEP, rua, número, bairro, cidade e UF são obrigatórios; para retirada, o endereço é dispensado e o comprovante mostra o local definido por `STORE_PICKUP_LOCATION` (padrão: endereço a configurar).

Quando o estoque é suficiente, a API reduz a quantidade e grava o pedido e os dados do checkout na mesma transação. A tela então exibe a confirmação com número do pedido, comprador, itens, total, recebimento, endereço ou local de retirada e pagamento escolhido. O sistema registra a forma de pagamento, mas não processa cobranças em um gateway externo.

Se o produto não existir, a API retorna `404 Not Found`. Se os dados estiverem incompletos ou o estoque não for suficiente, retorna `400 Bad Request`. A resposta inclui número do pedido, produto, quantidade, total, estoque restante, comprador, modalidade, pagamento escolhido, endereço ou local de retirada e status.

## Estrutura do projeto

- `Controller`: endpoints REST de produtos e pedidos.
- `Service`: regras de negócio para criação de pedidos.
- `Repository`: acesso ao banco via Spring Data JPA.
- `Model`: entidades `Product`, `Category` e `Order`.
- `DTO`: formatos de entrada e saída de pedidos.
- `Exception`: exceções de negócio e tratamento global dos erros.

## Escopo atual

A API implementa consulta, cadastro e exclusão condicionada de produtos, consulta de categorias e criação de pedidos. Não há endpoints para atualizar produtos ou consultar pedidos, nem autenticação/autorização.
