/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.configurers.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.ResolvableType;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InCubesAutowireCandidateResolver extends ContextAnnotationAutowireCandidateResolver {
	@Override
	public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
		if (super.isAutowireCandidate(bdHolder, descriptor)) {
			return checkCubes(bdHolder, descriptor);
		}
		return false;
	}

	private boolean checkCubes(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
		var targetAnn = descriptor.getAnnotation(InCube.class);
		if (targetAnn != null) {
			BeanDefinition bd = bdHolder.getBeanDefinition();
			Object source = bd.getSource();

			String[] candidateCubes = null;

			if (source instanceof AnnotatedTypeMetadata) { // source is not null when a candidate was created with @Bean annotation
				Map<String, Object> attributes =
						((AnnotatedTypeMetadata) source).getAnnotationAttributes(InCube.class.getName());
				if (attributes != null) {
					candidateCubes = (String[]) attributes.get("value");
				}
			} else {
				ResolvableType candidateType = bd.getResolvableType();
				if (candidateType != null) {
					// candidateType is not null when candidate was created with @Component like annotation
					Class<?> candidateClass = candidateType.resolve();
					if (candidateClass != null) {
						InCube cubesAnn = candidateClass.getAnnotation(InCube.class);
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
				// If a candidate doesn't have annotation then it's not a suitable candidate
				return false;
			}
		}
		// If target doesn't have annotation then return 'true' as super.isAutowireCandidate() does.
		return true;
	}
}