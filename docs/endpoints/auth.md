# Endpointy od autentykacji

Proces autentykcji ma na celu identyfiakcję tożsamości uzytkownika. Każde żądanie oznaczone jako wymagające autentykacji musi zawierać aktualną informację o tożsamości. 
Technicznie polega to na uzsykaniu tokena sesyjnego z aplikacji. Token sesyjny musi byc potem dostarczany jako nagłówek `authtoken` każdego kolejnego żądania HTTP

#### * `GET /auth/session` - sprawdza status token'a       

Response status:
     
     OK | Unauthorized
        
#### * `DELETE /auth/session` - dezaktywuje obecny token

Response status:
    
    OK | Unauthorized

#### * `POST /auth/fb` - loguje usera do systemu, a jeżeli jeszcze nie istnieje to rejestruje

Request:
        
    {
        "accessToken": "exampleFBtoken32434",
        "expiresIn": 1000 
    }
        
Response status:

    OK | BadRequest

Response body:
     
    {
        "token": "gfsqeb7igv30b7uo7ngel43q0aejqbkpu1pop0vaeccjr0fns0p",
        "userUUID": "db3987b9-4b8c-4363-8ebf-a1f37cdb475c",
        "validTo": "2015-05-28T14:04:19.655+02:00"
    }     
        

#### * `POST /auth/register` - rejestruję usera. Jeżeli użytkownik nie istniał to dostanie maila aktywacyjnego. Jeżeli istniał to dostanie maila z informacją że już istnieje i możliwością resety hasła

Request example:
    
    {
      "email" : "marcin.gosk@gmail.com",
      "password" : "secret"
    }

Response status:
 
    OK
    
#### * `POST /auth/activate` - aktywuje usera

Request example:
    
    {
      "token" : "token1"
    }

Response status:
 
    OK | BadRequest
    
#### * `POST /auth/login` - user login

Request example:
    
    {
      "email" : "email@mail.com",
      "password" : "secret"
    }

Response status:
 
    OK | BadRequest
    
#### * `POST /auth/password/reset` - send email with password reset token

Request example:
    
    {
      "email" : "email@mail.com"
    }

Response status:
 
    OK | BadRequest
    
#### * `POST /auth/password/reset/confirm` - token received in email

Request example:
    
    {
      "token" : "password_reset_token",
      "password" : "secret2"
    }

Response status:
 
    OK | BadRequest
        
    




---------------------------------------------

---------------------------------------------

// TODO implement mail/pass auth




        
* `POST /auth/confirm/{uuid}` - wysyła ponownie link aktywacyjny do użytkownika

* `GET /auth/confirm/{token}` - aktywuje konto

* `POST /auth/reset/{uuid}` - wysyła do użytkownika link z tokenem do resetu hasła

* `POST /auth/reset` - zapisuje nowe hasło

        Request:
        {
            [token]:String - password reset token
            [email]:String
            [password]:String
        }
        
        Response status: OK        

* `POST /auth/login` - loguje usera do systemu