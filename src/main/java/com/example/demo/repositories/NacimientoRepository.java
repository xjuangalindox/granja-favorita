package com.example.demo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.MontaModel;
import com.example.demo.models.NacimientoModel;

@Repository
public interface NacimientoRepository extends CrudRepository<NacimientoModel, Long> {

    boolean existsByMontaId(Long id);

    Optional<NacimientoModel> findByMonta(MontaModel montaModel);

    boolean existsById(Long id);

    @Query("SELECT n FROM NacimientoModel n WHERE EXISTS (SELECT e FROM EjemplarModel e WHERE e.nacimiento = n AND e.vendido = false)")
    List<NacimientoModel> findNacimientosConEjemplaresDisponibles();
}
