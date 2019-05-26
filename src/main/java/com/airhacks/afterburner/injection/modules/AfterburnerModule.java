package com.airhacks.afterburner.injection.modules;

/*
 * #%L
 * afterburner.fx
 * %%
 * Copyright (C) 2013 - 2019 Adam Bien
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


import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.inject.Inject;

import com.airhacks.afterburner.configuration.Configurator;
import com.google.inject.AbstractModule;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Essential Afterburner injection features implementation with Google Guice.
 */
public class AfterburnerModule extends AbstractModule {

	private final Configurator configurator;

	protected AfterburnerModule(Configurator configurator) {
		super();
		this.configurator = configurator;
	}

	@Override
	protected void configure() {
		bind(Integer.class).toInstance(0);
		bind(Long.class).toInstance(0l);

		bindListener(Matchers.any(), listener(((type, encounter) -> {
			for (Field field : type.getRawType().getDeclaredFields()) {
				if (field.isAnnotationPresent(Inject.class)) {
					field.setAccessible(true);
					encounter.register(injector(instance -> {
						try {
							Object value = configurator.getProperty(type.getRawType(), field.getName());
							field.set(instance, parse(value, field));
						} catch (@SuppressWarnings("unused") IllegalStateException ex) {
							// continue, no problem, Guice do de rest :)
						} catch (IllegalAccessException e) {
							binder().addError(e);
						}
					}));
				}
			}
		})));
	}

	private static Object parse(Object value, Field field) {

		Class<?> type = field.getType();

		if (type == boolean.class) {
			return Boolean.parseBoolean(value == null ? "false" : value.toString());
		}

		if (type == int.class) {
			return Integer.parseInt(value == null ? "0" : value.toString());
		}

		if (type == long.class) {
			return Long.parseLong(value == null ? "0" : value.toString());
		}

		if (type == String.class) {
			return value;
		}

		if (value == null) {
			throw new IllegalStateException("Not valid implementation in Afterburner for fieldtype: " + type);
		}

		return value;
	}

	private TypeListener listener(BiConsumer<TypeLiteral<?>, TypeEncounter<?>> consumer) {
		return consumer::accept;
	}

	private MembersInjector<Object> injector(Consumer<Object> consumer) {
		return consumer::accept;
	}
}
