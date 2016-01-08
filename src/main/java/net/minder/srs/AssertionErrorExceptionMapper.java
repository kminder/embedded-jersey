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
package net.minder.srs;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.PrintWriter;
import java.io.StringWriter;

@Provider
public class AssertionErrorExceptionMapper implements ExceptionMapper<AssertionError> {

  private static Response.StatusType ASSERTION_ERROR_STATUS = new AssertionErrorStatus();

  public static class AssertionErrorStatus implements Response.StatusType {

    public int getStatusCode() {
      return 517;
    }

    public Response.Status.Family getFamily() {
      return Response.Status.Family.OTHER;
    }

    public String getReasonPhrase() {
      return "Assertion Failed";
    }
  }

  @Override
  public Response toResponse( AssertionError e ) {
    javax.ws.rs.core.Response.ResponseBuilder builder = javax.ws.rs.core.Response.serverError();
    return builder.status( ASSERTION_ERROR_STATUS ).type( MediaType.TEXT_PLAIN_TYPE ).entity( formatException( e ) ).build();
  }

  private static String formatException( Throwable t ) {
    StringWriter w = new StringWriter();
    PrintWriter p = new PrintWriter( w );
    t.printStackTrace( p );
    return w.toString();
  }

}

