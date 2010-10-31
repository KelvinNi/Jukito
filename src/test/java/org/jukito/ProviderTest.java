/**
 * Copyright 2010 ArcBees Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jukito;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test that providers injected by the tester module behaves correctly.
 * 
 * @author Philippe Beaudoin
 */
@RunWith(JukitoRunner.class)
public class ProviderTest {

  /**
   * Guice test module.
   */
  public static class Module extends JukitoModule {
    @Override
    protected void configureTest() {
      bindNamedMock(Mock.class, "singleton").in(TestScope.SINGLETON);
      bindNamedMock(Mock.class, "nonsingleton");
      bindNamed(Instance.class, "singleton").to(Instance.class).in(TestScope.SINGLETON);
      bindNamed(Instance.class, "nonsingleton").to(Instance.class);
      bindNamed(Parent.class, "providerInstance").toProvider(new ParentProviderA());
      bindNamed(Parent.class, "providerClass").toProvider(ParentProviderB.class);
    }
  }
  
  interface Mock { }  

  static class Instance {
    @Inject Instance() { }
  }
  
  interface Parent {
    String getValue();
  }  

  static class ChildA implements Parent {
    public String getValue() {
      return "childA";
    }
  }

  interface MockInChildB { }

  interface MockInProviderB {
    void test();
  }
  
  static class ChildB implements Parent {
    @Inject MockInChildB mockB;
    public String getValue() {
      return "childB";
    }
  }
  
  static class ParentProviderA implements Provider<Parent> {
    @Override
    public Parent get() {
      return new ChildA();
    }
  }
  
  static class ParentProviderB implements Provider<Parent> {
    private final Provider<ChildB> childBProvider;

    @Inject
    ParentProviderB(Provider<ChildB> childBProvider, Provider<MockInProviderB> myMock) {
      this.childBProvider = childBProvider;
      
      // These calls should succeed
      myMock.get().test();
      verify(myMock.get()).test();
    }
    
    @Override
    public Parent get() {
      return childBProvider.get();
    }
  }
  
  @Test
  public void mockSingletonProviderShouldReturnTheSameInstance(
      @Named("singleton") Provider<Mock> provider) {
    assertSame(provider.get(), provider.get());
  }
  
  @Test
  public void mockNonSingletonProviderShouldNotReturnTheSameInstance(
      @Named("nonsingleton") Provider<Mock> provider) {
    assertNotSame(provider.get(), provider.get());
  }
  
  @Test
  public void singletonClassShouldReturnTheSameInstance(
      @Named("singleton") Provider<Instance> provider) {
    assertSame(provider.get(), provider.get());
  }
  
  @Test
  public void nonSingletonClassShouldNotReturnTheSameInstance(
      @Named("nonsingleton") Provider<Instance> provider) {
    assertNotSame(provider.get(), provider.get());
  }

  @Test
  public void bindingToProviderInstanceShouldWorkAndInject(
      @Named("nonsingleton") Provider<Mock> provider) {
    assertNotSame(provider.get(), provider.get());
  }

  @Test
  public void shouldInjectProviderBoundWithInstance(
      @Named("providerInstance") Parent parentProvidedFromProviderInstance) {
  }

  @Test
  public void shouldInjectProviderBoundWithClass(
      @Named("providerClass") Parent parentProvidedFromProviderInstance) {
  }

}