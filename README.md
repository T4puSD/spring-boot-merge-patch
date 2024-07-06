## Mitigating Spring Boot's Merge Patch Limitation
This is a demo project to showcase a custom implementation to mitigate the json `merge-patch` problem in spring boot 
where we are unable to remove or delete a property utilizing the `PATCH` request.

## The main Util Class
https://github.com/T4puSD/spring-boot-merge-patch/blob/eeb3ede0422d59a34127358c4ef1fc290948f7bd/src/main/java/com/tapusd/poc/util/PatchUtil.java#L8-L46

If the `PATCH` request contains the following value; `author` value will be deleted.
```http request

PATCH http://localhost:8080/api/posts/1
Content-Type: application/json

{
 "title": "Test title 2",
 "author": null
}
```
The custom implementation also treat empty space `""` and string `"null"` as a trigger
to delete the property.