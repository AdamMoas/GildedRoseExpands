package gilded.rose.expands.security;

import gilded.rose.expands.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
@ComponentScan(basePackages = "gilded.rose.expands")
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private static final String[] AUTHORIZED_GRANT_TYPES;
    private static final String[] AUTHORITIES;

    private ConfigurationService configurationService;

    private TokenStore tokenStore;

    private UserApprovalHandler userApprovalHandler;

    private AuthenticationManager authenticationManager;

    static {
        AUTHORIZED_GRANT_TYPES = new String[]{"password", "authorization_code",
                "refresh_token", "implicit"};
        AUTHORITIES = new String[]{"ROLE_CLIENT", "ROLE_TRUSTED_CLIENT"};
    }

    @Autowired
    public AuthorizationServerConfig(
            ConfigurationService configurationService,
            TokenStore tokenStore,
            UserApprovalHandler userApprovalHandler,
            AuthenticationManager authenticationManager) {
        this.configurationService = configurationService;
        this.tokenStore = tokenStore;
        this.userApprovalHandler = userApprovalHandler;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        String realm = "gildedRoseExpandsRealm";
        oauthServer.realm(realm + "/user");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
                .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient(configurationService.getClient())
                .authorizedGrantTypes(AUTHORIZED_GRANT_TYPES).authorities(AUTHORITIES)
                .scopes("BUY").secret(configurationService.getSecret())
                .accessTokenValiditySeconds(configurationService.getAccessTokenValidSeconds())
                .refreshTokenValiditySeconds(
                        configurationService.getRefreshTokenValidSeconds());
    }
}
