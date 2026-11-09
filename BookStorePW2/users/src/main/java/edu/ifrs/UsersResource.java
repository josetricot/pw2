package edu.ifrs;

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.microprofile.jwt.Claims;

import edu.ifrs.model.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.PermitAll;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
public class UsersResource {

    private static final String ISSUER = "users";

    @POST
    @Path("/getJwt")
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional 
    public String generate(@FormParam("email") String email,
            @FormParam("password") String password) {
        
        User user = User.find("email =?1 AND password =?2", email, password).firstResult(); 
        
        if (user == null) {
            throw new RuntimeException("Invalid email or password");
        }

        return Jwt.issuer(ISSUER)
                .upn(email)
                .groups(new HashSet<>(Arrays.asList("User", "Admin")))
                .claim(Claims.email, user.getEmail())
                .sign();
    }

    @Path ("/insertUser")
    @POST
    @PermitAll
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String insertUser(@FormParam("email") String email,
            @FormParam("password") String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.persist();
        return "User inserted successfully";
    }
}
