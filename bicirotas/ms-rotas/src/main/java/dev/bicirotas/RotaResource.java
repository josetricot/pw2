package dev.bicirotas;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.Retry;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/rotas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RotaResource {

    @Inject
    @RestClient
    AvaliacaoClient avaliacaoClient;

    @POST
    @Transactional
    public Response criar(Rota rota) {
        rota.persist();
        return Response.status(Response.Status.CREATED).entity(rota).build();
    }

    @GET
    public List<Rota> listar() {
        return Rota.listAll();
    }

    @GET
    @Path("/{id}/detalhes")
    @Timeout(2000) //2 segundos no máximo
    @Retry(maxRetries = 2) // Tenta de novo se falhar
    @Fallback(fallbackMethod = "detalhesFallback") // O Plano B
    public Response detalhes(@PathParam("id") Long id) {
        Rota rota = Rota.findById(id);
        if (rota == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        //aciona o REST Client para buscar as avaliações no outro microsserviço
        List<AvaliacaoDTO> avaliacoes = avaliacaoClient.buscarAvaliacoesDaRota(id);
        
        return Response.ok(new RotaDetalhesDTO(rota, avaliacoes)).build();
    }

    //método que é executado se o ms-avaliacoes estiver fora do ar, plano b
    public Response detalhesFallback(Long id) {
        Rota rota = Rota.findById(id);
        //retorna a rota, mas com uma lista vazia de avaliações, sem quebrar o sistema
        return Response.ok(new RotaDetalhesDTO(rota, List.of())).build();
    }
}