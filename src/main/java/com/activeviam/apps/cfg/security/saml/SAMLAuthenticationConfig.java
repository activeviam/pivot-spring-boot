package com.activeviam.apps.cfg.security.saml;

import com.activeviam.apps.security.IHttpSecurityProcessor;
import com.activeviam.apps.security.saml.SAMLWithRelayStateEntryPoint;
import com.qfs.server.cfg.IJwtConfig;
import com.qfs.server.cfg.impl.ActivePivotConfig;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.saml2.metadata.provider.ResourceBackedMetadataProvider;
import org.opensaml.util.resource.ClasspathResource;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.saml.*;
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.context.SAMLContextProviderLB;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.log.SAMLLogger;
import org.springframework.security.saml.metadata.*;
import org.springframework.security.saml.parser.ParserPoolHolder;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.storage.EmptyStorageFactory;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.*;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.Filter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Configuration
@Import({
        SAMLAuthenticationProviderConfig.class,
        SAMLProperties.class
})
@ConditionalOnProperty(name = "security.type", havingValue = "saml")
public class SAMLAuthenticationConfig {

    // TODO we autowire this to avoid circular dependencies. Could likely be avoided with some refactoring
    @Autowired
    private AuthenticationManager authenticationManager;
    private final SAMLProperties samlProperties;
    private final ApplicationContext applicationContext;

    public SAMLAuthenticationConfig(SAMLProperties samlProperties, ApplicationContext applicationContext) {
        this.samlProperties = samlProperties;
        this.applicationContext = applicationContext;
    }

    @Bean
    public static SAMLBootstrap samlBootstrap() {
        return new SAMLBootstrap();
    }

    @Bean
    public WebSSOProfileOptions defaultWebSSOProfileOptions() {
        WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
        webSSOProfileOptions.setIncludeScoping(false);
        return webSSOProfileOptions;
    }

    @Bean
    public SAMLEntryPoint samlEntryPoint() {
        SAMLEntryPoint samlEntryPoint = new SAMLWithRelayStateEntryPoint();
        samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
        return samlEntryPoint;
    }

    @Bean
    public SAMLLogger samlLogger() {
        return new SAMLDefaultLogger();
    }

    @Bean
    public WebSSOProfile webSSOprofile() {
        return new WebSSOProfileImpl();
    }

    @Bean
    public FilterChainProxy samlFilter() {
        List<SecurityFilterChain> chains = new ArrayList<>();
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"),
                samlWebSSOProcessingFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/discovery/**"),
                samlDiscovery()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"),
                samlEntryPoint()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"),
                samlLogoutFilter()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"),
                samlLogoutProcessingFilter()));
        return new FilterChainProxy(chains);
    }

    @Bean
    public SAMLProcessingFilter samlWebSSOProcessingFilter() {
        SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
        samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager);
        samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(successRedirectHandler());
        samlWebSSOProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        return samlWebSSOProcessingFilter;
    }

    @Bean
    public SAMLDiscovery samlDiscovery() {
        return new SAMLDiscovery();
    }

    @Bean
    public SAMLContextProvider samlContextProvider() throws URISyntaxException {
        URI serverLoadBalancerUri = new URI(samlProperties.getEntityBaseUrl());

        SAMLContextProviderLB samlContextProviderLB = new SAMLContextProviderLB();
        samlContextProviderLB.setScheme(serverLoadBalancerUri.getScheme());
        samlContextProviderLB.setServerName(serverLoadBalancerUri.getHost());
        samlContextProviderLB.setServerPort(serverLoadBalancerUri.getPort());
        samlContextProviderLB.setContextPath(serverLoadBalancerUri.getPath());
        samlContextProviderLB.setIncludeServerPortInRequestURL(true);
        samlContextProviderLB.setStorageFactory(new EmptyStorageFactory());

        return samlContextProviderLB;
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler() {
        return new SAMLRelayStateSuccessHandler();
    }

    @Bean
    public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
        SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
        failureHandler.setUseForward(true);
        failureHandler.setDefaultFailureUrl("/error");
        return failureHandler;
    }

    @Bean
    public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
        SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
        successLogoutHandler.setDefaultTargetUrl("/");
        return successLogoutHandler;
    }

    @Bean
    public SecurityContextLogoutHandler logoutHandler() {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.setInvalidateHttpSession(true);
        logoutHandler.setClearAuthentication(true);
        return logoutHandler;
    }

    @Bean
    public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() {
        return new SAMLLogoutProcessingFilter(successLogoutHandler(), logoutHandler());
    }

    @Bean
    public SAMLLogoutFilter samlLogoutFilter() {
        return new SAMLLogoutFilter(
                successLogoutHandler(),
                new LogoutHandler[]{logoutHandler()},
                new LogoutHandler[]{logoutHandler()}
        );
    }

    @Bean
    public SingleLogoutProfile logoutProfile() {
        return new SingleLogoutProfileImpl();
    }

    @Bean
    public MetadataGenerator metadataGenerator() {
        MetadataGenerator metadataGenerator = new MetadataGenerator();
        metadataGenerator.setEntityId(samlProperties.getEntityId());
        metadataGenerator.setExtendedMetadata(extendedMetadata());
        metadataGenerator.setIncludeDiscoveryExtension(false);
        metadataGenerator.setKeyManager(keyManager());
        metadataGenerator.setEntityBaseURL(samlProperties.getEntityBaseUrl());
        return metadataGenerator;
    }

    @Bean
    public MetadataGeneratorFilter metadataGeneratorFilter() {
        return new MetadataGeneratorFilter(metadataGenerator());
    }

    @Bean
    public ExtendedMetadata extendedMetadata() {
        ExtendedMetadata extendedMetadata = new ExtendedMetadata();
        extendedMetadata.setIdpDiscoveryEnabled(false);
        extendedMetadata.setSigningAlgorithm("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        return extendedMetadata;
    }

    @Bean
    public KeyManager keyManager() {
        SAMLProperties.Keystore properties = samlProperties.getKeystore();
        DefaultResourceLoader loader = new DefaultResourceLoader();
        Resource storeFile = loader.getResource(properties.getLocation());
        Map<String, String> passwords = new HashMap<>();
        passwords.put(properties.getUsername(), properties.getPassword());
        return new JKSKeyManager(storeFile, properties.getPassword(), passwords, properties.getDefaultKey());
    }

    @Bean
    @Qualifier("okta")
    public ExtendedMetadataDelegate oktaExtendedMetadataProvider() throws MetadataProviderException {
        org.opensaml.util.resource.Resource resource = null;
        try {
            resource = new ClasspathResource(samlProperties.getIdpMetadata());
        } catch (ResourceException e) {
            e.printStackTrace();
        }
        Timer timer = new Timer("saml-metadata");
        ResourceBackedMetadataProvider provider = new ResourceBackedMetadataProvider(timer, resource);
        provider.setParserPool(parserPool());
        provider.initialize();
        return new ExtendedMetadataDelegate(provider, extendedMetadata());
    }

    @Bean
    @Qualifier("metadata")
    public CachingMetadataManager metadata() throws MetadataProviderException {
        List<MetadataProvider> providers = new ArrayList<>();
        providers.add(oktaExtendedMetadataProvider());
        return new CachingMetadataManager(providers);
    }

    @Bean(initMethod = "initialize")
    public StaticBasicParserPool parserPool() {
        return new StaticBasicParserPool();
    }

    @Bean(name = "parserPoolHolder")
    public ParserPoolHolder parserPoolHolder() {
        return new ParserPoolHolder();
    }

    @Bean
    public HTTPPostBinding httpPostBinding() {
        return new HTTPPostBinding(parserPool(), VelocityFactory.getEngine());
    }

    @Bean
    public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
        return new HTTPRedirectDeflateBinding(parserPool());
    }

    @Bean
    public SAMLProcessorImpl processor() {
        ArrayList<SAMLBinding> bindings = new ArrayList<>();
        bindings.add(httpRedirectDeflateBinding());
        bindings.add(httpPostBinding());
        return new SAMLProcessorImpl(bindings);
    }

    @Bean
    public WebSSOProfileConsumer webSSOprofileConsumer() {
        WebSSOProfileConsumerImpl webSSOProfileConsumer = new WebSSOProfileConsumerImpl();
        webSSOProfileConsumer.setMaxAuthenticationAge(samlProperties.getMaxAuthenticationAge());
        return webSSOProfileConsumer;
    }

    @Bean
    public WebSSOProfileConsumer hokWebSSOprofileConsumer() {
        return new WebSSOProfileConsumerHoKImpl();
    }

    @Bean
    public IHttpSecurityProcessor httpSecurityConsumer() {
        return new IHttpSecurityProcessor() {

            @Override
            public void preProcess(HttpSecurity http) throws Exception {
                Filter jwtFilter = applicationContext.getBean(IJwtConfig.class).jwtFilter();
                ActivePivotConfig activePivotConfig = applicationContext.getBean(ActivePivotConfig.class);

                http.csrf().disable();

                http.cors();

                http.httpBasic().authenticationEntryPoint(samlEntryPoint());

                http
                        .addFilterBefore(metadataGeneratorFilter(), ChannelProcessingFilter.class)
                        .addFilterAfter(samlFilter(), BasicAuthenticationFilter.class)
                        .addFilterBefore(samlFilter(), CsrfFilter.class)
                        .addFilterAfter(jwtFilter, SecurityContextPersistenceFilter.class)
                        .addFilterAfter(activePivotConfig.contextValueFilter(), SwitchUserFilter.class);

                http
                        .logout()
                        .addLogoutHandler((request, response, authentication) -> {
                            try {
                                response.sendRedirect("/saml/logout");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }

        };
    }

}
