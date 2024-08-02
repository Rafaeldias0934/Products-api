package com.example.springboot.service;

import com.example.springboot.controllers.ProductController;
import com.example.springboot.dtos.ProductRecordDTO;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Validated
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public ProductModel saveProduct(ProductRecordDTO productRecordDTO) {
        ProductModel productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDTO, productModel);
        return productRepository.save(productModel);
    }

    public List<ProductModel> getAllProducts() {
        List<ProductModel> productsList = productRepository.findAll();
        if (!productsList.isEmpty()) {
            for (ProductModel product : productsList) {
                UUID id = product.getIdProject();
                product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            }
        }
        return productsList;
    }

    public ProductModel getOneProduct(UUID id) {
        Optional<ProductModel> getOneProductOpt = productRepository.findById(id);
        if (getOneProductOpt.isEmpty()) {
            return null;
        }
        getOneProductOpt.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Products List"));
        return getOneProductOpt.get();
    }

    public ProductModel updateProduct(UUID id, ProductRecordDTO productRecordDTO){
        Optional<ProductModel> UpdateProductOpt = productRepository.findById(id);
        if (UpdateProductOpt.isPresent()) {
             ProductModel productModel = UpdateProductOpt.get();
            BeanUtils.copyProperties(productRecordDTO, productModel );
            return productRepository.save(productModel);
        }
        return null;
    }

    public boolean delectProduct(UUID id) {
        Optional<ProductModel> delectProductOpt = productRepository.findById(id);
        if (delectProductOpt.isPresent()) {
            productRepository.delete(delectProductOpt.get());
            return true;
        } else {
            return false;
        }
    }

}
