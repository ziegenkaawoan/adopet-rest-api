# Post API Specs

---
# Post API Specs

## Upload Post
- **Description:** Create a new pet adoption post
- **Endpoint:** `POST /api/posts/`
- **Authorization:** Required (Bearer Token)

### Request Body:
```json
{
  "petName": "Buddy",
  "petBreed": "Golden Retriever",
  "petType": "Dog",
  "petOwnerId": 1,
  "description": "Anjing lucu yang mencari rumah baru",
  "confidenceScore": 90,
  "isAvailable": true,
  "petAge": 2
}
```

### Response Body (Success):
```json
{
  "message": "Post Successfully created",
  "postId": 101
}
```

### Response Body (Failed):
```json
{
  "message": "Failed to create a post"
}
```

---

## Get All Posts
- **Description:** Retrieve a list of all pet adoption posts
- **Endpoint:** `GET /api/posts`
- **Authorization:** Required (Bearer Token)
- **Query Parameters (Optional):**

| Parameter     | Type      | Description                                                   |
|---------------|----------|----------------------------------------------------------------|
| `page`        | `int`    | Page number for pagination (default: 1).                       |
| `size`        | `int`    | Number of posts per page (default: 10).                        |
| `petType`     | `string` | Filter posts by pet type (e.g., "Dog", "Cat").                 |
| `petBreed`    | `string` | Filter posts by pet breed (e.g., "Husky", "Persian").          |
| `isAvailable` | `boolean`| Filter posts by status (`true` = available, `false` = adopted) |

### Response Body (Success):
```json
{
  "data": [
    {
      "postId": 101,
      "petName": "Buddy",
      "petBreed": "Golden Retriever",
      "petType": "Dog",
      "petOwner": {
        "id": 4,
        "username": "vallerie",
        "email": "vallerie@gmail.com",
        "phoneNumber": "0852418146363"
      },
      "imageUrl": "https://example.com/image.jpg",
      "description": "Friendly and energetic dog looking for a home.",
      "confidenceScore": 90,
      "isAvailable": true,
      "postDate": "2025-03-30T10:00:00Z"
    },
    {
      "postId": 102,
      "petName": "Milo",
      "petBreed": "Persian",
      "petType": "Cat",
      "petOwner": {
        "id": 4,
        "username": "vallerie",
        "email": "vallerie@gmail.com",
        "phoneNumber": "0852418146363"
      },
      "imageUrl": "https://example.com/image2.jpg",
      "description": "Cute Persian cat available for adoption.",
      "confidenceScore": 85,
      "isAvailable": false,
      "postDate": "2025-03-28T15:30:00Z"
    }
  ],
  "pagination": {
    "currentPage": 1,
    "totalPages": 5,
    "totalPosts": 50
  }
}
```

### Response Body (No Post Found):
```json
{
  "message": "No Posts found"
}
```

---

## Get Post Detail
- **Description:** Retrieve the details of a specific post by its ID.
- **Endpoint:** `GET /api/posts/{postId}`
- **Authorization:** Required (Bearer Token)

### Request Parameters:

| Parameter | Type   | Required | Description                  |
|-----------|--------|----------|------------------------------|
| `postId`  | `long` | Yes      | The ID of the post to fetch  |

### Response Body (Success):
```json
{
  "postId": 1,
  "petName": "Buddy",
  "petBreed": "Golden Retriever",
  "petType": "Dog",
  "petOwner": {
    "id": 10,
    "username": "john_doe",
    "email": "john@example.com",
    "phoneNumber": "1234567890"
  },
  "confidenceScore": 90,
  "imageUrl": "https://example.com/image.jpg",
  "description": "Friendly and playful dog looking for a home.",
  "isAvailable": true,
  "postDate": "2025-03-30T10:00:00Z"
}

```

### Response Body (No Post Found):
```json
{
  "error": "Post"
}
```

---
## Get Upload History
- **Description:** Retrieve a list of pet adoption posts uploaded by a specific user.
- **Endpoint:** `GET /api/posts/history`
- **Authorization:** Required (Bearer Token)
- **Query Parameters (Optional):**

| Parameter  | Type   | Description                                   |
|------------|--------|-----------------------------------------------|
| `page`     | `int`  | Page number for pagination (default: 1).     |
| `size`     | `int`  | Number of posts per page (default: 10).      |
| `isAvailable` | `boolean` | Filter posts by availability (`true` = available, `false` = adopted). |

### Response Body (Success):
```json
{
  "data": [
    {
      "postId": 101,
      "petName": "Buddy",
      "petBreed": "Golden Retriever",
      "petType": "Dog",
      "imageUrl": "https://example.com/image.jpg",
      "description": "Friendly and energetic dog looking for a home.",
      "confidenceScore": 90,
      "isAvailable": true,
      "postDate": "2025-03-30T10:00:00Z"
    },
    {
      "postId": 102,
      "petName": "Milo",
      "petBreed": "Persian",
      "petType": "Cat",
      "imageUrl": "https://example.com/image2.jpg",
      "description": "Cute Persian cat available for adoption.",
      "confidenceScore": 85,
      "isAvailable": false,
      "postDate": "2025-03-28T15:30:00Z"
    }
  ],
  "pagination": {
    "currentPage": 1,
    "totalPages": 5,
    "totalPosts": 50
  }
}
```

---
## Get Upload History Detail

- **Description:** Retrieve the details of a specific upload history entry by its ID.
- **Endpoint:** `GET /api/history/{postId}`
- **Authorization:** Required (Bearer Token)

### Request Parameters

| Parameter | Type   | Required | Description                          |
|-----------|--------|----------|--------------------------------------|
| `postId`  | `long` | Yes      | The ID of the history entry to fetch |

### Response Body (Success)

```json
{
  "postId": 101,
  "petOwner" : {
    "ownerId": 1,
    "username": "Jonathan",
    "email": "jkamagi41@gmail.com",
    "phoneNumber": "0852131231"
  }, 
  "petName": "Buddy",
  "petBreed": "Golden Retriever",
  "petType": "Dog",
  "imageUrl": "https://example.com/image.jpg",
  "description": "Friendly and playful dog looking for a home.",
  "confidenceScore": 80,
  "uploadDate": "2025-03-30T10:00:00Z",
  "status": "Success"
}
```

### Response Body (Not Found)
```json
{
  "error": "History Not Found"
}
```

---
## Get Posts by Breed
- **Description:** Retrieve pet adoption posts filtered by breed.
- **Endpoint:** `GET /api/posts/breed/{petBreed}`
- **Authorization:** Required (Bearer Token)

### Request Parameters:

| Parameter  | Type   | Required | Description                        |
|------------|--------|----------|------------------------------------|
| `petBreed` | `string` | Yes      | The breed of the pet (e.g., "Husky", "Persian"). |

### Response Body (Success):
```json
{
  "data": [
    {
      "postId": 101,
      "petName": "Buddy",
      "petBreed": "Golden Retriever",
      "petType": "Dog",
      "petOwnerId": 1,
      "imageUrl": "https://example.com/image.jpg",
      "description": "Friendly and energetic dog looking for a home.",
      "confidenceScore": 90,
      "isAvailable": true,
      "postDate": "2025-03-30T10:00:00Z"
    }
  ]
}
```

### Response Body (No Post Found):
```json
{
  "message": "No Posts found"
}
```

---
## Get Posts by Type
- **Description:** Retrieve pet adoption posts filtered by pet type (Dog/Cat).
- **Endpoint:** `GET /api/posts/type/{petType}`
- **Authorization:** Required (Bearer Token)

### Request Parameters:

| Parameter  | Type   | Required | Description                            |
|------------|--------|----------|----------------------------------------|
| `petType`  | `string` | Yes      | The type of the pet (e.g., "Dog", "Cat"). |

### Response Body (Success):
```json
{
  "data": [
    {
      "postId": 102,
      "petName": "Milo",
      "petBreed": "Persian",
      "petType": "Cat",
      "petOwnerId": 2,
      "imageUrl": "https://example.com/image2.jpg",
      "description": "Cute Persian cat available for adoption.",
      "confidenceScore": 85,
      "isAvailable": false,
      "postDate": "2025-03-28T15:30:00Z"
    }
  ]
}
```

### Response Body (No Post Found):
```json
{
  "message": "No Posts found"
}
```

--- 

## Change Post Availability
- **Description:** Update the availability status of a pet adoption post.
- **Endpoint:** `PATCH /api/posts/{postId}/availability`
- **Authorization:** Required (Bearer Token)

### Request Parameters:

| Parameter | Type   | Required | Description                        |
|-----------|--------|----------|------------------------------------|
| `postId`  | `long` | Yes      | The ID of the post to update.     |

### Request Body:
```json
{
  "isAvailable": false
}
```

### Response Body (Success):
```json
{
  "message": "Post availability updated successfully"
}
```

### Response Body (Failed):
```json
{
  "message": "Failed to update post availability"
}
```






