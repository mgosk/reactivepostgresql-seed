# Endpoint for notes management

Notes are private for each user

#### * `GET /notes` - list of all notes belongs to logged user

Response format:

    {
        "data": [
            {
                "uuid": "6f5c652d-daca-4b13-b866-1eb12c11569b",
                "userUuid": "f0db2d5a-b812-43ac-ae4c-247da3d82135",
                "subject": "Important note",
                "content": "buy milk"
            }
        ]
    }
    
Response status:
   
    OK | BadRequest
    
#### * `GET /notes/:uuid` - returns note with :uuid

Response format:

    {
        "uuid": "6f5c652d-daca-4b13-b866-1eb12c11569b",
        "userUuid": "f0db2d5a-b812-43ac-ae4c-247da3d82135",
        "subject": "Important note",
        "content": "buy milk"
    }    

#### * `POST /notes` - create note for logged user

Request example:

    {
        "subject": "Important note",
        "content": "buy milk"
    }    
    
#### * `PATCH /notes/:uuid` - update note with :uuid

Request example:

    {
        "subject": "Important note",
        "content": "buy milk and eggs"
    }    

Response format:

    {
        "uuid": "6f5c652d-daca-4b13-b866-1eb12c11569b",
        "userUuid": "f0db2d5a-b812-43ac-ae4c-247da3d82135",
        "subject": "Important note",
        "content": "buy milk"
    }
    
#### * `DELETE /notes/:uuid` - delete note with :uuid
    
Response status:
   
    OK | BadRequest    