package dev.bicirotas;
//representa o dado que vem do outro serviço
public record AvaliacaoDTO(Long id, Long rotaId, String nomeCiclista, String condicaoAtual, Integer nota) {}