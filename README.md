# Stockly Ecommerce API

API REST para catálogo de produtos e checkout de pedidos, desenvolvida com Java 17, Spring Boot e MySQL.

## Sumário

- [Tecnologias](#tecnologias)
- [Requisitos](#requisitos)
- [Configuração e execução](#configuração-e-execução)
- [Documentação da API](#documentação-da-api)
- [Endpoints](#endpoints)
- [Carga inicial do catálogo](#carga-inicial-do-catálogo)
- [Testes](#testes)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Escopo atual](#escopo-atual)

## Tecnologias

- Java 17
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA / Hibernate
- MySQL
- Lombok
- Maven

## Requisitos

- Java 17
- MySQL disponível localmente ou em outro endereço configurado
- Banco de dados `Ecommerce` criado

## Configuração e execução

Configure as variáveis de ambiente e inicie a aplicação pelo PowerShell:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/Ecommerce"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "sua-senha"
$env:STORE_PICKUP_LOCATION = "Endereço completo da loja"
.\mvnw.cmd spring-boot:run
```

Execute os comandos na mesma sessão do PowerShell. `DB_URL` pode ser omitida quando o banco estiver no endereço padrão `localhost:3306/Ecommerce`. Configure `STORE_PICKUP_LOCATION` com o endereço da loja que será exibido em pedidos para retirada.

A API fica disponível em `http://localhost:8081`. O Hibernate atualiza o esquema existente ao iniciar (`spring.jpa.hibernate.ddl-auto=update`).

O CORS permite requisições de `localhost:4200` e `localhost:8080`, incluindo os mesmos endereços via `127.0.0.1`.

## Documentação da API

- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

Os endpoints de negócio aceitam o prefixo `/api` e também os caminhos sem prefixo (`/products`, `/categories` e `/orders`). As rotas sem prefixo mantêm compatibilidade com o proxy de desenvolvimento do front, que remove `/api` antes de encaminhar as requisições.

## Endpoints

### Categorias

| Método | Caminho | Descrição | Resposta |
| --- | --- | --- | --- |
| `GET` | `/api/categories` | Lista categorias com produtos cadastrados | `200 OK` |

### Produtos

| Método | Caminho | Descrição | Resposta |
| --- | --- | --- | --- |
| `GET` | `/api/products` | Lista todos os produtos | `200 OK` |
| `GET` | `/api/products/{id}` | Consulta produto pelo UUID | `200 OK` ou `404 Not Found` |
| `POST` | `/api/products` | Cadastra um produto | `201 Created` |
| `DELETE` | `/api/products/{id}` | Exclui produto sem estoque e sem vendas | `204 No Content` ou `409 Conflict` |

Para cadastrar um produto, informe um `categoryId` retornado por `GET /api/categories`:

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

O cadastro valida SKU único, nome, descrição, preço positivo, estoque não negativo e categoria existente. Um produto só pode ser excluído quando não possui estoque nem vendas; caso contrário, a API responde `409 Conflict`.

### Pedidos

| Método | Caminho | Descrição | Resposta |
| --- | --- | --- | --- |
| `POST` | `/api/orders` | Finaliza uma compra | `201 Created`, `400 Bad Request` ou `404 Not Found` |

Exemplo de requisição para entrega:

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

O checkout aceita as modalidades `DELIVERY` e `STORE_PICKUP`, e as formas de pagamento `PIX`, `CREDIT_CARD`, `DEBIT_CARD` e `CASH`.

- Para entrega, CEP, rua, número, bairro, cidade e UF são obrigatórios.
- Para retirada, `deliveryAddress` pode ser omitido; a confirmação informa o endereço configurado em `STORE_PICKUP_LOCATION`.
- Com estoque suficiente, o pedido é gravado e o estoque é reduzido na mesma transação.
- A resposta inclui número do pedido, produto, quantidade, total, estoque restante, comprador, modalidade, pagamento, endereço ou local de retirada e status.
- A API registra a forma de pagamento, mas não processa cobranças em um gateway externo.
- Produto inexistente retorna `404 Not Found`; dados inválidos ou estoque insuficiente retornam `400 Bad Request`.

## Carga inicial do catálogo

Na primeira inicialização, a aplicação cria quatro categorias e 18 produtos de demonstração caso ainda não existam: `Acessórios`, `Cafés Especiais`, `Cápsulas & Kits` e `Métodos`.

A carga é idempotente e preserva produtos e estoques existentes. Produtos legados sem SKU ou descrição recebem valores para aparecer corretamente no catálogo.

## Testes

Execute os testes com:

```powershell
.\mvnw.cmd test
```

Os testes usam o MySQL configurado por `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. As operações de teste são transacionais e revertidas ao final de cada teste.

## Estrutura do projeto

| Diretório | Responsabilidade |
| --- | --- |
| `Controller` | Endpoints REST de categorias, produtos e pedidos |
| `Service` | Regras de negócio de produtos e pedidos |
| `Repository` | Acesso ao banco via Spring Data JPA |
| `Model` | Entidades `Product`, `Category` e `Order` |
| `DTO` | Formatos de entrada e saída da API |
| `Exception` | Exceções de negócio e tratamento global de erros |

## Escopo atual

A API implementa consulta de categorias, consulta, cadastro e exclusão condicionada de produtos e criação de pedidos. Não há endpoints para atualizar produtos ou consultar pedidos, nem autenticação ou autorização.
