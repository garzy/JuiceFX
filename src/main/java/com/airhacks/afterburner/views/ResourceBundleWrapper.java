package com.airhacks.afterburner.views;

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
