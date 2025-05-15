package com.example.demo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.ArticuloModel;

@Repository
public interface ArticuloRepository extends CrudRepository<ArticuloModel, Long>{
    
}
