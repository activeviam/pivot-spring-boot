/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.configurers.annotation;

import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ActiveViam
 */
@Configuration
public class CustomAutowireConfiguration {

    @Bean
    public CustomAutowireConfigurer autowireConfigurer(DefaultListableBeanFactory beanFactory) {
        CustomAutowireConfigurer configurer = new CustomAutowireConfigurer();
        beanFactory.setAutowireCandidateResolver(new InCubesAutowireCandidateResolver());
        configurer.postProcessBeanFactory(beanFactory);
        return configurer;
    }
}
