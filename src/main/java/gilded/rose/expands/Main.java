package gilded.rose.expands;

import gilded.rose.expands.config.SurgeConfig;
import gilded.rose.expands.security.AuthorizationServerConfig;
import gilded.rose.expands.security.OAuth2Config;
import gilded.rose.expands.security.ResourceServerConfig;
import gilded.rose.expands.model.Item;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@ComponentScan(basePackages = {"gilded.rose.expands"})
@EntityScan(basePackageClasses = Item.class)
@SpringBootApplication
@PropertySource("classpath:/environment.properties")
@Import({OAuth2Config.class, ResourceServerConfig.class,
        AuthorizationServerConfig.class, SurgeConfig.class})
public class Main extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}
