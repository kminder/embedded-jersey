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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class JavaProcessBuilder {

  private static List<String> EMPTY_LIST = Collections.unmodifiableList( Collections.EMPTY_LIST );
  private static String NULL_PROP_VALUE = new String("NULL");

  private String jvm = null;
  private ArrayList<String> xargs = null;
  private String main = null;
  private ArrayList<String> args = null;
  private String classPath = null;
  private Properties sysProps = null;
  private Boolean inheritIo = null;
  private Integer debugPort = null;
  private Boolean debugWait = null;

  private static String getCurrentClassPath() {
    return System.getProperty( "java.class.path" );
  }

  public JavaProcessBuilder jvm( String jvmFileName ) {
    jvm = jvmFileName;
    return this;
  }

  private static String getCurrentJvm() {
    String jvm = "java";
    String sep = System.getProperty( "file.separator" );
    String home = System.getProperty( "java.home" );
    if( home != null && !home.isEmpty() ) {
      jvm = home + sep + "bin" + sep + "java";
    }
    return jvm;
  }

  public String getJvm() {
    if( jvm != null ) {
      return jvm;
    } else {
      return getCurrentJvm();
    }
  }

  public JavaProcessBuilder main( Class mainClass ) {
    main( mainClass.getName() );
    return this;
  }

  public JavaProcessBuilder main( String mainClassName ) {
    main = mainClassName;
    return this;
  }

  public String getMainClass() {
    if( main == null ) {
      throw new IllegalArgumentException( "main==null" );
    }
    return main;
  }

  public JavaProcessBuilder args( String... values ) {
    if( values != null ) {
      args( Arrays.asList( values ) );
    }
    return this;
  }

  public JavaProcessBuilder args( List<String> args ) {
    if( args != null ) {
      if( this.args == null ) {
        this.args = new ArrayList();
      }
      this.args.addAll( args );
    }
    return this;
  }

  public List<String> getMainArgs() {
    if( args != null ) {
      return args;
    } else {
      return EMPTY_LIST;
    }
  }

  public JavaProcessBuilder inheritClassPath() {
    classPath = getCurrentClassPath();
    return this;
  }

  public JavaProcessBuilder classPath( String... classPath ) {
    if( classPath != null ) {
      classPath( Arrays.asList( classPath ) );
    }
    return this;
  }

  public JavaProcessBuilder classPath( List<String> classPath ) {
    if( classPath != null ) {
      String sep = System.getenv( "path.separator" );
      StringBuilder str = new StringBuilder();
      boolean first = true;
      for( String classSrc : classPath ) {
        if( first ) {
          first = false;
        } else {
          str.append( sep );
          first = false;
        }
        str.append( classSrc );
      }
      this.classPath = str.toString();
    }
    return this;
  }

  public List<String> getClassPathArgs() {
    ArrayList<String> args = new ArrayList();
    if( classPath != null ) {
      args.add( "-cp" );
      args.add( classPath );
    }
    return args;
  }

  public JavaProcessBuilder debug() {
    return debug( 5005, false );
  }

  public JavaProcessBuilder debug( int port ) {
    return debug( port, false );
  }

  public JavaProcessBuilder debug( int port, boolean suspend ) {
    debugPort = port;
    debugWait = suspend;
    return this;
  }

  public List<String> getDbgArgs() {
    ArrayList<String> args = new ArrayList();
    if( debugPort != null ) {
      String s = Boolean.TRUE.equals( debugWait ) ? "y" : "n";
      args.add( String.format( "-agentlib:jdwp=transport=dt_socket,server=y,suspend=%s,address=%d", s, debugPort ) );
    }
    return args;
  }

  public JavaProcessBuilder prop( String name ) {
    return prop( name, null );
  }

  public JavaProcessBuilder prop( String name, String value ) {
    if( name != null ) {
      if( sysProps == null ) {
        sysProps = new Properties();
      }
      if( value == null ) {
        value = NULL_PROP_VALUE;
      }
      sysProps.setProperty( name, value );
    }
    return this;
  }

  public List<String> getSysProps() {
    ArrayList<String> args = new ArrayList();
    if( sysProps != null ) {
      for( Map.Entry<Object,Object> entry: sysProps.entrySet() ) {
        Object name = entry.getKey();
        Object value = entry.getValue();
        if( value != null ) {
          args.add( "-D" + name );
        } else {
          args.add( "-D" + name + "=" + value );
        }
      }
    }
    return args;
  }

  public JavaProcessBuilder xarg( String name ) {
    return xarg( name, null );
  }

  public JavaProcessBuilder xarg( String name, String value ) {
    if( name != null ) {
      if( xargs == null ) {
        xargs  = new ArrayList();
      }
      if( value == null ) {
        xargs.add( name );
      } else {
        xargs.add( name + "=" + value );
      }
    }
    return this;
  }

  public List<String> getJvmArgs() {
    ArrayList<String> args = new ArrayList();
    if( xargs != null ) {
      for( String xarg: xargs ) {
        args.add( "-X" + xarg );
      }
    }
    return args;
  }

  public JavaProcessBuilder inheritIO() {
    inheritIo = Boolean.TRUE;
    return this;
  }

  public Process start() throws IOException {
    ProcessBuilder pb = new ProcessBuilder();
    ArrayList<String> args = new ArrayList();
    args.add( getJvm() );
    args.addAll( getDbgArgs() );
    args.addAll( getJvmArgs() );
    args.addAll( getSysProps() );
    args.addAll( getClassPathArgs() );
    args.add( getMainClass() );
    args.addAll( getMainArgs() );
    pb.command( args );
    if( Boolean.TRUE.equals( inheritIo ) ) { pb.inheritIO(); }
    return pb.start();
  }

}
