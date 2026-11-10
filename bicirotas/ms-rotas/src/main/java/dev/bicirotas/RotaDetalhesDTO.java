package dev.bicirotas;
import java.util.List;
//junta a Rota com a lista de avaliações
public record RotaDetalhesDTO(Rota rota, List<AvaliacaoDTO> avaliacoes) {}