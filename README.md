[![Maven Central](https://maven-badges.herokuapp.com/maven-central/blog.svenbayer/spring-cloud-contract-swagger/badge.svg?style=plastic)](https://search.maven.org/search?q=g:blog.svenbayer%20AND%20a:spring-cloud-contract-swagger)
[![Javadocs](http://javadoc.io/badge/blog.svenbayer/spring-cloud-contract-swagger.svg?color=blue)](http://javadoc.io/doc/blog.svenbayer/spring-cloud-contract-swagger)
[![Project Stats](https://www.openhub.net/p/spring-cloud-contract-swagger/widgets/project_thin_badge?format=gif&ref=Thin+badge)](https://www.openhub.net/p/spring-cloud-contract-swagger)

[**\[Unit Tests** ![CircleCI](https://circleci.com/gh/SvenBayer/spring-cloud-contract-swagger/tree/master.svg?style=svg)](https://circleci.com/gh/SvenBayer/spring-cloud-contract-swagger/tree/master) 
[**\[Integration Tests** ![CircleCI](https://circleci.com/gh/SvenBayer/spring-cloud-contract-swagger-sample/tree/master.svg?style=svg)](https://circleci.com/gh/SvenBayer/spring-cloud-contract-swagger-sample/tree/master)

[![codecov](https://codecov.io/gh/SvenBayer/spring-cloud-contract-swagger/branch/master/graph/badge.svg)](https://codecov.io/gh/SvenBayer/spring-cloud-contract-swagger)
[**\[SonarQube\]**](https://sonarcloud.io/dashboard?id=blog.svenbayer%3Aspring-cloud-contract-swagger)

# Spring Cloud Contract Swagger
**Converts Swagger files to contracts for Spring Cloud Contract**

This project enables Spring Cloud Contract to parse [Swagger API 2.0 specifications](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md) as Spring Cloud Contracts.

## Usage
You can check out [spring-cloud-contract-swagger-sample](https://github.com/SvenBayer/spring-cloud-contract-swagger-sample) for examples.

### Producer
To convert a Swagger file to a Spring Cloud Contract and to execute it against a Producer, add the **spring-cloud-contract-maven-plugin** as plugin and the converter **spring-cloud-contract-swagger** as plugin-dependency.

### Consumer
For the consumer, add as dependencies the **spring-cloud-starter-contract-stub-runner** and the converter **spring-cloud-contract-swagger**.

### Default Behaviour
Currently, **Spring Cloud Contract Swagger** generates default values for a Swagger document’s fields.

* Boolean: true
* Number: 1
* Float/Double: 1.1
* String: (the name of the String)

### Custom Values
To set your own default values, you can use the **x-example** field in the **parameters** and **responses** section. In the **definitions** section, you can also use the **x-example** or the supported **example** field. You should avoid the **default** field, since the current Swagger parser (1.0.36) interprets numerical values of **default** fields as String. Regarding the order, the converter will first evaluate **example**, then **x-example**, and then **default** fields. If it does not find a predefined value, it will go all the way down to the primitive fields. The converter will only use the first response of a Swagger method entry.

### Optional Parameters
If you do not want to pass an optional parameter (required: false) in a request, you can add **x-ignore: true** to this field. 

### Patterns
Pattern only work for request parameters. Your provided example values will be checked against the pattern.

### Custom JSON Body
You can set the request and response body with a json string. For this, you have to place an **x-example** field next (on the same level) to the **schema** field. Use single ticks for your json string so you do not have to escape any quotes.

Also, you can reference external json files for the request and response by using the **x-ref** field. Just place the **x-ref** field next (on the same level) to the **schema** field. Use single ticks for the path, relative to the Swagger file. 

## Recommendation
If you are working in a project that restricts you to define your API with Swagger, this library is for you. Instead of defining your API and Groovy/JSON/YAML contracts, maintaining two separate documents, and risking inconsistency, you can maintain your API specification and contract in one document. This enables you to take at least partly benefit of CDC. The features of this are more restricted then by using proper contracts. However, if possible, you should go the way to define your API with contracts and let the Swagger documentation be generated by your Maven project. See more details at https://github.com/spring-cloud/spring-cloud-contract

If you decide to do contract testing with this project, you might have the choice to move gradually to Spring Cloud Contract with the [Swagger Request Validator](https://bitbucket.org/atlassian/swagger-request-validator) since *Spring Cloud Contract* generates WireMock stubs. This would enable you to add Groovy/JSON/YAML contracts and therefore test move scenarios. Thanks to [Marcin Grzejszczak](https://github.com/marcingrzejszczak) for this tip.

## Further Information
Read the blog about [Consumer Driven Contracts with Swagger](https://svenbayer.blog/cdc-with-swagger) for more information.
