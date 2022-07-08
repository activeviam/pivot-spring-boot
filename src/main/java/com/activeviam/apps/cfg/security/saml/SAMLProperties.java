package com.activeviam.apps.cfg.security.saml;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.saml")
public class SAMLProperties {

    private String entityId;
    private String entityBaseUrl;
    private String idpMetadata;
    private int maxAuthenticationAge;
    private Keystore keystore;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityBaseUrl() {
        return entityBaseUrl;
    }

    public void setEntityBaseUrl(String entityBaseUrl) {
        this.entityBaseUrl = entityBaseUrl;
    }

    public String getIdpMetadata() {
        return idpMetadata;
    }

    public void setIdpMetadata(String idpMetadata) {
        this.idpMetadata = idpMetadata;
    }

    public Keystore getKeystore() {
        return keystore;
    }

    public void setKeystore(Keystore keystore) {
        this.keystore = keystore;
    }

    public int getMaxAuthenticationAge() {
        return maxAuthenticationAge;
    }

    public void setMaxAuthenticationAge(int maxAuthenticationAge) {
        this.maxAuthenticationAge = maxAuthenticationAge;
    }

    public static class Keystore {

        private String location;
        private String username;
        private String password;
        private String defaultKey;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDefaultKey() {
            return defaultKey;
        }

        public void setDefaultKey(String defaultKey) {
            this.defaultKey = defaultKey;
        }

    }

}
