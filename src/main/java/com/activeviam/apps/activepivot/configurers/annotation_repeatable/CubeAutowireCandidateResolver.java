/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.configurers.annotation_repeatable;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// inspired by https://github.com/Cepr0/sb-tagged-autowire-candidate-resolver
public class CubeAutowireCandidateResolver extends ContextAnnotationAutowireCandidateResolver {

	private static final String VALUE = "value";

	@Override
	public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
		if (super.isAutowireCandidate(bdHolder, descriptor)) {
			return checkCubes(bdHolder, descriptor);
		}
		return false;
	}


	// InCube is a Repeatable annotation: if it is repeated, then we get a InCubes annotation containing multiple InCube values.
	// If there is a single InCube, it will be an InCube
	private boolean checkCubes(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
		var targetAnn = descriptor.getAnnotation(Cube.class);
		if (targetAnn != null) {
			BeanDefinition bd = bdHolder.getBeanDefinition();
			Object source = bd.getSource();

			Set<String> candidateCubes = null;

			if (source instanceof AnnotatedTypeMetadata) {
				// source is not null when a candidate was created with @Bean annotation
				Map<String, Object> attributes =
						((AnnotatedTypeMetadata) source).getAnnotationAttributes(InCubes.class.getName());
				if (attributes != null) {
					candidateCubes = Arrays.stream((AnnotationAttributes[]) attributes.get(VALUE)).map(a -> (String) extractValue(a)).collect(Collectors.toSet());
				} else {
					attributes =
							((AnnotatedTypeMetadata) source).getAnnotationAttributes(Cube.class.getName());
					if (attributes != null) {
						candidateCubes = Stream.of(attributes).map(a -> (String) a.get(VALUE)).collect(Collectors.toSet());
					}
				}
			} else {
				ResolvableType candidateType = bd.getResolvableType();
				// candidateType is not null when candidate was created with @Component like annotation
				Class<?> candidateClass = candidateType.resolve();
				if (candidateClass != null) {
					// If InCube annotation is repeated, it is replaced by "InCubes"
					InCubes cubesAnn = candidateClass.getAnnotation(InCubes.class);
					if (cubesAnn != null) {
						candidateCubes = Arrays.stream(cubesAnn.value()).map(Cube::value).collect(Collectors.toSet());
					} else {
						Cube cubeAnn = candidateClass.getAnnotation(Cube.class);
						if (cubeAnn != null) {
							candidateCubes = Set.of(cubeAnn.value());
						}
					}
				}
			}

			if (candidateCubes != null) {
				return candidateCubes.contains(targetAnn.value());
			} else {
				// If a candidate doesn't have annotation then it's not a suitable candidate
				return false;
			}
		}
		// If target doesn't have annotation then return 'true' as super.isAutowireCandidate() does.
		return true;
	}
}