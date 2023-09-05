package com.activeviam.apps.cfg.security;

import java.time.Duration;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;

@ConfigurationProperties(SecurityJwtProperties.JWT_PROPERTIES_PREFIX)
@Data
public class SecurityJwtProperties {
    public static final String JWT_PROPERTIES_PREFIX = "activeviam.jwt";
    private static final String PUBLIC_KEY = "public";
    private static final String PRIVATE_KEY = "private";

    @NonNull
    @Getter(AccessLevel.PRIVATE)
    private Map<String, String> key = Map.of(
            PUBLIC_KEY,
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAluessE71V+rxRxMfWs4ZuzzjJs+2mlUuZSF8OvRW2C+QK+/pOsze/SnWiAABGnDkQCQTBz28Vpx3AA2+/3bMqCcTTUWHdTXdZ5jfZsizzwp0xccc9EpvRv2o+h3PnHDXxRDrNaoYQyKXFBj/IJzPLQ1uy9UN+zC6XLlhAHg8f+UHZ6IOVRIa6pS8dYbqjQMEy9htyhNrJrgSgqFOEH7FVQKV8om36ADcaHqEQeAQanWaxaC01xz3vK753q7X7tmE0nYbGGQ4Nmk1QJGT1BFVVeRfd+r1QTcsQRagCekucyo9in2//rF0+yGHHJ9vcpvej3dElS4olNuF/ak12uh9TwIDAQAB",
            PRIVATE_KEY,
            "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCW56ywTvVX6vFHEx9azhm7POMmz7aaVS5lIXw69FbYL5Ar7+k6zN79KdaIAAEacORAJBMHPbxWnHcADb7/dsyoJxNNRYd1Nd1nmN9myLPPCnTFxxz0Sm9G/aj6Hc+ccNfFEOs1qhhDIpcUGP8gnM8tDW7L1Q37MLpcuWEAeDx/5Qdnog5VEhrqlLx1huqNAwTL2G3KE2smuBKCoU4QfsVVApXyibfoANxoeoRB4BBqdZrFoLTXHPe8rvnertfu2YTSdhsYZDg2aTVAkZPUEVVV5F936vVBNyxBFqAJ6S5zKj2Kfb/+sXT7IYccn29ym96Pd0SVLiiU24X9qTXa6H1PAgMBAAECggEAf9N7KlWX6YMwIj6Gfsq6bSpkV8n3KcAh7rRwoRe8QJ/5hd5RN+e2s8gu20D1rkoWbmagX6/hy5P6EWeTdJ5TOdTvurK8zYJ1K34JLu2vsh9vTuEdG6m5nO7dphB+fkvv2hQ8yVZt+uBgDTwnUJ7Dt6v7QFoW38Ik+spk6pru1H4AUtll5ChROQVHAVXP1EhJIGLJPidmM9rc0IrKa+udFXLleROp3o6sPVxspp7uqk9qWp7AJ/Rz8M6mNylNQhecGacK+Cn0gzzxVnNR/bxqA36QNDNCBWaSXfmC2rDyvzUF3ZjKrSsUv7k+gV6vsuE7Arb2cjcY4j4gTyAUNEnvQQKBgQDrln+v7nGGvv3HdFcRXHgI+cePny5T5PaLi2XSak+nIT2HTsebNAU+vXjnNYFh2ryjPkvS4/BMXRwWbL6d6z6E+h+JYXQc/pCFDa2yyJj/AkRqy8/zjET6uTSsugYfRp0ZTxKsyexqCNXooH4jI+YuKNLioARzRJxFMt2UhmNmPwKBgQCj+tnH995WktZ04vNShpB5WbBjiRqU/v7nCx5mFS51YaKkbcyOq12+bXV2VcIlGPyO9+3JFOsTn4goUpLTCxAS8JuJAKu0yHuBtX4aNBX55Ssx+4t4PInUBspO7nBPgMah06FEe/i6b6J1+Z7c5/P/3Z4dbRTGxCfjkhBHgbPE8QKBgF58zReVzciaX5SYj7cx3B7Vd3meAWm6gjuznBIJe4rvpQrYyOvxsEzal1w8NHk3zsK2YJjjvOQT0AkaclVKHZgd7XofMP/UBcinlMwI8nwMv74Joozu7FeW4o6ISZ5PpwCYm0fb8MsSYiDcBds4McC/tN0aCs7kbLzASuigAcvnAoGAf+Wr7xgZRpUIx+ortnZWQQ//T+Mj5Ipu7m3xq9VhgxQ/8tfg8HYgi+J792w0HRM8CZa+1FOIdqm7XRfqhMjgJKWd6mGniz3DdwvD61Qsv9hKtJVp1sIBDmqtaJr45kmeo6GY6v12ppNjt3iWu93+pdaI+JKX3eAo7IqEQAGd4UECgYEAufrUnraq6aZ1dPGR5xfXhVFEWvPVF2jAZ3NRf24klVGKzDixv4WQi9j2y9OiFv9u4DrWM7/y97z51q9plKDg6WYKNFG4uQxWXru61txaTDqXQBLzIvA3YCrsPUB3SFtMVhJ/HYFWSr4lyYf7xPzKh391M2cr9VsObsYrwvWU0sc=");

    private Duration expiration = Duration.ofHours(12);
    private boolean configureLogout = true;
    private boolean failOnDifferentAuthorities;

    public String getPublicKey() {
        return key.get(PUBLIC_KEY);
    }

    public String getPrivateKey() {
        return key.get(PRIVATE_KEY);
    }
}