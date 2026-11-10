package dev.bicirotas;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Rota extends PanacheEntity {
    public String nome;
    public String cidade;
    public Double distanciaKm;
    public String nivelDificuldade;
}