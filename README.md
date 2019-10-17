# Spring Boot - Stack Exchange Service

Goal:
*	Create an API server which can analyse the content of big xml files
*	A distributable docker container running the server

Requirement:
*	Create an API with Spring Boot (Java) and Maven.
*	A POST request should be possible with a url to an XML file (this file can be > 1GB)
*	The response of the post request should hold an overview of the analysation of the XML fields.
*	The code should be unit and component tested.
*	Place the code on github account.
*	The code should pass the maven build and be runnable via cli with max of 512MB of memory.
*	At least a single Java 8 feature should be included.

Docker container:
*	Create a docker-image for the server, based on the standard Java docker image (https://hub.docker.com/_/java/). The dockerfile used to create this image should be part of the repo.
*	Commit the docker image on Dockerhub (https://hub.docker.com)
*	Add a Readme on how to start and use the docker image

Example files:
The files are based on stack overflow site with data per topic.
*	805Kb - https://s3-eu-west-1.amazonaws.com/merapar-assessment/3dprinting-posts.xml
*	71Kb - https://s3-eu-west-1.amazonaws.com/merapar-assessment/arabic-posts.xml

Find other larger files on archive site https://archive.org/details/stackexchange

example of posts.xml file is available in src/test/resources/xml/posts/posts.xml
if you copy posts.xml to C: on your local harddrive you could access it with the service with the following url:
file:c://posts.xml

example JSON post request:
 > POST http://localhost:8080/stack/posts/analyze
 > host: localhost:8080
 > Content-Type: application/json
{
  "url" : "https://s3-eu-west-1.amazonaws.com/merapar-assessment/arabic-posts.xml"
}

example JSON response:
<addr>
 > POST http://localhost:8080/stack/posts/analyze
 < 200 (OK)
 < Content-Type: application/json
{
  "analyseDate" : "2019-10-18T00:23:24.432",
  "details" : {
    "firstPost" : "2015-07-14T18:39:27.757",
    "lastPost" : "2015-09-14T12:46:52.053",
    "totalPosts" : 80,
    "totalAcceptedPosts" : 7,
    "avgScore" : 2
  }
}
</addr>

You can run the spring boot jar by using Java 8 with the following command from commandline:
java -jar stackservice-1.0-SNAPSHOT.jar

if you are behind a proxy add proxy configuration as jvm parameters to avoid UnkownHostException, for instance like this:
java -Djava.net.useSystemProxies=true -jar stackservice-1.0-SNAPSHOT.jar

When the server is running you can test the service with the Swagger-UI:
http://localhost:8080/swagger-ui.html

the api documentation is available in openapi.json format:
http://localhost:8080/v3/api-

A truststore is available in src/main/resources/keystore. Add a signed certificate in the truststore for the https url you want to get a xml file from. if you don't have such a certificate use http or enable the accept all truststore in application.properties. If you add a certificate with keytool make sure it is signed by an authorized CA (available in cacerts keystore of your jre) in the top of the certificate chain. 

test the service after adding the certificate by starting the service like this (not tested yet):
java -Djava.net.useSystemProxies=true -jar stackservice-1.0-SNAPSHOT.jar --http.client.ssl.accept-all-trust-store=false


# Used depedencies:
Spring Boot 2
Lombok
Mapstruct 1.1 (versions 1.2 amd 1.3 give conflicts with Lombok)
openapi 3
JUnit 4
Mockito 2
Mock-server





