/*
 * Copyright (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps.kdc;

import org.springframework.security.kerberos.test.MiniKdc;

class KerberosMiniKdc {
	private static final String KRB_WORK_DIR = "krb-test-workdir";

	public static void main(String[] args) throws Exception {
		MiniKdcConfig config = MiniKdcConfig.builder()
				.workDir(KRB_WORK_DIR)
				.confDir("minikdc-krb5.conf")
				.keytabName("local.keytab")
				.principal("client/localhost")
				.principal("HTTP/localhost")
				.build();

		MiniKdc.main(config.asConfig());
	}
}