package gov.goias.smartapi;

import gov.goias.cas.auth.CasUserExtractor;
import org.jasig.cas.client.boot.configuration.CasClientConfigurer;
import org.jasig.cas.client.boot.configuration.EnableCasClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCasClient
public class SmartApiApplication extends SpringBootServletInitializer implements CasClientConfigurer {

    @Bean
    public CasUserExtractor casUserExtractor() {
        return new CasUserExtractor();
    }

    public static void main(String[] args) {
        SpringApplication.run(SmartApiApplication.class, args);
    }

    @Override
    public void configureAuthenticationFilter(FilterRegistrationBean authenticationFilter) {
        authenticationFilter.getInitParameters().put("artifactParameterName", "casTicket");
        authenticationFilter.getInitParameters().put("serviceParameterName", "TARGET");
    }
}
