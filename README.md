Simple JAX-RS REST Server
=========================

This simple wrapper makes embedding a JAX-RS REST server as simple as this:

```java
  server = new SimpleRestServer();
  server.uri( "http://0.0.0.0:8888/" ).resources( SampleResource.class ).start().awaitStop().destroy();
```

The server can then be stopped from a resource like this:  

```java
  @Path( "/stop" )
  @GET
  @Produces( "text/plain" )
  public String stop() {
   server.stop();
   return "ok";
  }
```