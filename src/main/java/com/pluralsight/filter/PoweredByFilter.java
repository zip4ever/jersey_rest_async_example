package com.pluralsight.filter;

import com.pluralsight.annotation.PoweredBy;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * Created by KDAAU95 on 20/04/2015.
 */
@Provider
public class PoweredByFilter implements ContainerResponseFilter{

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        for( Annotation annotation : containerResponseContext.getEntityAnnotations()) {
            if(annotation.annotationType() == PoweredBy.class) {
                String value = ((PoweredBy) annotation).value();
                containerResponseContext.getHeaders().add("X-Powered-By", "Pluralsight");
            }
        }

    }
}
