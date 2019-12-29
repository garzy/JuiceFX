package com.airhacks.afterburner.injection;

/*
 * #%L
 * afterburner.fx
 * %%
 * Copyright (C) 2013 - 2018 Adam Bien
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.airhacks.afterburner.configuration.Configurator;
import com.airhacks.afterburner.injection.modules.JuiceFXModule;
import com.google.inject.Guice;
import com.google.inject.Module;

/**
 *
 * @author adam-bien.com
 */
public class Injector {

    private static final Set<Object> presenters = Collections.newSetFromMap(new WeakHashMap<>());

    private static Function<Class<?>, Object> instanceSupplier = getDefaultInstanceSupplier();

    private static final Configurator configurator = new Configurator();
    
    private static com.google.inject.Injector guiceInjector = null;

	private static final List<Module> guiceModules = new ArrayList<>();

    public static <T> T instantiatePresenter(Class<T> clazz, Function<String, Object> injectionContext) {
        @SuppressWarnings("unchecked")
		T presenter = (T) instanceSupplier.apply(clazz);
		presenters.add(presenter);
        //after the regular, conventional initialization and injection, perform postinjection
        Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                final String fieldName = field.getName();
                final Object value = injectionContext.apply(fieldName);
                if (value != null) {
					injectIntoField(field, presenter, value);
                }
            }
        }
        return presenter;
    }
    
	private static void injectIntoField(final Field field, final Object instance, final Object target) {
        AccessController.doPrivileged((PrivilegedAction<?>) () -> {
            boolean wasAccessible = field.isAccessible();
            try {
                field.setAccessible(true);
                field.set(instance, target);
                return null; // return nothing...
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new IllegalStateException("Cannot set field: " + field + " with value " + target, ex);
            } finally {
                field.setAccessible(wasAccessible);
            }
        });
    }
    
    public <T> T getInstance(Class<T> clazz) {
        return getGuiceInjector().getInstance(clazz);
    }

    public static <T> T instantiatePresenter(Class<T> clazz) {
        return instantiatePresenter(clazz, f -> null);
    }

    public static void setInstanceSupplier(Function<Class<?>, Object> instanceSupplier) {
        Injector.instanceSupplier = instanceSupplier;
    }

    public static void setConfigurationSource(Function<Object, Object> configurationSupplier) {
        configurator.set(configurationSupplier);
    }

    public static void resetInstanceSupplier() {
        instanceSupplier = getDefaultInstanceSupplier();
    }

    public static void resetConfigurationSource() {
        configurator.forgetAll();
    }

    /**
     * Caches the passed presenter internally and injects all fields
     *
     * @param <T> the class to initialize
     * @param instance An already existing (legacy) presenter interesting in
     * injection
     * @return presenter with injected fields
     */
	public static <T> T registerExistingAndInject(T instance) {
		doCallAnotationMethodsOnFieldsInstance(instance, PostConstruct.class);
		presenters.add(instance);
		return instance;
	}

	/*
	 * For Object that have been created without Guice usiong new Constructor.
	 */
	protected static <T> T injectAndInitialize(T product) {
		getGuiceInjector().injectMembers(product);
		doCallAnotationMethodsOnFieldsInstance(product, PostConstruct.class);
		return product;
	}

	protected static void destroy(Object instance) {
		doCallAnotationMethodsOnFieldsInstance(instance, PreDestroy.class);
    }

	private static void invokeMethodWithAnnotation(Class<?> clazz, final Object instance,
			final Class<? extends Annotation> annotationClass) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (final Method method : declaredMethods) {
            if (method.isAnnotationPresent(annotationClass)) {
                AccessController.doPrivileged((PrivilegedAction<?>) () -> {
                    boolean wasAccessible = method.isAccessible();
                    try {
                        method.setAccessible(true);
						return method.invoke(instance);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        throw new IllegalStateException("Problem invoking " + annotationClass + " : " + method, ex);
                    } finally {
                        method.setAccessible(wasAccessible);
                    }
                });
            }
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            invokeMethodWithAnnotation(superclass, instance, annotationClass);
        }
    }

    public static void forgetAll() {
		presenters.forEach(Injector::destroy);
		presenters.clear();
        resetInstanceSupplier();
        resetConfigurationSource();
    }

	private static Function<Class<?>, Object> getDefaultInstanceSupplier() {
		return c -> {
            try {
				Object instance = getGuiceInjector().getInstance(c);
				doCallAnotationMethodsOnFieldsInstance(instance, PostConstruct.class);
				return instance;
            } catch (Exception ex) {
                throw new IllegalStateException("Cannot instantiate view: " + c, ex);
            }
        };
    }    

	private static void doCallAnotationMethodsOnFieldsInstance(Object instance,
			final Class<? extends Annotation> annotationClass) {
		Field[] fields = instance.getClass().getDeclaredFields();
		for (Field field : fields) {
			boolean wasAccesible = field.isAccessible();
			field.setAccessible(true);
			Object value;
			try {
				value = field.get(instance);
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				throw new IllegalStateException("Problem invoking " + annotationClass + " : " + field, ex);
			}
			if (value != null) {
				invokeMethodWithAnnotation(value.getClass(), value, annotationClass);
			}
			field.setAccessible(wasAccesible);
		}
		invokeMethodWithAnnotation(instance.getClass(), instance, annotationClass);
	}
    
    
	public static com.google.inject.Injector getGuiceInjector() {
    	if (guiceInjector == null) {
			List<Module> modules = new ArrayList<>(guiceModules);
			modules.add(new JuiceFXModule(configurator));
			guiceInjector = Guice.createInjector(modules);
    	}
    	return guiceInjector;
    }
    
	public static void setGuiceModules(Module... modules) {
		guiceModules.clear();
		if (modules != null && modules.length > 0) {
			guiceModules.addAll(Arrays.asList(modules));
		}
	}

	/**
	 * Sets external previous created guice Injector. If you want to add more
	 * Modules you must call <code>setGuiceModules</code> before this. A new
	 * injector, child of the param, whill by created as
	 * injector.createChildInjector.
	 * 
	 * @param injector the previously created guice Injector.
	 */
	public static void setGuiceInjector(com.google.inject.Injector injector) {
		List<Module> modules = new ArrayList<>(guiceModules);
		modules.add(new JuiceFXModule(configurator));
		guiceInjector = injector.createChildInjector(modules);
	}

    public static Consumer<String> getDefaultLogger() {
        return l -> {
        };
    }
}
