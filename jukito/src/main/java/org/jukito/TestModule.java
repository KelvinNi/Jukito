/**
 * Copyright 2014 ArcBees Inc.
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

import java.lang.reflect.Constructor;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.name.Names;
import com.google.inject.spi.InjectionPoint;

/**
 * A guice {@link com.google.inject.Module Module} with a bit of syntactic sugar to bind within
 * typical test scopes. Depends on mockito.
 * <p/>
 * Depends on Mockito.
 */
public abstract class TestModule extends AbstractModule {

    protected Class<?> testClass;

    /**
     * Attach the {@link TestModule} to a given test class.
     *
     * @param testClass The test class to attach to this {@link TestModule}.
     */
    public void setTestClass(Class<?> testClass) {
        this.testClass = testClass;
    }

    @Override
    public void configure() {
        bindScopes();
        configureTest();
    }

    protected void bindScopes() {
        bindScope(TestSingleton.class, TestScope.SINGLETON);
        bindScope(TestEagerSingleton.class, TestScope.EAGER_SINGLETON);
    }

    /**
     * Configures a test {@link com.google.inject.Module Module} via the exposed methods.
     */
    protected abstract void configureTest();

    /**
     * Binds an interface to a mocked version of itself. You will usually want to bind this in the
     * {@link TestSingleton} scope.
     *
     * @param <T>   The type of the interface to bind
     * @param klass The class to bind
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> ScopedBindingBuilder bindMock(Class<T> klass) {
        return bindNewMockProvider(Key.get(klass));
    }

    /**
     * Binds an interface annotated with a {@link com.google.inject.name.Named @Named} to a
     * mocked version of itself. You will usually want to bind this in the
     * {@link TestSingleton} scope.
     *
     * @param <T>         The type of the interface to bind, a parameterized type
     * @param typeLiteral The {@link TypeLiteral} corresponding to the parameterized type to bind.
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> ScopedBindingBuilder bindMock(
            TypeLiteral<T> typeLiteral) {
        return bindNewMockProvider(Key.get(typeLiteral));
    }

    /**
     * Binds a concrete object type so that spies of instances are returned
     * instead of instances themselves. You will usually want to bind this in the
     * {@link TestSingleton} scope.
     *
     * @param <T>   The type of the interface to bind
     * @param klass The class to bind
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> ScopedBindingBuilder bindSpy(Class<T> klass) {
        return bindNewSpyProvider(Key.get(klass));
    }

    /**
     * Binds a concrete object type so that spies of instances are returned
     * instead of instances themselves. You will usually want to bind this in the
     * {@link TestSingleton} scope.
     *
     * @param <T>         The type of the interface to bind, a parameterized type
     * @param typeLiteral The {@link TypeLiteral} corresponding to the parameterized type to bind.
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> ScopedBindingBuilder bindSpy(
            TypeLiteral<T> typeLiteral) {
        return bindNewSpyProvider(Key.get(typeLiteral));
    }

    /**
     * Binds a concrete instance so that spies of this instance are returned
     * instead of the object itself. Each spy is an independent spy but the
     * underlying instance will be the same, so if the object is mutable,
     * your tests can be polluted!
     * <p/>
     * You will usually want to bind this in the {@link TestSingleton} scope.
     *
     * @param <T>      The type of the interface to bind
     * @param klass    The class to bind
     * @param instance The instance to bind this class to.
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> ScopedBindingBuilder bindSpy(Class<T> klass, T instance) {
        return bindNewSpyImmutableInstanceProvider(Key.get(klass), instance);
    }

    /**
     * Binds a concrete instance so that spies of this instance are returned
     * instead of the object itself. Each spy is an independent spy but the
     * underlying instance will be the same, so if the object is mutable,
     * your tests can be polluted!
     * <p/>
     * You will usually want to bind this in the
     * {@link TestSingleton} scope.
     *
     * @param <T>         The type of the interface to bind, a parameterized type
     * @param typeLiteral The {@link TypeLiteral} corresponding to the parameterized type to bind.
     * @param instance    The instance to bind this class to.
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> ScopedBindingBuilder bindSpy(
            TypeLiteral<T> typeLiteral, T instance) {
        return bindNewSpyImmutableInstanceProvider(Key.get(typeLiteral), instance);
    }

    /**
     * Binds an interface annotated with a {@link com.google.inject.name.Named @Named} to a
     * mocked version of itself. You will usually want to bind this in the
     * {@link TestSingleton} scope.
     *
     * @param <T>   The type of the interface to bind
     * @param klass The class to bind
     * @param name  The name used with the {@link com.google.inject.name.Named @Named} annotation.
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> ScopedBindingBuilder bindNamedMock(Class<T> klass, String name) {
        return bindNewMockProvider(Key.get(klass, Names.named(name)));
    }

    /**
     * Binds an interface annotated with a {@link com.google.inject.name.Named @Named} to a
     * mocked version of itself. You will usually want to bind this in the
     * {@link TestSingleton} scope.
     *
     * @param <T>         The type of the interface to bind
     * @param typeLiteral The {@link TypeLiteral} corresponding to the parameterized type to bind.
     * @param name        The name used with the {@link com.google.inject.name.Named @Named} annotation.
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> ScopedBindingBuilder bindNamedMock(TypeLiteral<T> typeLiteral,
            String name) {
        return bindNewMockProvider(Key.get(typeLiteral, Names.named(name)));
    }

    /**
     * Binds a concrete object type annotated with a
     * {@link com.google.inject.name.Named @Named} so that spies of instances are returned
     * instead of instances themselves. You will usually want to bind this in the
     * {@link TestSingleton} scope.
     *
     * @param <T>   The type of the interface to bind
     * @param klass The class to bind
     * @param name  The name used with the {@link com.google.inject.name.Named @Named} annotation.
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> ScopedBindingBuilder bindNamedSpy(Class<T> klass, String name) {
        return bindNewSpyProvider(Key.get(klass, Names.named(name)));
    }

    /**
     * Binds  a concrete object type annotated with a
     * {@link com.google.inject.name.Named @Named} so that spies of instances are returned
     * instead of instances themselves. You will usually want to bind this in the
     * {@link TestSingleton} scope.
     *
     * @param <T>         The type of the interface to bind
     * @param typeLiteral The {@link TypeLiteral} corresponding to the parameterized type to bind.
     * @param name        The name used with the {@link com.google.inject.name.Named @Named} annotation.
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> ScopedBindingBuilder bindNamedSpy(TypeLiteral<T> typeLiteral,
            String name) {
        return bindNewSpyProvider(Key.get(typeLiteral, Names.named(name)));
    }

    /**
     * Binds a concrete instance annotated with {@link com.google.inject.name.Named @Named} so that spies of this
     * instance are returned instead of the object itself. Each spy is an independent spy but the underlying instance
     * will be the same, so if the object is mutable, your tests can be polluted!
     * <p/>
     * You will usually want to bind this in the {@link TestSingleton} scope.
     *
     * @param <T>      The type of the interface to bind
     * @param klass    The class to bind
     * @param instance The instance to bind this class to.
     * @param name     The name used with the {@link com.google.inject.name.Named @Named} annotation.
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> ScopedBindingBuilder bindNamedSpy(Class<T> klass, T instance,
            String name) {
        return bindNewSpyImmutableInstanceProvider(Key.get(klass, Names.named(name)), instance);
    }

    /**
     * Binds a concrete instance annotated with {@link com.google.inject.name.Named @Named} so that spies of this
     * instance are returned instead of the object itself. Each spy is an independent spy but the underlying instance
     * will be the same, so if the object is mutable, your tests can be polluted!
     * <p/>
     * You will usually want to bind this in the {@link TestSingleton} scope.
     *
     * @param <T>         The type of the interface to bind
     * @param typeLiteral The {@link TypeLiteral} corresponding to the parameterized type to bind.
     * @param instance    The instance to bind this class to.
     * @param name        The name used with the {@link com.google.inject.name.Named @Named} annotation.
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> ScopedBindingBuilder bindNamedSpy(TypeLiteral<T> typeLiteral,
            T instance, String name) {
        return bindNewSpyImmutableInstanceProvider(Key.get(typeLiteral, Names.named(name)), instance);
    }

    @SuppressWarnings("unchecked")
    private <T> ScopedBindingBuilder bindNewMockProvider(Key<T> key) {
        return bind(key).toProvider(
                new MockProvider<T>((Class<T>) key.getTypeLiteral().getRawType()));
    }

    @SuppressWarnings("unchecked")
    private <T> ScopedBindingBuilder bindNewSpyProvider(Key<T> key) {
        TypeLiteral<T> type = key.getTypeLiteral();
        InjectionPoint constructorInjectionPoint = InjectionPoint.forConstructorOf(type);
        Key<T> relayingKey = Key.get(type, JukitoInternal.class);
        bind(relayingKey).toConstructor((Constructor<T>) constructorInjectionPoint.getMember());
        return bind(key).toProvider(new SpyProvider<T>(getProvider(relayingKey), relayingKey));
    }

    private <T> ScopedBindingBuilder bindNewSpyImmutableInstanceProvider(Key<T> key, T instance) {
        return bind(key).toProvider(new SpyImmutableInstanceProvider<T>(instance));
    }

    /**
     * This method binds many different instances to the same class or interface. Use this only
     * if the instances are totally stateless. That is, they are immutable and have
     * no mutable dependencies (e.g. a {@link String} or a simple POJO). For more
     * complex classes use {@link #bindMany}.
     * <p/>
     * The specified {@link Class} will be bound to all the different instances, each
     * binding using a different unique annotation.
     * <p/>
     * This method is useful when combined with the {@literal @}{@link All} annotation.
     *
     * @param clazz     The {@link Class} to which the instances will be bound.
     * @param instances All the instances to bind.
     * @see {@link All}
     */
    protected <T, V extends T> void bindManyInstances(Class<T> clazz, V... instances) {
        bindManyNamedInstances(clazz, All.DEFAULT, instances);
    }

    /**
     * This method binds many different instances to the same class or interface. Use this only
     * if the instances are totally stateless. That is, they are immutable and have
     * no mutable dependencies (e.g. a {@link String} or a simple POJO). For more
     * complex classes use {@link #bindMany}.
     * <p/>
     * The specified {@link Class} will be bound to all the different instances, each
     * binding using a different unique but named annotation.
     * <p/>
     * This method is useful when combined with the {@literal @}{@link All} annotation with
     * a name parameter.
     *
     * @param clazz     The {@link Class} to which the instances will be bound.
     * @param name      The name to which to bind the instances.
     * @param instances All the instances to bind.
     * @see {@link All}
     */
    protected <T, V extends T> void bindManyNamedInstances(Class<T> clazz, String name, V... instances) {
        for (V instance : instances) {
            bind(clazz).annotatedWith(NamedUniqueAnnotations.create(name)).toInstance(instance);
        }
    }

    /**
     * This method binds many different instances to the same type literal. Use this only
     * if the instances are totally stateless. That is, they are immutable and have
     * no mutable dependencies (e.g. a {@link String} or a simple POJO). For more
     * complex classes use {@link #bindMany}.
     * <p/>
     * The specified {@link TypeLiteral} will be bound to all the different instances, each
     * binding using a different unique annotation.
     * <p/>
     * This method is useful when combined with the {@literal @}{@link All} annotation.
     *
     * @param type      The {@link TypeLiteral} to which the instances will be bound.
     * @param instances All the instances to bind.
     * @see {@link All}
     */
    protected <T, V extends T> void bindManyInstances(TypeLiteral<T> type, V... instances) {
        bindManyNamedInstances(type, All.DEFAULT, instances);
    }

    /**
     * This method binds many different instances to the same class or interface. Use this only
     * if the instances are totally stateless. That is, they are immutable and have
     * no mutable dependencies (e.g. a {@link String} or a simple POJO). For more
     * complex classes use {@link #bindMany}.
     * <p/>
     * The specified {@link Class} will be bound to all the different instances, each
     * binding using a different unique but named annotation.
     * <p/>
     * This method is useful when combined with the {@literal @}{@link All} annotation with
     * a name.
     *
     * @param type      The {@link Class} to which the instances will be bound.
     * @param name      The name to which to bind the instances.
     * @param instances All the instances to bind.
     * @see {@link All}
     */
    protected <T, V extends T> void bindManyNamedInstances(TypeLiteral<T> type, String name, V... instances) {
        for (V instance : instances) {
            bind(type).annotatedWith(NamedUniqueAnnotations.create(name)).toInstance(instance);
        }
    }

    /**
     * This method binds many different classes to the same interface. All the
     * classes will be bound within the {@link TestScope#SINGLETON} scope.
     * <p/>
     * This method is useful when combined with the {@literal @}{@link All} annotation.
     *
     * @param clazz        The {@link Class} to which the instances will be bound.
     * @param boundClasses All the classes to bind.
     * @see {@link All}
     */
    protected <T> void bindMany(Class<T> clazz, Class<? extends T>... boundClasses) {
        bindManyNamed(clazz, All.DEFAULT, boundClasses);
    }

    /**
     * This method binds many different type literals to the same type literal. All the
     * classes will be bound within the {@link TestScope#SINGLETON} scope.
     * <p/>
     * This method is useful when combined with the {@literal @}{@link All} annotation with
     * a name.
     *
     * @param clazz        The {@link Class} to which the instances will be bound.
     * @param name         The name to which to bind the instances.
     * @param boundClasses All the types to bind.
     * @see {@link All}
     */
    protected <T> void bindManyNamed(Class<T> clazz, String name, Class<? extends T>... boundClasses) {
        for (Class<? extends T> boundClass : boundClasses) {
            bind(clazz).annotatedWith(NamedUniqueAnnotations.create(name)).to(boundClass).in(TestScope.SINGLETON);
        }
    }

    /**
     * This method binds many different type literals to the same type literal. All the
     * classes will be bound within the {@link TestScope#SINGLETON} scope.
     * <p/>
     * This method is useful when combined with the {@literal @}{@link All} annotation.
     *
     * @param type       The {@link Class} to which the instances will be bound.
     * @param boundTypes All the types to bind.
     * @see {@link All}
     */
    protected <T> void bindMany(TypeLiteral<T> type, TypeLiteral<? extends T>... boundTypes) {
        bindManyNamed(type, All.DEFAULT, boundTypes);
    }

    /**
     * This method binds many different type literals to the same type literal. All the
     * classes will be bound within the {@link TestScope#SINGLETON} scope.
     * <p/>
     * This method is useful when combined with the {@literal @}{@link All} annotation with
     * a name.
     *
     * @param type       The {@link Class} to which the instances will be bound.
     * @param name       The name to which to bind the instances.
     * @param boundTypes All the types to bind.
     * @see {@link All}
     */
    protected <T> void bindManyNamed(TypeLiteral<T> type, String name,
            TypeLiteral<? extends T>... boundTypes) {
        for (TypeLiteral<? extends T> boundType : boundTypes) {
            bind(type).annotatedWith(NamedUniqueAnnotations.create(name)).to(boundType).in(TestScope.SINGLETON);
        }
    }

    /**
     * Binds an interface annotated with a {@link com.google.inject.name.Named @Named}.
     *
     * @param <T>   The type of the interface to bind
     * @param klass The class to bind
     * @param name  The name used with the {@link com.google.inject.name.Named @Named} annotation.
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> LinkedBindingBuilder<T> bindNamed(Class<T> klass, String name) {
        return bind(klass).annotatedWith(Names.named(name));
    }

    /**
     * Binds an interface annotated with a {@link com.google.inject.name.Named @Named}.
     *
     * @param <T>         The type of the interface to bind
     * @param typeLiteral The {@link TypeLiteral} corresponding to the parameterized type to bind.
     * @param name        The name used with the {@link com.google.inject.name.Named @Named} annotation.
     * @return A {@link ScopedBindingBuilder}.
     */
    protected <T> LinkedBindingBuilder<T> bindNamed(TypeLiteral<T> typeLiteral,
            String name) {
        return bind(typeLiteral).annotatedWith(Names.named(name));
    }
}
