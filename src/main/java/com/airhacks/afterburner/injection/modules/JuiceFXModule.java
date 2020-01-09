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


import com.airhacks.afterburner.configuration.Configurator;
import com.google.inject.AbstractModule;

/**
 * Multi module class container for group JuiceFX implemented modules.
 */
public class JuiceFXModule extends AbstractModule {

	private final Configurator configurator;

	public JuiceFXModule(Configurator configurator) {
		super();
		this.configurator = configurator;
	}

	@Override
	protected void configure() {
		install(new PostConstructModule());
		install(new AfterburnerModule(configurator));
	}
}