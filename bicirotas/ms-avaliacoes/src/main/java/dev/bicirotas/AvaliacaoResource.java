package dev.bicirotas;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/avaliacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AvaliacaoResource {

    @POST
    @Transactional
    public Response criar(Avaliacao avaliacao) {
        avaliacao.persist();
        return Response.status(Response.Status.CREATED).entity(avaliacao).build();
    }

    @GET
    @Path("/rota/{rotaId}")
    public List<Avaliacao> buscarPorRota(@PathParam("rotaId") Long rotaId) {
        return Avaliacao.find("rotaId", rotaId).list();
    }
}