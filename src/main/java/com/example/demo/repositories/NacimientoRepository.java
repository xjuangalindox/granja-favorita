package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.MontaModel;
import com.example.demo.models.NacimientoModel;

@Repository
public interface NacimientoRepository extends CrudRepository<NacimientoModel, Long> {

    boolean existsByMonta(MontaModel monta);

    Optional<NacimientoModel> findByMonta(MontaModel montaModel);
}
