package com.example.demo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.MontaModel;

@Repository
public interface MontaRepository extends CrudRepository<MontaModel, Long> {
	boolean existsById(Long id);
}
