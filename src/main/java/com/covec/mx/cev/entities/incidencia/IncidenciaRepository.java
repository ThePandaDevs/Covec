package com.covec.mx.cev.entities.incidencia;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidenciaRepository extends CrudRepository <Incidencia,Integer> {
    
}
