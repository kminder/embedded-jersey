/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.minder.poc;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.BrokenBarrierException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class JseEmbeddedJerseySample {

  private static EmbeddedJerseyServer server;

  public static class Input {
    public Input() {}
    public String name = "test-name";
    public String[] args = new String[]{ "one", "two" };
  }

  @Path( "/exec" )
  public static class Exec {

    @POST
    @Consumes( "application/json" )
    @Produces( "text/plain" )
    public String exec( Input input ) {
      System.out.println( input.name );
      for( int i=0; i< input.args.length; i++ ) {
        System.out.println( "  [" + i + "]=" + input.args[ i ] );
      }
      assertThat( input.name, is( "test-name" ) );
      return "ok";
    }

  }

  public static class Output {
    public Output() {}
    public String id = "test-id";
    public String desc = "test-desc";
    public String toString() { return "id=" + id + ", desc=" + desc; }
  }

  @Path( "/query" )
  public static class Query {

    @GET
    @Produces( "application/json" )
    public Output status() {
      Output output = new Output();
      return output;
    }

  }

  @Path( "/ping" )
  public static class Ping {

    @GET
    @Produces( "text/plain" )
    public String ping() {
      return "hello";
    }

  }

  @Path( "/exit" )
  public static class Exit {

    @GET
    public String exit() {
      server.stop();
      return "ok";
    }

  }

  public static void main( String[] args ) throws IOException, BrokenBarrierException, InterruptedException {

    int port = args.length == 0 ? 9999 : Integer.parseInt( args[0] );
    URI uri = UriBuilder.fromUri( "http://0.0.0.0/" ).port( port ).build();

    server = new EmbeddedJerseyServer();
    server.uri( uri );
    server.resources( Exec.class, Query.class, Ping.class, Exit.class );
    server.start();
    server.awaitStop();
    server.destroy();

  }

}
