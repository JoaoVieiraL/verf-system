package com.verf_system.verfS;

import com.verf_system.verfS.database.entity.FornecedorEntity;
import com.verf_system.verfS.database.repository.IFornecedorRepository;
import com.verf_system.verfS.service.FornecedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;

@SpringBootApplication
@RequiredArgsConstructor
public class VerfSApplication {

    public static void main(String[] args) {
        SpringApplication.run(VerfSApplication.class, args);
    }




}
