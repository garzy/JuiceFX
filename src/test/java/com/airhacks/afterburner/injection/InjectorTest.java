package com.airhacks.afterburner.injection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/*
 * #%L
 * afterburner.fx
 * %%
 * Copyright (C) 2013 Adam Bien
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;

/**
 *
 * @author adam-bien.com
 */
public class InjectorTest {

    
	@Test
    public void injection() {
        View view = Injector.instantiatePresenter(View.class);
        Boundary boundary = view.getBoundary();
        assertNotNull(boundary);
        assertThat(boundary.getNumberOfInstances(), is(1));
        @SuppressWarnings("unused")
        AnotherView another = Injector.instantiatePresenter(AnotherView.class);
        assertThat(boundary.getNumberOfInstances(), is(1));
    }

    @Test
    public void perInstanceInitialization() {
        View first = Injector.instantiatePresenter(View.class);
        View second = Injector.instantiatePresenter(View.class);
        assertNotSame(first, second);
    }

    @Test
    public void forgetAllPresenters() {
        Presenter first = Injector.instantiatePresenter(Presenter.class);
        Injector.forgetAll();
        Presenter second = Injector.instantiatePresenter(Presenter.class);
        assertNotSame(first, second);
    }

    @Test
    public void injectionContextWithEmptyContext() {
        PresenterWithField withField = Injector.instantiatePresenter(PresenterWithField.class);
        assertNull(withField.getName());
        Injector.forgetAll();
    }

    @Test
    public void injectionContextWithMathingKey() {
        String expected = "hello duke";
        Map<String, Object> injectionContext = new HashMap<>();
        injectionContext.put("name", expected);
        PresenterWithField withField = Injector.instantiatePresenter(PresenterWithField.class, injectionContext::get);
        assertThat(withField.getName(), is(expected));
        Injector.forgetAll();
    }


    @Test
    public void productInitialization() {
        InitializableProduct product = Injector.instantiatePresenter(InitializableProduct.class);
        assertTrue(product.isInitialized());
    }

    @Test
    public void existingPresenterInitialization() {
        InitializableProduct product = Injector.registerExistingAndInject(new InitializableProduct());
        assertTrue(product.isInitialized());
    }

    @Test
    public void productDestruction() {
        DestructibleProduct product = Injector.instantiatePresenter(DestructibleProduct.class);
        assertFalse(product.isDestroyed());
        Injector.forgetAll();
        assertTrue(product.isDestroyed());
    }

    @Test
    public void systemPropertiesInjectionOfExistingProperty() {
        final String expected = "42";
        System.setProperty("shouldExist", expected);
        SystemProperties systemProperties = Injector.injectAndInitialize(new SystemProperties());
        String actual = systemProperties.getShouldExist();
        assertThat(actual, is(expected));
    }

    @Test
    public void systemPropertiesInjectionOfNotExistingProperty() {
        SystemProperties systemProperties = Injector.injectAndInitialize(new SystemProperties());
        String actual = systemProperties.getDoesNotExists();
        assertNull(actual);
    }

    @Test
    public void longInjectionWithCustomProvider() {
        long expected = 42;
        Injector.setConfigurationSource((f) -> expected);
        CustomProperties systemProperties = Injector.injectAndInitialize(new CustomProperties());
        long actual = systemProperties.getNumber();
        assertThat(actual, is(expected));
    }

    @Test
    public void longInjectionOfNotExistingProperty() {
        long expected = 0;
        CustomProperties systemProperties = Injector.injectAndInitialize(new CustomProperties());
        long actual = systemProperties.getNumber();
        assertThat(actual, is(expected));
    }

    @Test
    public void dateInjectionWithCustomProvider() {
        Date expected = new Date();
        Injector.setConfigurationSource((f) -> expected);
        DateProperties systemProperties = Injector.injectAndInitialize(new DateProperties());
        Date actual = systemProperties.getCustomDate();
        assertThat(actual, is(expected));
    }

    @Test
    public void dateInjectionOfNotExistingProperty() {
        DateProperties systemProperties = Injector.injectAndInitialize(new DateProperties());
        Date actual = systemProperties.getCustomDate();
        //java.util.Date is not a primitive, or String. Can be created and injected.
        assertNotNull(actual);
    }
    

    @After
    public void reset() {
        Injector.forgetAll();
    }
}
