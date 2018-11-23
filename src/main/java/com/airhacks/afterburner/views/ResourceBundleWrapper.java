package com.airhacks.afterburner.views;

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


import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * This class is for fix nullpointer error when an fxml has fx:include tag with
 * i18n resources
 * 
 * @author garzy
 * @see https://community.oracle.com/thread/2595439
 * 
 */
public class ResourceBundleWrapper extends ResourceBundle {

	private final ResourceBundle bundle;

	protected ResourceBundleWrapper(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	@Override
	protected Object handleGetObject(String key) {
		return bundle.getObject(key);
	}

	@Override
	public Enumeration<String> getKeys() {
		return bundle.getKeys();
	}

	@Override
	public boolean containsKey(String key) {
		return bundle.containsKey(key);
	}

	@Override
	public Locale getLocale() {
		return bundle.getLocale();
	}

	@Override
	public Set<String> keySet() {
		return bundle.keySet();
	}

}
