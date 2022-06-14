# Warehouse Test Application

### About the Tech Stack
The project was developed in Kotlin which now I'm much more comfortable with. 
The language to me doesn't look too different compared to Java (newer versions), but little details to work with immutability, 
piling operations on collections and simply returning it makes the code much simpler in my opinion. 
Also, the way Kotlin deals with nullability is something that I really like.

I used Spring in this project because, honestly, it's the only framework that I know the best nowadays. 
Already had some little and quick tests with Quarkus and Ktor, but it was a long time ago. 
I know about the incongruity of working with Kotlin and Spring, working with Kotlin should open the 
possibility to work more towards functional programming, but Spring and its tools don't help a lot with that 
since it's basically doing reflection and a lot of side effects.

As far as I can tell, trying to code taking advantage of data classes and immutability in general made my code much 
more maintainable, readable and easy to write tests.

The database I've chosen was MongoDB. In my experience (e-commerce in Brazil), some microservices were using MongoDB, 
and we could see how easy it was to scale up those applications. The interface with Spring Data is really nice and easy to reason. 
The only thing that starts to become tricky is when you have to fetch data from different collections to combine them, 
and then you make the decision to create a composer and deal with it in memory, or use some aggregators from MongoDB.

Another choice which already gave me some good results in my career was WebFlux with Reactor. 
Because of the Event Loop strategy that WebFlux uses to manage the requests of your Rest API, it makes your application 
better use threads and at the end you'll see less GC presence compared to traditional Rest APIs in Java, and also 
considerably less use of memory. In general, you can say your application is more available, not faster.

### Only one service for Inventory and Products
To deliver this project I've chosen to build only one microservice, but in reality, talking about production in a real scenario, 
I would say both subjects (inventory and product) should be even in different microservices. 
This makes it possible for both services to grow and if you need to fetch data from both and combine, 
then another service could be a good approach to aggregate information. Also, graphQL could be an option.

### Some assumptions about the project
- Both endpoints to upload the json files were made not caring about existent data. 
I've just assumed that those calls would be made for a database startup. 
As I haven't put any product identification in the json file, they'll be added without any problems. 
I assumed that there's another service managing backoffice and an integration with inventory and other services related to stock/warehouse.

- The upload of the files could also be done using better management. Even a job controlling how many products or 
articles were added and how many got errors because of a problem in the json file or something else.
Also, because this can be really tricky identifying existent products and articles and doing a consolidation between the file and the database,
a job would be a better approach because it would take more time and resources to check stock and merge information.

- I've added in the inventory/article domain data the field "minStock" to represent the minimum stock that this 
article must have to be considered as available. This way, considering concurrency problems, even if the front-end 
doesn't correctly know the number of articles available, at least there's a margin of error and the product can be sold.

### Documentation and Postman Collection
I don't agree with writing comments and documentation in the code. If you need it, it's because one is doing something really dark and hidden
that's not able for others to understand. But when a service has endpoints, for example, a good way to help with the documentation, in my opinion,
is to provide a Postman Collection or anything like that.

So that's what I'm doing. Together with the zip of this project, I'm also providing the Postman Collection that I used to test the endpoints:
- POST /products/?uploadFile
- POST /inventories/?uploadFile
- PUT /products/{productNumber}?sell
- GET /products/?page=0&size=10

You can find the product and inventory files at the root directory of project, in the "files" directory.
In there, also, you can find the postman collection:

 - files
   - inventory_json_upload.json
   - product_json_upload.json
   - warehouse-test.postman_collection.json