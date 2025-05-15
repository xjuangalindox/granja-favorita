package com.example.demo.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    

    @Bean
    // Creacion de Bean para no crear multiples instancias
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
