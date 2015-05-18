

# Endpointy od edycji profile

* `GET /profiles/me` - return whole user profile

Response format:

    {
        "weight": 34,
        "nick": "Janek",
        "avatar": "http://example.com", 
        "userUuid": "8ff077ef-7fcc-47d2-9c42-93d13d54c3e4",
        "homeLatitude": 3,
        "age": 333,
        "sex": "male",
        "homeLongitude": 4,
        "homeLongitude" : "Mazowiecka 5/6",
        "created": "2015-04-15T19:00:10.199+02:00",
        "height" : 123
    }

Response status: `OK`

* `GET /profiles/me/home` - return user home

Response format:

    {
        "latitude": 44.444,
        "longitude": 3.12312,
        "address" : "Mazowiecka"
    }


Response status: `OK` | `BadRequest` when location is not defined

* `GET /profiles/me/nick` - return user nick

Response format:

    {
        "nick": "Harry"
    }


Response status: `OK` | `BadRequest` when nick is not defined


* `GET /profiles/me/sex` - return user sex

Response format:

    {
        "sex": "male"
    }

Options: `male`|`female`

Response status: `OK` | `BadRequest` when sex is not defined

* `GET /profiles/me/age` - return user age

Response format:

    {
        "age": 11
    }


Response status: `OK` | `BadRequest` when age is not defined

* `GET /profiles/me/weight` - return user weight

Response format:

    {
        "weight": 222
    }


Response status: `OK` | `BadRequest` when weight is not defined

* `GET /profiles/me/avatar` - return user avatar

Response format:

    {
        "avatar": "http://example.com"
    }

Response status: `OK` | `BadRequest` when avatar is not defined

* `POST /profiles/me/home` - save user home

Request format:

    {

        "latitude": 44.444,
        "longitude": 3.12312,
        "address" : "Mazowiecka"
    }

Response status: OK | BadRequest


* `PUT /profiles/me` - update profile

Request format:

    {
        "weight": 34,
        "nick": "Janek",
        "avatar": "http://example.com",
        "homeLatitude": 3,
        "age": 333, 
        "sex": "male",
        "homeLongitude": 4,
        "homeLongitude" : 6,
        "homeAddress" : "Mazowiecka",
        "height" : 123
    }
    
Response status: OK | BadRequest
