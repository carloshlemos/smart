/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.goias.smartapi.conf;

import gov.goias.EncodingFilter;
import gov.goias.cas.auth.UserRolesServlet;
import gov.goias.conf.contextlisteners.IgnoreSSLContextListener;
import gov.goias.portal.security.PortalCasRoleBasedAccessControlFilter;
import gov.goias.servlets.CasLogoutServlet;
import org.jasig.cas.client.authentication.Saml11AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Saml11TicketValidationFilter;
import org.owasp.csrfguard.CsrfGuardHttpSessionListener;
import org.owasp.csrfguard.CsrfGuardServletContextListener;
import org.owasp.csrfguard.servlet.JavaScriptServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import javax.servlet.DispatcherType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ednilson.santos
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cas.authfilter.casServerLoginUrl}")
    private String casServerLoginUrl;

    @Value("${cas.casServerUrlPrefix}")
    private String casServerUrl;

    @Value("${cas.client.serverName}")
    private String casClientServer;

    @Value("${owasp.referer.pattern}")
    private String owaspRefererPattern;

    @Value("${cas.logout.url}/logout")
    private String urlCasLogout;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/template/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath,
                                                   Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        return requestedResource.exists() && requestedResource.isReadable() ? requestedResource
                                : new ClassPathResource("/template/index.html");
                    }
                });
    }

//  ********************** Filtros ********************
    @Bean
    @Order(2)
    public EncodingFilter encodingFilter() {
        return new EncodingFilter();
    }

    @Bean
    @Order(2)
    public FilterRegistrationBean encodingFilterBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(encodingFilter());
        filterRegistration.setName("CharacterEncodingFilter");
        filterRegistration.setOrder(2);
        filterRegistration.addUrlPatterns("/*");

        Map<String, String> params = new HashMap<>();
        params.put("requestResponseCharEncoding", "UTF-8");

        filterRegistration.setAsyncSupported(true);
        filterRegistration.setInitParameters(params);

        return filterRegistration;
    }

    @Order(3)
    @Bean(name = "singleSignOutFilter")
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(casServerUrl);
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }

    @Bean
    @Order(3)
    public FilterRegistrationBean singleSignOutFilterBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(singleSignOutFilter());
        filterRegistration.setName("CAS Single Sign Out Filter");
        filterRegistration.setOrder(3);
        filterRegistration.addUrlPatterns("/*");

        Map<String, String> params = new HashMap<>();
        params.put("casServerUrlPrefix", casServerUrl);
        params.put("artifactParameterName", "SAMLart");

        filterRegistration.setAsyncSupported(true);
        filterRegistration.setInitParameters(params);

        return filterRegistration;
    }

    @Bean
    @Order(5)
    public Saml11AuthenticationFilter saml11AuthenticationFilter() {
        return new Saml11AuthenticationFilter();
    }

    @Bean
    @Order(5)
    public FilterRegistrationBean saml11AuthenticationFilterBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(saml11AuthenticationFilter());
        filterRegistration.setName("CAS Authentication Filter");
        filterRegistration.setOrder(5);
        filterRegistration.addUrlPatterns("/app/*", "/api/*");

        Map<String, String> params = new HashMap<>();
        params.put("casServerLoginUrl", casServerLoginUrl);
        params.put("serverName", casClientServer);

        filterRegistration.setInitParameters(params);

        return filterRegistration;
    }

    @Bean
    @Order(6)
    public Saml11TicketValidationFilter saml11TicketValidationFilter() {
        return new Saml11TicketValidationFilter();
    }

    @Bean
    @Order(6)
    public FilterRegistrationBean saml11TicketValidationFilterBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(saml11TicketValidationFilter());
        filterRegistration.setName("CAS Validation Filter");
        filterRegistration.setOrder(6);
        filterRegistration.addUrlPatterns("/app/*", "/api/*", "/login/*");

        Map<String, String> params = new HashMap<>();
        params.put("casServerUrlPrefix", casServerUrl);
        params.put("serverName", casClientServer);
        params.put("properties", "classpath:samlvalidate.properties");
        params.put("tolerance", "300000");

        filterRegistration.setInitParameters(params);

        return filterRegistration;
    }

    @Bean
    @Order(7)
    public PortalCasRoleBasedAccessControlFilter portalCasRoleBasedAccessControlFilter() {
        return new PortalCasRoleBasedAccessControlFilter();
    }

    @Bean
    @Order(7)
    public FilterRegistrationBean portalCasRoleBasedAccessControlFilterBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(portalCasRoleBasedAccessControlFilter());
        filterRegistration.setName("Portal CAS Role Based Access Control Filter");
        filterRegistration.setOrder(7);
        filterRegistration.addUrlPatterns("/app/*", "/api/*");

        Map<String, String> params = new HashMap<>();
        params.put("properties", "classpath:rbac.properties");

        filterRegistration.setInitParameters(params);

        return filterRegistration;
    }

    @Bean
    @Order(8)
    public HttpServletRequestWrapperFilter httpServletRequestWrapperFilter() {
        return new HttpServletRequestWrapperFilter();
    }

    @Bean
    @Order(8)
    public FilterRegistrationBean httpServletRequestWrapperFilterBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(httpServletRequestWrapperFilter());
        filterRegistration.setName("CAS HttpServletRequest Wrapper Filter");
        filterRegistration.setOrder(8);
        filterRegistration.addUrlPatterns("/api/*");

        Map<String, String> params = new HashMap<>();
        params.put("roleAttribute", "ROLES");

        filterRegistration.setInitParameters(params);

        return filterRegistration;
    }

    //**************** Servlet  ***********************
    @Bean
    public ServletRegistrationBean userRolesServlet() {
        ServletRegistrationBean servletRegistration = new ServletRegistrationBean(new UserRolesServlet());
        servletRegistration.setName("UserRolesServlet");
        servletRegistration.setLoadOnStartup(1);
        servletRegistration.addUrlMappings("/userroles");
        return servletRegistration;
    }

    @Bean
    public ServletRegistrationBean casLogout() {
        ServletRegistrationBean servletRegistration = new ServletRegistrationBean(new CasLogoutServlet());
        servletRegistration.setName("CasLogoutServlet");
        servletRegistration.setLoadOnStartup(1);
        servletRegistration.addUrlMappings("/logout");

        Map<String, String> params = new HashMap<>();
        params.put("cas.logout.url", urlCasLogout);
        servletRegistration.setInitParameters(params);

        return servletRegistration;
    }

    //*************** Listener *************
//    @Bean
//    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> singleSignOutHttpSessionListener() {
//        ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> listenerRegBean = new ServletListenerRegistrationBean<>();
//        listenerRegBean.setListener(new SingleSignOutHttpSessionListener());
//        return listenerRegBean;
//    }
//
//    @Bean
//    public ServletListenerRegistrationBean<IgnoreSSLContextListener> ignoreSSLContextListener() {
//        ServletListenerRegistrationBean<IgnoreSSLContextListener> listenerRegBean = new ServletListenerRegistrationBean<>();
//        listenerRegBean.setListener(new IgnoreSSLContextListener());
//        return listenerRegBean;
//    }
}
