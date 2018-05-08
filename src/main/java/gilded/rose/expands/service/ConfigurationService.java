package gilded.rose.expands.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {

    @Value("${server.port}")
    private int port;

    @Value("${server.ssl.enabled}")
    private boolean sslEnabled;

    @Value("${server.ssl.keyStore}")
    private String sslKeystore;

    @Value("${server.ssl.keyStorePassword}")
    private String sslKeystorePassword;

    @Value("${server.ssl.keyPassword}")
    private String sslKeyPassword;

    @Value("${security.config.client}")
    private String client;

    @Value("${security.config.client.secret}")
    private String secret;

    @Value("${security.config.accessTokenValidSec}")
    private int accessTokenValidSeconds;

    @Value("${security.config.refreshTokenValidSec}")
    private int refreshTokenValidSeconds;

    public String getClient() {
        return client;
    }

    public String getSecret() {
        return secret;
    }

    public int getAccessTokenValidSeconds() {
        return accessTokenValidSeconds;
    }

    public int getRefreshTokenValidSeconds() {
        return refreshTokenValidSeconds;
    }

}
