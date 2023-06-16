package com.pragma.plazoleta.infrastructure.persistence.plato;

import com.pragma.plazoleta.infrastructure.persistence.categoria.CategoriaData;
import com.pragma.plazoleta.infrastructure.persistence.restaurante.RestauranteData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "plato")
public class PlatoData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String urlImagen;
    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private RestauranteData restaurante;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaData categoria;
}
