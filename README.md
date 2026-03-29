# Chat API 
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-000000?style=for-the-badge\&logo=socket.io\&logoColor=white)


Este projeto consiste em uma infraestrutura de API REST desenvolvida com Java e Spring Boot, projetada para oferecer uma experiência de comunicação escalável e segura. Indo muito além de um CRUD convencional, a aplicação implementa um ecossistema de chat em tempo real com suporte a contatos e uma camada de segurança rigorosa.

A arquitetura foi pensada para refletir cenários reais de produção, integrando WebSockets para baixa latência e uma estratégia de banco de dados híbrida, onde o PostgreSQL garante a integridade dos usuários enquanto o MongoDB gerencia a alta fluidez das mensagens. O projeto também prioriza a resiliência do sistema através de mecanismos de Rate Limiting por IP (Bucket4j) e otimização de performance com Caffeine Cache.

Este trabalho demonstra a aplicação prática de conceitos avançados de backend, incluindo testes automatizados com JUnit 5, versionamento de banco de dados com Flyway e gestão de mídia via Cloudinary, sempre pautado pelas melhores práticas de Clean Code e segurança da informação.

## Frontend
O frontend da aplicação pode ser acessado no seguinte repositório: https://github.com/vitor3211/chat-api-frontend

---

## Funcionalidades
* Cadastro e login de usuários
* Autenticação com JWT
* Login com Google
* Verificação de email
* Recuperação de senha
* Criação de contatos
* Envio de mensagens em tempo real
* Armazenamento da foto de perfil de usuário
* Proteção com rate limiting

---
## Tecnologias utilizadas

* Java 21
* Maven 3.6.3
* Spring Boot
* WebSocket
* JWT 
* Flyway
* Cloudinary 
* Postgres
* MongoDB
* Oauth2


---


## Como executar o projeto

### Pré-requisitos

* Java 21+
* Maven 3.9+
* PostgreSQL
* MongoDB
* Uma conta na Cloudinary para armazenar imagens
* Uma conta de email para envio de mensagens

### Clonando repositório
Abra o terminal e execute:

```bash
git clone https://github.com/vitor3211/chat-api.git
```
e depois: 
```bash
cd chat-api
```
### Configurando variáveis de ambiente

Preencha os campos do arquivo `.env.example`, depois que terminar rode esse comando para transferir as credenciais do `.env.example` para o `.env` que será criado automaticamente:

```bash
cp .env.example .env
```

### Executando
Use o comando abaixo para executar os testes e compilar o projeto: 

```bash
mvn clean install
```

Logo em seguida use esse comando para rodar o projeto: 

```bash
./mvnw spring-boot:run
```

---

## Principais endpoints da API

Para usar e acessar os endpoints da aplicação você pode usar o Postman ou alguma outra tecnologia de sua preferência. Os endpoints serão listados abaixo:

### Autenticação

```
POST /auth/login
POST /auth/register
POST /auth/refresh
POST /auth/verify
POST /auth/resendEmail
POST /auth/updatepassword
PUT /auth/updatepassword/{uuid}
POST /auth/logout
```

### Usuários

```
GET /user/me
GET /user/details
```

### Chat / Salas

```
POST /room  
POST /room/name  
GET /room  
GET /room/{roomId}/messages  
DELETE /room/{contactId}  
DELETE /room/messages/{messageId}
```

### Arquivos

```

POST /files/uploadFile
```

## WebSocket
A API utiliza o protocolo STOMP over WebSockets para garantir que as mensagens sejam entregues.

Endpoint da conexão:
```
ws://localhost:8080/ws-chat
```
Fluxo de mensagens:
```
PUB  /app/sendMessage/{roomId}

SUB  /topic/chat/{roomId}
```
