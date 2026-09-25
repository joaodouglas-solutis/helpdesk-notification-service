# Helpdesk Notification Service

Microsserviço responsável pelo armazenamento e gerenciamento das notificações produzidas pelos eventos do sistema.

## Arquitetura

A estrutura principal é:

```text
com.solutis.notificationservice
├── config
├── controller
├── dto
├── entity
├── event
├── exception
├── repository
└── service
```

O projeto separa o fluxo HTTP do fluxo assíncrono.

```text
HTTP
 ↓
Controller
 ↓
Service
 ↓
Repository
 ↓
Database
```

Enquanto os eventos seguem:

```text
RabbitMQ
 ↓
TicketEventConsumer
 ↓
Notification
 ↓
NotificationRepository
```

A estrutura do repositório mostra explicitamente essa separação entre API convencional e processamento de eventos.

## RabbitMQ

A configuração de mensageria está centralizada em `RabbitMQConfig`.

Existe uma `TopicExchange`:

```text
ticket.exchange
```

e três filas:

```text
ticket.created.queue
ticket.assigned.queue
ticket.status-changed.queue
```

Cada fila possui sua própria routing key:

```text
ticket.created
ticket.assigned
ticket.status-changed
```

As filas são declaradas como duráveis.

A configuração também registra `JacksonJsonMessageConverter`, permitindo que os eventos sejam transportados como objetos JSON em vez de mensagens manuais em formato textual.

## Eventos

Os eventos são representados por classes específicas:

```text
TicketCreatedEvent
TicketAssignedEvent
TicketStatusChangedEvent
```

Cada evento possui somente os dados necessários para representar a ocorrência.

O consumidor não precisa conhecer a entidade `Ticket` do outro microsserviço.

## Consumer

`TicketEventConsumer` utiliza listeners independentes:

```java
@RabbitListener(
    queues = "ticket.created.queue"
)
```

```java
@RabbitListener(
    queues = "ticket.assigned.queue"
)
```

```java
@RabbitListener(
    queues = "ticket.status-changed.queue"
)
```

Cada listener transforma o evento recebido em uma entidade `Notification` e persiste o resultado.

O serviço, portanto, não precisa consultar continuamente o Ticket Service para descobrir alterações. Ele recebe as ocorrências através da infraestrutura de mensageria.

## Persistência

A entidade central é `Notification`, persistida através de `NotificationRepository`.

O fluxo de criação da notificação acontece dentro do consumidor:

```text
Event
 ↓
TicketEventConsumer
 ↓
Notification.builder()
 ↓
NotificationRepository.save()
```

A notificação mantém informações relacionadas ao chamado, cliente, mensagem e tipo do evento.

## Regras de acesso

`NotificationService` utiliza o objeto `Authentication` para definir o escopo dos dados retornados.

Para `ROLE_CLIENT`, a consulta é limitada ao próprio `customerId`:

```java
findByCustomerIdAndActiveTrue(customerId)
```

Para outros papéis, a consulta considera todas as notificações ativas.

A consulta individual possui uma segunda proteção: mesmo que um cliente conheça o UUID de outra notificação, o serviço verifica se o `customerId` da notificação corresponde ao usuário autenticado.

Caso contrário:

```java
throw new AccessDeniedException(...)
```

## Limpeza das notificações

A limpeza também respeita o papel.

Para clientes:

```text
deactivateByCustomerId(...)
```

Para demais usuários:

```text
deactivateAll()
```

A operação trabalha sobre o estado ativo das notificações, mantendo o registro persistido.

## Segurança

O serviço funciona como Resource Server OAuth2/JWT.

O `JwtDecoder` utiliza uma chave HMAC configurada através de:

```text
security.jwt.secret
```

O claim:

```text
role
```

é convertido para autoridades Spring através de:

```java
authoritiesConverter.setAuthoritiesClaimName("role");
authoritiesConverter.setAuthorityPrefix("ROLE_");
```

## Organização do código

O serviço possui uma separação clara entre:

```text
event/
```

para integração assíncrona,

```text
service/
```

para regras de aplicação,

```text
repository/
```

para persistência,

e

```text
controller/
```

para exposição HTTP.

Isso permite que o mecanismo de entrada por RabbitMQ permaneça desacoplado da API REST.
