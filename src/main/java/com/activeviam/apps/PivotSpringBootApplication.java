package com.activeviam.apps;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import com.activeviam.apps.annotations.ActivePivotApplication;
import com.activeviam.apps.cfg.security.SecurityKerberosProperties;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.kerberos.test.MiniKdc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import lombok.extern.slf4j.Slf4j;

@ActivePivotApplication
@EnableWebMvc
@Slf4j
public class PivotSpringBootApplication {
//    private static final String MINI_KDC_STARTUP = """
//            Standalone MiniKdc Running
//            ---------------------------------------------------
//              Realm           : {}
//              Running at      : {}:{}
//              krb5conf        : {}
//
//              created keytab  : {}
//              with principals : {}
//            ---------------------------------------------------""";


    static {
        System.setProperty("java.security.krb5.conf",
                Paths.get("./krb-test-workdir/krb5.conf").normalize().toAbsolutePath().toString());
//        System.setProperty("sun.security.krb5.debug", "true");
        System.setProperty("http.use.global.creds", "false");
    }

//    @Bean(destroyMethod = "stop")
//    public MiniKdc createEmbeddedKDC(SecurityKerberosProperties securityKerberosProperties) throws Exception {
//        Path dir = Paths.get(securityKerberosProperties.getWorkDir());
//        File directory = dir.normalize().toFile();
//        FileUtils.deleteQuietly(directory);
//        FileUtils.forceMkdir(directory);
//
//        final MiniKdc miniKdc = new MiniKdc(MiniKdc.createConf(), directory);
//        miniKdc.start();
//        File keytabFile = securityKerberosProperties.getKeytabLocation().getFile();
//        String[] principals = new String[] {securityKerberosProperties.getServicePrincipal(), securityKerberosProperties.getUserPrincipal()};
//        miniKdc.createPrincipal(keytabFile, principals);
//        log.info(MINI_KDC_STARTUP, miniKdc.getRealm(), miniKdc.getHost(), miniKdc.getHost(), miniKdc.getKrb5conf(), keytabFile, Arrays.asList(principals));
//            return miniKdc;
//    }

    public static void main(String[] args) {
        SpringApplication.run(PivotSpringBootApplication.class, args);
    }
}
