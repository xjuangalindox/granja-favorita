package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.VentaModel;

@Repository
public interface VentaRepository extends CrudRepository<VentaModel, Long>{
    List<VentaModel> findAllByOrderByFechaEntregaDesc();
}
