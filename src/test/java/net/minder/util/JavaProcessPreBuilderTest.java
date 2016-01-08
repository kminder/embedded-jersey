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

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class JavaProcessPreBuilderTest {

  @Test
  public void testJvmPath() {
    JavaProcessPreBuilder jpb;

    jpb = new JavaProcessPreBuilder();
    jpb.main( "test-main" );
    assertThat( jpb.getJvmPathArg(), endsWith( "java" ) );
    assertThat( jpb.getCmdArgs().get( 0 ), endsWith( "java" ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    assertThat( jpb.getJvmPathArg(), is( "test-jvm" ) );
    assertThat( jpb.getCmdArgs().get( 0 ), is( "test-jvm" ) );
  }

  @Test
  public void testMain() throws IOException {
    JavaProcessPreBuilder jpb;

    jpb = new JavaProcessPreBuilder();
    assertThat( jpb.getMainClassName(), is( nullValue() ) );
    try {
      jpb.start();
      fail( "Expected IllegalArgumentException" );
    } catch ( IllegalArgumentException e ) {
      // Expected.
    }

    jpb = new JavaProcessPreBuilder();
    jpb.main( JavaProcessPreBuilderTest.class );
    assertThat( jpb.getMainClassName(), is( JavaProcessPreBuilderTest.class.getName() ) );

    jpb = new JavaProcessPreBuilder();
    jpb.main( "test-main" );
    assertThat( jpb.getMainClassName(), is( "test-main" ) );
    assertThat( jpb.getCmdArgs().get( 1 ), is( "test-main" ) );
  }

  @Test
  public void testMainArgs() {
    JavaProcessPreBuilder jpb;

    jpb = new JavaProcessPreBuilder();
    jpb.main( "test-main" );
    assertThat( jpb.getCmdArgs(), hasSize( 2 ) );

    jpb = new JavaProcessPreBuilder();
    jpb.main( "test-main" );
    jpb.args( "test-arg1" );
    assertThat( jpb.getMainArgs(), hasSize( 1 ) );
    assertThat( jpb.getMainArgs(), contains( "test-arg1" ) );
    assertThat( jpb.getCmdArgs(), hasSize( 3 ) );
    assertThat( jpb.getCmdArgs().get( 2 ), is( "test-arg1" ) );

    jpb = new JavaProcessPreBuilder();
    jpb.main( "test-main" );
    jpb.args( "test-arg1", "test-arg2" );
    assertThat( jpb.getMainArgs(), hasSize( 2 ) );
    assertThat( jpb.getMainArgs(), contains( "test-arg1", "test-arg2" ) );
    assertThat( jpb.getCmdArgs(), hasSize( 4 ) );
    assertThat( jpb.getCmdArgs().get( 2 ), is( "test-arg1" ) );
    assertThat( jpb.getCmdArgs().get( 3 ), is( "test-arg2" ) );

    jpb = new JavaProcessPreBuilder();
    jpb.main( "test-main" );
    jpb.args( Arrays.asList( "test-arg1", "test-arg2" ) );
    assertThat( jpb.getMainArgs(), hasSize( 2 ) );
    assertThat( jpb.getMainArgs(), contains( "test-arg1", "test-arg2" ) );
    assertThat( jpb.getCmdArgs(), hasSize( 4 ) );
    assertThat( jpb.getCmdArgs().get( 2 ), is( "test-arg1" ) );
    assertThat( jpb.getCmdArgs().get( 3 ), is( "test-arg2" ) );

    jpb = new JavaProcessPreBuilder();
    jpb.main( "test-main" );
    jpb.args();
    assertThat( jpb.getMainArgs(), hasSize( 0 ) );
    assertThat( jpb.getCmdArgs(), hasSize( 2 ) );

    jpb = new JavaProcessPreBuilder();
    jpb.main( "test-main" );
    jpb.args( (List<String>)null );
    assertThat( jpb.getMainArgs(), hasSize( 0 ) );
    assertThat( jpb.getCmdArgs(), hasSize( 2 ) );

    jpb = new JavaProcessPreBuilder();
    jpb.main( "test-main" );
    jpb.args( (String)null );
    assertThat( jpb.getMainArgs(), hasSize( 0 ) );
    assertThat( jpb.getCmdArgs(), hasSize( 2 ) );

    jpb = new JavaProcessPreBuilder();
    jpb.main( "test-main" );
    assertThat( jpb.getMainArgs(), hasSize( 0 ) );
    jpb.args( Arrays.asList( (String)null ) );
    assertThat( jpb.getCmdArgs(), hasSize( 2 ) );
  }

  @Test
  public void testClassPath() {
    JavaProcessPreBuilder jpb;

    String sep = System.getProperty( "path.separator" );
    String cp;

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.classPath();
    assertThat( jpb.getClassPathArgs(), hasSize( 0 ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.classPath( "test-jar" );
    assertThat( jpb.getClassPathArgs(), hasSize( 2 ) );
    assertThat( jpb.getClassPathArgs(), contains( "-cp", "test-jar" ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.classPath( "test-jar1", "test-jar2" );
    cp = StringUtils.join( Arrays.asList( "test-jar1", "test-jar2" ), sep );
    assertThat( jpb.getCmdArgs(), hasSize( 4 ) );
    assertThat( jpb.getCmdArgs(), contains( "test-jvm", "-cp", cp, "test-main" ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.classPath( Arrays.asList( "test-jar1", "test-jar2" ) );
    cp = StringUtils.join( Arrays.asList( "test-jar1", "test-jar2" ), sep );
    assertThat( jpb.getClassPathArgs(), contains( "-cp", cp ) );
    assertThat( jpb.getCmdArgs(), hasSize( 4 ) );
    assertThat( jpb.getCmdArgs(), contains( "test-jvm", "-cp", cp, "test-main" ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.classPath( "test-jar1" );
    jpb.classPath( "test-jar2" );
    cp = StringUtils.join( Arrays.asList( "test-jar1", "test-jar2" ), sep );
    assertThat( jpb.getClassPathArgs(), contains( "-cp", cp ) );
    assertThat( jpb.getCmdArgs(), hasSize( 4 ) );
    assertThat( jpb.getCmdArgs(), contains( "test-jvm", "-cp", cp, "test-main"  ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.inheritClassPath();
    cp = System.getProperty( "java.class.path" );
    assertThat( jpb.getClassPathArgs(), contains( "-cp", cp ) );
    assertThat( jpb.getCmdArgs(), hasSize( 4 ) );
    assertThat( jpb.getCmdArgs(), contains( "test-jvm", "-cp", cp, "test-main" ) );
  }

  @Test
  public void testDebug() {
    JavaProcessPreBuilder jpb;

    String s;

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.debug();
    s = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005";
    assertThat( jpb.getDbgArgs(), contains( s ) );
    assertThat( jpb.getCmdArgs(), contains( "test-jvm", s, "test-main" ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.debug( 42 );
    s = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=42";
    assertThat( jpb.getDbgArgs(), contains( s ) );
    assertThat( jpb.getCmdArgs(), contains( "test-jvm", s, "test-main" ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.debug( 42, true );
    s = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=42";
    assertThat( jpb.getDbgArgs(), contains( s ) );
    assertThat( jpb.getCmdArgs(), contains( "test-jvm", s, "test-main" ) );
  }

  @Test
  public void testInheritIO() {
    JavaProcessPreBuilder jpb;

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    assertThat( jpb.getInheritIO(), is( false ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.inheritIO();
    assertThat( jpb.getInheritIO(), is( true ) );
  }

  @Test
  public void testJvmArgs() {
    JavaProcessPreBuilder jpb;

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    assertThat( jpb.getJvmArgs(), hasSize( 0 ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.opt( "test-opt" );
    assertThat( jpb.getJvmArgs(), hasSize( 1 ) );
    assertThat( jpb.getJvmArgs(), contains( "-test-opt" ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.opt( "test-opt-name", "test-opt-value" );
    assertThat( jpb.getJvmArgs(), hasSize( 1 ) );
    assertThat( jpb.getJvmArgs(), contains( "-test-opt-name=test-opt-value" ) );
  }

  @Test
  public void testExtArgs() {
    JavaProcessPreBuilder jpb;

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    assertThat( jpb.getJvmArgs(), hasSize( 0 ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.xarg( "test-xarg" );
    assertThat( jpb.getExtArgs(), hasSize( 1 ) );
    assertThat( jpb.getExtArgs(), contains( "-Xtest-xarg" ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.xarg( "test-xarg-name", "test-xarg-value" );
    assertThat( jpb.getExtArgs(), hasSize( 1 ) );
    assertThat( jpb.getExtArgs(), contains( "-Xtest-xarg-name=test-xarg-value" ) );
  }

  @Test
  public void testSysProps() {
    JavaProcessPreBuilder jpb;

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    assertThat( jpb.getPropArgs(), hasSize( 0 ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.prop( "test-prop" );
    assertThat( jpb.getPropArgs(), hasSize( 1 ) );
    assertThat( jpb.getPropArgs(), contains( "-Dtest-prop" ) );

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.prop( "test-prop-name", "test-prop-value" );
    assertThat( jpb.getPropArgs(), hasSize( 1 ) );
    assertThat( jpb.getPropArgs(), contains( "-Dtest-prop-name=test-prop-value" ) );
  }

  @Test
  public void testEverything() {
    JavaProcessPreBuilder jpb;

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    jpb.classPath( "test-jar" );
    jpb.prop( "test-prop-name", "test-prop-value" );
    jpb.xarg( "test-xarg-name", "test-xarg-value" );
    jpb.args( "test-arg" );
    jpb.opt( "test-opt-name", "test-opt-value" );
    assertThat( jpb.getCmdArgs(), contains( "test-jvm", "-test-opt-name=test-opt-value", "-Xtest-xarg-name=test-xarg-value", "-Dtest-prop-name=test-prop-value", "-cp", "test-jar", "test-main", "test-arg" ) );
  }

  @Test
  public void testBuild() {
    JavaProcessPreBuilder jpb;

    jpb = new JavaProcessPreBuilder();
    jpb.jvm( "test-jvm" );
    jpb.main( "test-main" );
    assertThat( jpb.prepare(), notNullValue() );
  }

}
