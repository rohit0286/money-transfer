
# Money Transfer App

Application starts on http://localhost:8080 this can be configured in config.properties.

 - **Jetty** - as a server
 - **Jersey** - as a JAX-RS implementation
 - **JUnit 5** - for Unit testing
 - **Mockito** - for mocking while unit testing
 - **Rest Assured** - for API testing
 - **Java** - Java version used in 11

Application can be build via maven:
```sh
mvn clean install
```

Application may be started from standalone jar:
```sh
java -jar moneytransfer-1.0-SNAPSHOT-jar-with-dependencies.jar
```
or as a maven goal

```sh
mvn exec:java
```
Postman collection of below APIs is provided: Money-Transfer.postman_collection.json


## Account API - `/accounts`

**GET** - retrieves all accounts from database

Response:
**Status: 200 OK**
```json
[{
        "id": "9028a6b0-14b4-4ca9-badb-212a6ce6f038",
        "balance": 1000
    },
    {
        "id": "2f6d8278-54dc-4773-aa5e-60beec05f889",
        "balance": 1000
    },
    {
        "id": "eb5dc5d5-f89b-4448-9f8c-03a0b85482b2",
        "balance": 1000
    }
]
```
---
**POST** - persists new account 


**Request Body** - Account object


Sample request:
```json
{
	"balance":"999"
}
```


Sample response:
**Status: 200 OK**
```json
{
    "id": "d6bb588b-3834-4502-aff8-bd41d1a91317",
    "balance": 999
}
```
---
## Transaction API - `/transactions`

**POST** - create new transaction


**Request Body** - 


Sample request:
```json
{
"source": "f224faa3-59e1-481m6-9622-806a106a73c0",
"target": "4e3fe6af-bcf1-4a0d-b310-f827c9968b25",
"amount": 10
}
```


Sample response:


**Status: 200 OK**
```json
{
    "id": "d391b387-5673-4828-afb4-917a6dee4211",
    "source": "6908a462-74ab-4d24-92fd-a5ea8c08cac1",
    "target": "95819fa9-a7e5-4d36-92fb-5deeb4897a72",
    "amount": 10,
    "status": "PENDING",
    "reason": null,
    "rollbackSucess": false
}
```
---
**PUT** - Process Money transfer - `/transactions/{transactionId}`

Sample response:


**Status: 200 OK**
```json
{
    "id": "d391b387-5673-4828-afb4-917a6dee4211",
    "source": "6908a462-74ab-4d24-92fd-a5ea8c08cac1",
    "target": "95819fa9-a7e5-4d36-92fb-5deeb4897a72",
    "amount": 10,
    "status": "COMPLETED",
    "reason": null,
    "rollbackSucess": false
}
```
---
**GET** - retrieves all Money transfer

Response:

**Status: 200 OK**
```json
[
    {
        "id": "d391b387-5673-4828-afb4-917a6dee4211",
        "source": "6908a462-74ab-4d24-92fd-a5ea8c08cac1",
        "target": "95819fa9-a7e5-4d36-92fb-5deeb4897a72",
        "amount": 10,
        "status": "COMPLETED",
        "reason": null,
        "rollbackSucess": false
    }
]
```
