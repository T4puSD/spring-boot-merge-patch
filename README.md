## Mitigating Spring Boot's Merge Patch Limitation
This is a demo project to showcase a custom implementation to mitigate the json `merge-patch` problem in spring boot 
where we are unable to remove or delete a property utilizing the `PATCH` request.

# Example Request
If the `PATCH` request contains the following value; `author` value will be deleted.
```http request

PATCH http://localhost:8080/api/posts/1
Content-Type: application/json

{
 "title": "Test title 2",
 "author": null
}
```

The custom implementation treats `null` and empty space `""` as a trigger
to delete the property.