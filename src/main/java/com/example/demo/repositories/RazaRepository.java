package com.example.demo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.RazaModel;

@Repository
public interface RazaRepository extends CrudRepository<RazaModel, Long>{
    
}
