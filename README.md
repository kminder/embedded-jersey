Simple JAX-RS REST Server
=========================

This simple wrapper makes starting a JSE embedded JAX-RS REST server as simple as this:
 
 ```java
    server = new SimpleRestServer();
    server.uri( uri );
    server.resources( SampleJaxRsResource.class );
    server.start();
    server.awaitStop();
    server.destroy(); 
 