package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConejoMontaNacimientoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConejoMontaNacimientoApplication.class, args);
	}

}

/*
 * git branch (mostrar rama actual)
 * git branch -a (mostrar ramas locales)
 * git branch -r (mostrar ramas remotas)
 */

/*
 * git switch ramaExistente (cambiar de rama)
 * git switch -c ramaNueva (crear y cambiar de rama)
 */

/*
 * git init
 * git add .
 * git commit -m "Mi mensaje de commit"
 */

/*
 * git remote add origin https://github.com/usuario/nombre-repo.git
 * git push -u origin main
 * git push
 */

/*
 * git push -u origin ramaLocalExistente (subir rama por primera vez)
 * git push (subir rama)
 */

/*
 * git switch main (cambiar a rama principal)
 * git pull (traer ultimos cambios del remoto)
 * 
 * git merge modulo-final-conejo (unir rama a main)
 * git push (subir rama completa)
 * 
 * git branch -d modulo-final-conejo (eliminar rama local)
 * git push origin --delete modulo-final-conejo (eliminar rama remota)
 */