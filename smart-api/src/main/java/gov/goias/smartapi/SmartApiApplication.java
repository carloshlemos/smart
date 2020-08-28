package gov.goias.smartapi;

import gov.goias.cas.auth.CasUserExtractor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySources({
        @PropertySource(value = "classpath:cas-config.properties"),
})
public class SmartApiApplication extends SpringBootServletInitializer {

    @Bean
    public CasUserExtractor casUserExtractor() {
        return new CasUserExtractor();
    }

    public static void main(String[] args) {
        SpringApplication.run(SmartApiApplication.class, args);
    }

}
