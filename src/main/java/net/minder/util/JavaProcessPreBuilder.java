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
package net.minder.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class JavaProcessPreBuilder {

  private static List<String> EMPTY_LIST = Collections.unmodifiableList( Collections.EMPTY_LIST );
  private static String NULL_PROP_VALUE = new String("NULL");

  private String jvm = null;
  private ArrayList<String> opts = null;
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

  public JavaProcessPreBuilder jvm( String jvmFileName ) {
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

  public String getJvmPathArg() {
    if( jvm != null ) {
      return jvm;
    } else {
      return getCurrentJvm();
    }
  }

  public JavaProcessPreBuilder main( Class mainClass ) {
    main( mainClass.getName() );
    return this;
  }

  public JavaProcessPreBuilder main( String mainClassName ) {
    main = mainClassName;
    return this;
  }

  public String getMainClassName() {
    return main;
  }

  public JavaProcessPreBuilder args( String... values ) {
    if( values != null ) {
      args( Arrays.asList( values ) );
    }
    return this;
  }

  public JavaProcessPreBuilder args( List<String> args ) {
    if( args != null ) {
      if( this.args == null ) {
        this.args = new ArrayList();
      }
      for( String arg: args ) {
        if( arg != null ) {
          this.args.add( arg );
        }
      }
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

  public JavaProcessPreBuilder inheritClassPath() {
    classPath = getCurrentClassPath();
    return this;
  }

  public JavaProcessPreBuilder classPath( String... classPath ) {
    if( classPath != null ) {
      classPath( Arrays.asList( classPath ) );
    }
    return this;
  }

  public JavaProcessPreBuilder classPath( List<String> classPath ) {
    if( classPath != null ) {
      String sep = System.getProperty( "path.separator" );
      StringBuilder str = new StringBuilder( this.classPath == null ? "" : this.classPath );
      boolean first = ( str.length() == 0 );
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
    if( classPath != null && !classPath.isEmpty() ) {
      args.add( "-cp" );
      args.add( classPath );
    }
    return args;
  }

  public JavaProcessPreBuilder debug() {
    return debug( 5005, false );
  }

  public JavaProcessPreBuilder debug( int port ) {
    return debug( port, false );
  }

  public JavaProcessPreBuilder debug( int port, boolean suspend ) {
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

  public JavaProcessPreBuilder prop( String name ) {
    return prop( name, null );
  }

  public JavaProcessPreBuilder prop( String name, String value ) {
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

  public List<String> getPropArgs() {
    ArrayList<String> args = new ArrayList();
    if( sysProps != null ) {
      for( Map.Entry<Object,Object> entry: sysProps.entrySet() ) {
        Object name = entry.getKey();
        Object value = entry.getValue();
        if( value == null || value == NULL_PROP_VALUE ) {
          args.add( "-D" + name );
        } else {
          args.add( "-D" + name + "=" + value );
        }
      }
    }
    return args;
  }

  public JavaProcessPreBuilder opt( String name ) {
    return opt( name, null );
  }

  public JavaProcessPreBuilder opt( String name, String value ) {
    if( name != null ) {
      if( opts == null ) {
        opts  = new ArrayList();
      }
      if( value == null ) {
        opts.add( name );
      } else {
        opts.add( name + "=" + value );
      }
    }
    return this;
  }

  public List<String> getJvmArgs() {
    ArrayList<String> args = new ArrayList();
    if( opts != null ) {
      for( String opt: opts ) {
        args.add( "-" + opt );
      }
    }
    return args;
  }

  public JavaProcessPreBuilder xarg( String name ) {
    return xarg( name, null );
  }

  public JavaProcessPreBuilder xarg( String name, String value ) {
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

  public List<String> getExtArgs() {
    ArrayList<String> args = new ArrayList();
    if( xargs != null ) {
      for( String xarg: xargs ) {
        args.add( "-X" + xarg );
      }
    }
    return args;
  }

  public JavaProcessPreBuilder inheritIO() {
    inheritIo = Boolean.TRUE;
    return this;
  }

  public boolean getInheritIO() {
    return Boolean.TRUE.equals( inheritIo );
  }

  public List<String> getCmdArgs() {
    if( getMainClassName() == null ) { throw new IllegalArgumentException( "No main class name provided." ); }
    ArrayList<String> args = new ArrayList();
    args.add( getJvmPathArg() );
    args.addAll( getDbgArgs() );
    args.addAll( getJvmArgs() );
    args.addAll( getExtArgs() );
    args.addAll( getPropArgs() );
    args.addAll( getClassPathArgs() );
    args.add( getMainClassName() );
    args.addAll( getMainArgs() );
    return args;
  }

  public ProcessBuilder prepare() {
    ProcessBuilder pb = new ProcessBuilder();
    if( getInheritIO() ) { pb.inheritIO(); }
    pb.command( getCmdArgs() );
    return pb;
  }

  public Process start() throws IOException {
    return prepare().start();
  }

}
