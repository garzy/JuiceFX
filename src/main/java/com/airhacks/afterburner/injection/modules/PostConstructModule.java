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


import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Allow usage of import javax.annotation.PostConstruct to be called after Guice
 * Injection. Only the first method width @PostConstruct will be called and
 * doesn't must have parameters
 */
public class PostConstructModule extends AbstractModule {

	public PostConstructModule() {
		super();
	}

	@Override
	protected void configure() {

		bindListener(Matchers.any(), new TypeListener() {
			@Override
			public <I> void hear(final TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
				typeEncounter.register(new InjectionListener<I>() {
					@Override
					public void afterInjection(Object obj) {
						for (Method method : obj.getClass().getDeclaredMethods()) {
							if (method.isAnnotationPresent(PostConstruct.class)) {
								try {
									boolean wasAccesible = method.isAccessible();
									method.setAccessible(true);
									method.invoke(obj);
									method.setAccessible(wasAccesible);
								} catch (Exception ex) {
									throw new IllegalStateException(String.format(
											"Not valid postConstruct method on %s class", obj.getClass().getName()),
											ex);
								}
								// Only allowed the first, then break the loop
								break;
							}
						}
					}
				});
			}
		});
	}
}
