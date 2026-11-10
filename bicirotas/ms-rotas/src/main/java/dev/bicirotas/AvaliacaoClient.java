package dev.bicirotas;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.util.List;

//aponta para a porta do ms-avaliacoes
@RegisterRestClient(baseUri = "http://localhost:8081")
public interface AvaliacaoClient {

    @GET
    @Path("/avaliacoes/rota/{rotaId}")
    List<AvaliacaoDTO> buscarAvaliacoesDaRota(@PathParam("rotaId") Long rotaId);
}