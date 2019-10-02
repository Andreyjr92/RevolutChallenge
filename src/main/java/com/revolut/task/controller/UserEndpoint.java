package com.revolut.task.controller;

import com.revolut.task.dao.UserDAO;
import com.revolut.task.dto.PersonDTO;
import com.revolut.task.exception.UserNotFoundException;
import com.revolut.task.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

/**
 * <p>Endpoint to manage bank account holder</p>
 */
@Path("user")
public class UserEndpoint extends BaseEndpoint {

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean addPerson(PersonDTO personDTO) {
        new UserDAO(personDTO, connectionPool).submit();
        return true;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User getPerson(@QueryParam("id") Long id) {
        Optional<User> personOpt = new UserDAO.Identified(id, connectionPool).get();
        return personOpt.orElseThrow(UserNotFoundException::new);
    }
}
