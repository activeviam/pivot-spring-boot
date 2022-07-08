package com.activeviam.apps.security.saml;

import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.websso.WebSSOProfileOptions;

/**
 * Custom {@link SAMLEntryPoint} that will set the relay state on SAML requests if a certain URL parameter is present
 * in the request. This allows the user to be redirected to a specific page after logging in (e.g. back to a specific
 * dashboard in the UI).
 */
public class SAMLWithRelayStateEntryPoint extends SAMLEntryPoint {

    @Override
    protected WebSSOProfileOptions getProfileOptions(SAMLMessageContext context, AuthenticationException exception)
            throws MetadataProviderException {
        WebSSOProfileOptions ssoProfileOptions = super.getProfileOptions(context, exception);

        HttpServletRequestAdapter httpServletRequestAdapter =
                (HttpServletRequestAdapter) context.getInboundMessageTransport();

        String myRedirectUrl = httpServletRequestAdapter.getParameterValue("redirectTo");

        if (myRedirectUrl != null) {
            ssoProfileOptions.setRelayState(myRedirectUrl);
        }

        return ssoProfileOptions;
    }

}