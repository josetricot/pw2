package dev.bicirotas;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Avaliacao extends PanacheEntity {
    //usando campos públicos conforme o padrão simplificado do Panache
    public Long rotaId;
    public String nomeCiclista;
    public String condicaoAtual;
    public Integer nota;
}