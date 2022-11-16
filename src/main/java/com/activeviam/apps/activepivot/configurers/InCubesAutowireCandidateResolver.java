/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.configurers;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class InCubesAutowireCandidateResolver extends QualifierAnnotationAutowireCandidateResolver {
	public InCubesAutowireCandidateResolver() {
		super(InCubes.class);
	}

	@Override
	public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
		if (super.isAutowireCandidate(bdHolder, descriptor)) {
			return checkCubes(bdHolder, descriptor);
		}
		return false;
	}

	private boolean checkCubes(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
		InCubes targetAnn = descriptor.getAnnotation(InCubes.class);
		if (targetAnn != null) {
			BeanDefinition bd = bdHolder.getBeanDefinition();
			Object source = bd.getSource();

			String[] candidateCubes = null;

			if (source instanceof AnnotatedTypeMetadata) { // source is not null when a candidate was created with @Bean annotation
				Map<String, Object> attributes = ((AnnotatedTypeMetadata) source).getAnnotationAttributes(InCubes.class.getName());
				if (attributes != null) {
					candidateCubes = (String[]) attributes.get("value");
				}
			} else {
				ResolvableType candidateType = ((RootBeanDefinition) bd).getResolvableType();
				if (candidateType != null) { // candidateType is not null when candidate was created with @Component like annotation
					Class<?> candidateClass = candidateType.resolve();
					if (candidateClass != null) {
						InCubes cubesAnn = candidateClass.getAnnotation(InCubes.class);
						if (cubesAnn != null) {
							candidateCubes = cubesAnn.value();
						}
					}
				}
			}

			if (candidateCubes != null) {
				List<String> targetCubes = new ArrayList<>(Arrays.asList(targetAnn.value()));
				targetCubes.retainAll(Arrays.asList(candidateCubes));
				return !targetCubes.isEmpty();
			} else {
				// If a candidate doesn't have @Cubes annotation then it's not a suitable candidate
				return false;
			}
		}
		// If target doesn't have @Cubes annotation then return 'true' as super.isAutowireCandidate() does.
		return true;
	}

	@Override
	@Nullable
	public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, @Nullable String beanName) {
		return (isLazy(descriptor) ? buildLazyResolutionProxy(descriptor, beanName) : null);
	}

	protected boolean isLazy(DependencyDescriptor descriptor) {
		for (Annotation ann : descriptor.getAnnotations()) {
			Lazy lazy = AnnotationUtils.getAnnotation(ann, Lazy.class);
			if (lazy != null && lazy.value()) {
				return true;
			}
		}
		MethodParameter methodParam = descriptor.getMethodParameter();
		if (methodParam != null) {
			Method method = methodParam.getMethod();
			if (method == null || void.class == method.getReturnType()) {
				Lazy lazy = AnnotationUtils.getAnnotation(methodParam.getAnnotatedElement(), Lazy.class);
				if (lazy != null && lazy.value()) {
					return true;
				}
			}
		}
		return false;
	}

	protected Object buildLazyResolutionProxy(final DependencyDescriptor descriptor, final @Nullable String beanName) {
		BeanFactory beanFactory = getBeanFactory();
		Assert.state(beanFactory instanceof DefaultListableBeanFactory,
				"BeanFactory needs to be a DefaultListableBeanFactory");
		final DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;

		TargetSource ts = new TargetSource() {
			@Override
			public Class<?> getTargetClass() {
				return descriptor.getDependencyType();
			}
			@Override
			public boolean isStatic() {
				return false;
			}
			@Override
			public Object getTarget() {
				Set<String> autowiredBeanNames = (beanName != null ? new LinkedHashSet<>(1) : null);
				Object target = dlbf.doResolveDependency(descriptor, beanName, autowiredBeanNames, null);
				if (target == null) {
					Class<?> type = getTargetClass();
					if (Map.class == type) {
						return Collections.emptyMap();
					}
					else if (List.class == type) {
						return Collections.emptyList();
					}
					else if (Set.class == type || Collection.class == type) {
						return Collections.emptySet();
					}
					throw new NoSuchBeanDefinitionException(descriptor.getResolvableType(),
							"Optional dependency not present for lazy injection point");
				}
				if (autowiredBeanNames != null) {
					for (String autowiredBeanName : autowiredBeanNames) {
						if (dlbf.containsBean(autowiredBeanName)) {
							dlbf.registerDependentBean(autowiredBeanName, beanName);
						}
					}
				}
				return target;
			}
			@Override
			public void releaseTarget(Object target) {
			}
		};

		ProxyFactory pf = new ProxyFactory();
		pf.setTargetSource(ts);
		Class<?> dependencyType = descriptor.getDependencyType();
		if (dependencyType.isInterface()) {
			pf.addInterface(dependencyType);
		}
		return pf.getProxy(dlbf.getBeanClassLoader());
	}

}