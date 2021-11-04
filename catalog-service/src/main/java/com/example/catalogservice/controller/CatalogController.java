package com.example.catalogservice.controller;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.service.CatalogService;
import com.example.catalogservice.vo.ResponseCatalog;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/catalog-service")
public class CatalogController {
    // 1.applicaiton.yml 파일에서 특정한 데이터를 가져올때 쓰는 Environment
    Environment env;
    CatalogService catalogService;

    // 2. 생성자를 통해 주입
    @Autowired
    public CatalogController(CatalogService catalogService,Environment env) {
        this.catalogService = catalogService;
        this.env = env;
    }

    @GetMapping("/health_check")
    public String status(){

        return String.format("It's Working in Catalog Service on PORT %s", env.getProperty("local.server.port"));
    }

    @GetMapping("/catalogs")
    public ResponseEntity<List<ResponseCatalog>> getCatalogs(){
        //1. CatalogEntity 가져오기
        Iterable<CatalogEntity> catalogList = catalogService.getAllCatalogs();

        //2. CatalogEntity -> ResponseCatalog로 변경하기 (반복문을 쓰기에 List로)
        List<ResponseCatalog> result = new ArrayList<>();
        catalogList.forEach(v -> {
            result.add(new ModelMapper().map(v,ResponseCatalog.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
