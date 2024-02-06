/*
 * Copyright (C) ActiveViam 2024
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.cfg.security.dsl.local;

import static com.activeviam.apps.cfg.security.SecurityConstants.AUTHENTICATION_TYPE_PROPERTY;
import static com.activeviam.apps.cfg.security.SecurityConstants.LOCAL_AUTHENTICATION;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * The condition that checks whether the cube is data node.
 *
 * @author ActiveViam
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ConditionalOnProperty(value = AUTHENTICATION_TYPE_PROPERTY, havingValue = LOCAL_AUTHENTICATION)
public @interface ConditionalOnLocalAuthentication {}
