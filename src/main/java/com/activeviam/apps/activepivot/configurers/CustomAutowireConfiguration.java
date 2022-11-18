/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.configurers;

import com.activeviam.apps.activepivot.configurers.annotation_repeatable.CubeAutowireCandidateResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ActiveViam
 */
@Configuration
@Slf4j
public class CustomAutowireConfiguration {

    //    @Bean
    //    public CustomAutowireConfigurer multivalueAnnotationAutowireConfigurer(DefaultListableBeanFactory beanFactory)
    // {
    //        CustomAutowireConfigurer configurer = new CustomAutowireConfigurer();
    //        beanFactory.setAutowireCandidateResolver(new InCubesAutowireCandidateResolver());
    //        configurer.postProcessBeanFactory(beanFactory);
    //        return configurer;
    //    }

    @Bean
    public CustomAutowireConfigurer repeatableAnnotationAutowireConfigurer(DefaultListableBeanFactory beanFactory) {
        CustomAutowireConfigurer configurer = new CustomAutowireConfigurer();
        beanFactory.setAutowireCandidateResolver(new CubeAutowireCandidateResolver());
        configurer.postProcessBeanFactory(beanFactory);
        return configurer;
    }
}
