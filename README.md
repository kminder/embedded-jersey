Simple JAX-RS REST Server
=========================

This simple wrapper that makes embedding a JAX-RS REST server as simple as this:
 
 ```java
    server = new SimpleRestServer();
    server.uri( "http://0.0.0.0:8888/" ).resources( SampleResource.class ).start().awaitStop().destroy();
 