# User API Spec

# Register User

- Endpoint : POST /api/auth/signup

Request Body :

```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "phoneNumber": "string"
}
```

Response Body (Success) : 
```json 
{
  "data": "User registered"
}
```

Response Body (Failed):
```json
{
  "data": "Username already taken"
}
```

# Login User

- Endpoint : POST /api/auth/login

Request Body : 
```json
{
  "username": "string",
  "password": "string"
}
```

Response Body (Success) :
```json
{
  "data": "Success",
  "token" : "token"
}
```

