# Embedded wallet
Challenge by [**revolut.com**](https://www.revolut.com): https://drive.google.com/file/d/1Rhk07_MT5WP_5f-lF0LxkJKt5pPM8SKd/view

### Libraries/dependencies:
- JRE v.1.8
- Maven
- Jetty embedded server
- Jersey servlet container
- Jersey media json Jackson
- H2 in-memory database
- Junit
- Apache maven shade plugin;

### Application launch tips:

- Root url for REST methods: */api*
- Default port: 8080
- Jar file name: revolut-task-1.0-SNAPSHOT.jar
- Maven builds jar file at package stage. All unit tests launched during build.
No any servlet containers needed to boot an application;

### Rest api methods:

- POST request, url: */user/new* - add new user to database;
```json
{
    "name": "Donald",
    "surname": "Tramp"
}
```
- GET response, url: */user?id=1* - search user by id; 
  
```json
  {
    "id": 1,
    "name": "Vladimir",
    "surname": "Putin"
  }
  
```
- GET response, url: */bank/account?id=2* - search account by id;
```json
{
    "id": 2,
    "person": {
        "id": 2,
        "name": "Donald",
        "surname": "Tramp"
    },
    "balance": 299.5,
    "currency": "RUB"
}
```
- POST request, url: */bank/withdraw* - subtracts from existent entry specified amount;
```json
{
    "accountId": 1,
    "personId": 1,
    "amount": 600,
    "currency": "RUB"
}
```
- POST request, url: */bank/deposit* - creates new account entry or adds to existent entry specified amount; 
```json
{
    "accountId": 1,
    "personId": 1,
    "amount": 600,
    "currency": "RUB"
}
```
- POST request, url: */bank/transaction* - creates new transaction between two accounts (specified by ID)
```json
{
    "from": 2,
    "to": 1,
    "amount": 100.5,
    "currency": "RUB"
}
```
- GET request, url: */bank/all* - retrieves all accounts from database 
```json
[
    {
        "id": 1,
        "user": {
            "id": 1,
            "name": "Donald",
            "surname": "Tramp"
        },
        "balance": 700.5,
        "currency": "RUB"
    },
    {
        "id": 2,
        "user": {
            "id": 2,
            "name": "Vladimir",
            "surname": "Putin"
        },
        "balance": 1449.5,
        "currency": "RUB"
    }
]
```

- GET request, url: */bank/close?id=1* - delete account from database by id 
___

_by Andrey Zakhryamin,_
 
 andrey.zakhryamin@gmail.com
