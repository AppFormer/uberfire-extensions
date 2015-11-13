package org.uberfire.ext.security.server;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CacheHeadersFilter implements Filter {
    static final String EXPIRES_HEADER = "Expires";
    static final String CACHE_CONTROL_HEADER = "Cache-Control";
    static final String PRAGMA_HEADER = "Pragma";
    static final int YEAR_IN_SECONDS = 365 * 24 * 60 * 60;

    @Override
    public void init( final FilterConfig filterConfig ) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter( final ServletRequest request,
                          final ServletResponse response,
                          final FilterChain chain ) throws IOException, ServletException {

        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        final String requestURI = httpServletRequest.getRequestURI();
        
        if ( requestURI.contains( ".cache." ) ) {
            final Calendar calendar = Calendar.getInstance();
            calendar.add( Calendar.SECOND, YEAR_IN_SECONDS );

            httpServletResponse.setHeader( CACHE_CONTROL_HEADER, "max-age=" + YEAR_IN_SECONDS + ", must-revalidate" );
            httpServletResponse.setDateHeader( EXPIRES_HEADER, calendar.getTime().getTime() );
        } 
        else if ( requestURI.endsWith( ".nocache.js" ) || ( requestURI.endsWith( ".html" )) ) {
            httpServletResponse.setHeader( CACHE_CONTROL_HEADER, "no-cache, no-store, must-revalidate" );
            httpServletResponse.setHeader( PRAGMA_HEADER, "no-cache" );
            httpServletResponse.setDateHeader( EXPIRES_HEADER, 0 );
        }

        chain.doFilter( request, response );
    }

}