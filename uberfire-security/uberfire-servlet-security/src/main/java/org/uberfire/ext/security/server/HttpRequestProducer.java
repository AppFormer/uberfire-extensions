package org.uberfire.ext.security.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;

@ApplicationScoped
public class HttpRequestProducer {

    @Produces
    @RequestScoped
    public HttpServletRequest produceHttpRequest() {
        return ServletSecurityAuthenticationService.getRequestForThread();
    }
}
