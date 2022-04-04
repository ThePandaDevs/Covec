package com.covec.mx.cev.entities.comite;

import com.covec.mx.cev.entities.colonia.Colonia;
import com.covec.mx.cev.entities.usuario.integrante.Integrante;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Comites")
public class Comite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comites")
    private Integer id;

    @OneToMany(mappedBy = "comite", cascade = {CascadeType.ALL})
    private List<Integrante> integrantes;

    @ManyToOne
    @JoinColumn(name = "id_colonia")
    private Colonia colonia;
}