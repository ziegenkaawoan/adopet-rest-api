# User API Specification

## Register User
- **Description:** Register a new user
- **Endpoint:** `POST /api/auth/signup`
- **Authorization:** Not Required

### Request Body:
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "phoneNumber": "string"
}
```

### Response Body:
#### Success:
```json
{
  "message": "User registered successfully"
}
```
#### Failed:
```json
{
  "error": "Username already taken"
}
```

---

## Login User
- **Description:** Authenticate user and generate an access token
- **Endpoint:** `POST /api/auth/login`
- **Authorization:** Not Required

### Request Body:
```json
{
  "username": "string",
  "password": "string"
}
```

### Response Body:

#### Success:
```json
{
  "message": "Login successful",
  "token": "jwt-token"
}
```

#### Failed:
```json
{
  "error": "Invalid username or password"
}
```
