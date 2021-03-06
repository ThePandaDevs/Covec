package com.covec.mx.cev.entities.operacion;

import com.covec.mx.cev.entities.usuario.Usuario;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "Operaciones")
@Data
@NoArgsConstructor
@NamedStoredProcedureQuery(name = "operaciones", procedureName = "operaciones", parameters = {
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "eaccion", type = String.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "eid_usuario", type = Integer.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "eanterior", type = String.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "eactual", type = String.class)
})
public class Operacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operacion")
    private Integer id;
    @Column(name = "accion")
    private String accion;
    @Column(name = "anterior")
    private String anterior;
    @Column(name = "actual")
    private String actual;
    @Column(name = "fecha_hora")
    private Date fechaHora;

    @ManyToOne
    @JoinColumn(name = "idusuario")
    private Usuario usuario;
}
