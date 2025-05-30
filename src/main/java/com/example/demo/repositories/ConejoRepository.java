package com.example.demo.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.ConejoModel;

import java.util.List;

@Repository
public interface ConejoRepository extends CrudRepository<ConejoModel, Long> {

	//Retornar todos los conejos que contengan "nombre"
	List<ConejoModel> findByNombreContainingIgnoreCase(String nombre);

	//Retornar todos los conejos que contengan "macho" o "hembra"
	List<ConejoModel> findBySexoIgnoreCase(String sexo);

	//Retornar todos los conejos que contengan "nombre" y "macho" o "hembra"
	List<ConejoModel> findByNombreContainingIgnoreCaseAndSexoIgnoreCase(String nombre, String sexo);

	boolean existsByNombre(String nombre);

	boolean existsById(Long id);
	
}
