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

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class EmbeddedJerseyServer {

  private URI uri;
  private ResourceConfig config;
  private ExecutorService threads;
  private HttpServer server;
  private Semaphore barrier = new Semaphore( 1 );

  public EmbeddedJerseyServer() {
    config = new ResourceConfig();
    providers( AssertionErrorExceptionMapper.class );
  }

  public EmbeddedJerseyServer uri( URI uri ) {
    this.uri = uri;
    return this;
  }

  public EmbeddedJerseyServer providers( Class... providerClasses ) {
    for( Class c: providerClasses ) {
      config.register( c );
    }
    return this;
  }

  public EmbeddedJerseyServer resources( Class... resourceClasses ) {
    config.registerClasses( resourceClasses );
    return this;
  }

  public void start() throws InterruptedException {
    threads = Executors.newCachedThreadPool();
    server = JdkHttpServerFactory.createHttpServer( uri, config, false );
    server.setExecutor( threads );
    barrier.acquire();
    server.start();
  }

  public void awaitStop() throws InterruptedException {
    barrier.acquire();
  }

  public void stop() {
    barrier.release();
  }

  public void destroy() {
    server.stop( 0 );
    threads.shutdown();
  }

}
