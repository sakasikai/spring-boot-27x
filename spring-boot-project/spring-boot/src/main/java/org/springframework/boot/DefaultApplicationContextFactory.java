/*
 * Copyright 2012-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * Default {@link ApplicationContextFactory} implementation that will create an
 * appropriate context for the {@link WebApplicationType}.
 *
 * @author Phillip Webb
 */
class DefaultApplicationContextFactory implements ApplicationContextFactory {

	@Override
	public Class<? extends ConfigurableEnvironment> getEnvironmentType(WebApplicationType webApplicationType) {
		return getFromSpringFactories(webApplicationType, ApplicationContextFactory::getEnvironmentType, null);
	}

	@Override
	public ConfigurableEnvironment createEnvironment(WebApplicationType webApplicationType) {
		return getFromSpringFactories(webApplicationType, ApplicationContextFactory::createEnvironment, null);
	}

	@Override
	public ConfigurableApplicationContext create(WebApplicationType webApplicationType) {
		try {
			return getFromSpringFactories(webApplicationType,
					ApplicationContextFactory::create,
					AnnotationConfigApplicationContext::new);
		}
		catch (Exception ex) {
			throw new IllegalStateException("Unable create a default ApplicationContext instance, "
					+ "you may need a custom ApplicationContextFactory", ex);
		}
	}

	/**
	 * @description: TODO
	 * @author: maiqi
	 * @param webApplicationType
	 * @param action
	 * @param defaultResult 默认值 Supplier ==》AnnotationConfigApplicationContext::new
	 * @return T
	 * @update: 2023/11/11 15:19
	 */
	private <T> T getFromSpringFactories(WebApplicationType webApplicationType,
			BiFunction<ApplicationContextFactory, WebApplicationType, T> action,
			Supplier<T> defaultResult) {

		// 从 META-INF/spring.factories 中检索 ApplicationContextFactory.class 的所有实现类
		for (ApplicationContextFactory candidate : SpringFactoriesLoader.loadFactories(ApplicationContextFactory.class,
				getClass().getClassLoader())) {

			// R apply(T t, U u)
			// ==> BiFunction<ApplicationContextFactory, WebApplicationType, T> action

			// =====> ApplicationContextFactory::create
			// 因为是非static函数‼️，所以是 (T t, U u) -> return r = t.create(u);

			// 绑定情况如下：
			// t 绑定到 ApplicationContextFactory::create 的实例上，
			// u 绑定到 ApplicationContextFactory::create 的参数上
			T result = action.apply(candidate, webApplicationType);
			if (result != null) {
				return result;
			}
		}
		// IF result == null，动用默认值
		return (defaultResult != null) ? defaultResult.get() : null;
	}

}
