package com.pluralsight.mapper;

import com.pluralsight.exception.BookNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by KDAAU95 on 17/04/2015.
 */
@Provider
public class BookNotFoundMapper implements ExceptionMapper<BookNotFoundException> {
    @Override
    public Response toResponse(BookNotFoundException e) {
        return Response.status(404).entity(e.getMessage()).type("text/plain").build();
    }
}
