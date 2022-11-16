/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.configurers;

import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @author ActiveViam
 */
@Configuration
public class AutowireConfiguration {

    @Bean
    public CustomAutowireConfigurer autowireConfigurer(DefaultListableBeanFactory beanFactory) {
        CustomAutowireConfigurer configurer = new CustomAutowireConfigurer();
        configurer.setCustomQualifierTypes(Set.of(InCubes.class));
        beanFactory.setAutowireCandidateResolver(new InCubesAutowireCandidateResolver());
        configurer.postProcessBeanFactory(beanFactory);
        return configurer;
    }
}
