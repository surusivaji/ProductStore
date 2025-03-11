package org.siva.productstore.repository;

import java.util.List;

import org.siva.productstore.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
	List<Product> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrBrandContainingIgnoreCase(String ch1, String ch2, String ch3);

}
